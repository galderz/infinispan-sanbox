package org.infinispan.sandbox;

import org.infinispan.container.versioning.NumericVersion;
import org.infinispan.functional.EntryView.ReadEntryView;
import org.infinispan.functional.EntryView.ReadWriteEntryView;
import org.infinispan.functional.FunctionalMap.ReadOnlyMap;
import org.infinispan.functional.FunctionalMap.ReadWriteMap;
import org.infinispan.functional.FunctionalMap.WriteOnlyMap;
import org.infinispan.functional.MetaParam.MetaEntryVersion;
import org.infinispan.functional.Traversable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

public class FunctionalEvalMany {

   public static void main( String[] args ) throws Exception {
      CreateFunctionalMaps.main(args);
      ReadOnlyMap<String, String> readOnlyMap = CreateFunctionalMaps.ro();
      WriteOnlyMap<String, String> writeOnlyMap = CreateFunctionalMaps.wo();
      ReadWriteMap<String, String> readWriteMap = CreateFunctionalMaps.rw();

      // Create some data to be stored
      Map<String, String> entries = new HashMap<>();
      entries.put("key1", "value1");
      entries.put("key2", "value2");
      entries.put("key3", "value3");

      // Use multi-key write-only operation to store each key/value pair and
      // generate version metadata (to keep it simple, version generated is either `1` or `2`)
      CompletableFuture<Void> writeManyFuture = writeOnlyMap.evalMany(entries, (v, writeView) -> {
         NumericVersion version = new NumericVersion((long) (Math.random() * 2 + 1));
         writeView.set(v, new MetaEntryVersion(version));
      });

      Set<String> keys = entries.keySet();

      // Once the write operation completes...
      CompletableFuture<Void> readManyFuture = writeManyFuture.thenAccept(ignore -> {
         // Use a multi-key read-only operation to read all values associated with given keys
         Traversable<String> readViewTraverse = readOnlyMap.evalMany(keys, ReadEntryView::get);
         readViewTraverse.forEach(view -> System.out.printf("Read value: %s%n", view));
      });

      // Once the read operation completes...
      CompletableFuture<Void> removeManyFuture = readManyFuture.thenAccept(ignore -> {
         // Use a multi-key read-write operation to remove each key/value pair
         // and return content exposed as view
         Traversable<ReadWriteEntryView<String, String>> prevTraverse = readWriteMap.evalMany(keys,
            readWriteView -> {
               readWriteView.remove();
               return readWriteView;
            });

         // Collect previous entry views grouping them by metadata version
         Map<MetaEntryVersion, List<ReadWriteEntryView<String, String>>> collected = prevTraverse
            .collect(groupingBy(view -> view.findMetaParam(MetaEntryVersion.class).get()));

         // Print a summary of the grouped set
         System.out.printf("Values removed, grouped by version: %n");
         collected.entrySet().forEach(entry -> {
            String values = entry.getValue().stream().map(ReadEntryView::get).collect(joining(", "));
            System.out.printf("Version=%s -> %s%n", entry.getKey(), values);
         });
      });

      // Wait for the chain of operations to complete
      removeManyFuture.get();
   }

}
