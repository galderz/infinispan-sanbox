package org.infinispan.sandbox;

import org.infinispan.commons.api.functional.EntryView.ReadEntryView;
import org.infinispan.commons.api.functional.FunctionalMap.ReadOnlyMap;
import org.infinispan.commons.api.functional.FunctionalMap.ReadWriteMap;
import org.infinispan.commons.api.functional.FunctionalMap.WriteOnlyMap;
import org.infinispan.commons.api.functional.MetaParam.MetaLifespan;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class SingleKeyOperations {

   public static void main( String[] args ) throws Exception {
      CreateFunctionalMaps.main(args);
      ReadOnlyMap<String, String> readOnlyMap = CreateFunctionalMaps.ro;
      WriteOnlyMap<String, String> writeOnlyMap = CreateFunctionalMaps.wo;
      ReadWriteMap<String, String> readWriteMap = CreateFunctionalMaps.rw;

      // Write a value and metadata to be associated with a key
      CompletableFuture<Void> writeFuture = writeOnlyMap.eval("key1", "value1",
         (v, writeView) -> writeView.set(v, new MetaLifespan(Duration.ofHours(1).toMillis())));
      // Chain a read operation to happen after the write operation has completed
      CompletableFuture<ReadEntryView<String, String>> readFuture = writeFuture.thenCompose(
         ignore -> readOnlyMap.eval("key1", readView -> readView));
      // Chain an operation to log the value read
      CompletableFuture<Void> logReadFuture = readFuture.thenAccept(
         view -> System.out.printf("Read entry view: %s%n", view));
      // Chain an operation to remove and return previously stored value
      CompletableFuture<String> removeFuture = logReadFuture.thenCompose(
         ignore -> readWriteMap.eval("key1", readWriteView -> {
            String previousValue = readWriteView.get();
            readWriteView.remove();
            return previousValue;
         }));
      // Finally, log the previously stored value returned by the remove
      CompletableFuture<Void> logRemoveFuture = removeFuture.thenAccept(
         previousValue -> System.out.printf("Removed value: %s%n", previousValue));

      // Wait for the chain of operations to complete
      logRemoveFuture.get();
   }

}
