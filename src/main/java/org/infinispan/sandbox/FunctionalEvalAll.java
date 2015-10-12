package org.infinispan.sandbox;

import org.infinispan.commons.api.functional.EntryView.ReadWriteEntryView;
import org.infinispan.commons.api.functional.EntryView.WriteEntryView;
import org.infinispan.commons.api.functional.FunctionalMap.ReadOnlyMap;
import org.infinispan.commons.api.functional.FunctionalMap.ReadWriteMap;
import org.infinispan.commons.api.functional.FunctionalMap.WriteOnlyMap;
import org.infinispan.commons.api.functional.Traversable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.stream.Collectors.joining;

public class FunctionalEvalAll {

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

      // Use multi-key write-only operation to store each key/value pair
      writeOnlyMap.evalMany(entries, (v, writeView) -> writeView.set(v)).thenAccept(ignore -> {
         // Use read-only entries traversable to locate a key/value pair with a particular value
         boolean valueExists = readOnlyMap.entries().anyMatch(view -> view.get().equals("value2"));
         System.out.printf("'value2' exists? %b%n", valueExists);
      }).thenAccept(ignore -> {
         // Use read-only keys traversable to count number of keys that have 'key' prefix
         long numKeysPrefix = readOnlyMap.keys().filter(key -> key.startsWith("key")).count();
         System.out.printf("Number of keys with 'key' as prefix? %d%n", numKeysPrefix);
      }).thenAccept(ignore -> {
         // Use read-write evalAll to replace all values and return latest entry view
         Traversable<ReadWriteEntryView<String, String>> replaceAllTraverse = readWriteMap.evalAll(
            readWriteView -> {
               String prev = readWriteView.get();
               readWriteView.set("new-" + prev);
               return readWriteView;
         });
         // Print out entries after replacing
         String pairs = replaceAllTraverse
            .map(view -> view.key() + "=" + view.get())
            .collect(joining(", "));
         System.out.printf("After replacing, entries contain: %s%n", pairs);
      }).thenCompose(ignore ->
         // Use write-only evalAll to remove all entries, one by one
         writeOnlyMap.evalAll(WriteEntryView::remove)
      ).thenAccept(ignore -> {
         // Use read-only keys traversable to verify that the map is empty
         boolean isEmpty = !readOnlyMap.keys().findAny().isPresent();
         System.out.printf("Is empty? %b", isEmpty);
      }).get();
   }

}
