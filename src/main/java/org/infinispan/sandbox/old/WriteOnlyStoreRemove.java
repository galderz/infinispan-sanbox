package org.infinispan.sandbox.old;

import org.infinispan.commons.api.functional.EntryView.WriteEntryView;
import org.infinispan.commons.api.functional.FunctionalMap.WriteOnlyMap;
import org.infinispan.commons.api.functional.MetaParam.MetaLifespan;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class WriteOnlyStoreRemove {
   public static void main( String[] args ) throws Exception {
      CreateWriteOnlyMap.main(args);

      WriteOnlyMap<String, String> writeOnlyMap = CreateWriteOnlyMap.woMap;

      String key = "key1", value = "value1";

      // Write a value and metadata to be associated with a key
      CompletableFuture<Void> storeFuture = writeOnlyMap.eval(key, value,
         (v, writeView) -> writeView.set(v, new MetaLifespan(Duration.ofHours(1).toMillis())));
      // Log that the writen has completed
      CompletableFuture<Void> storeLogFuture = storeFuture.thenAccept(x ->
         System.out.printf("Stored value with metadata%n"));
      // Remove the value and metadata associated with key
      CompletableFuture<Void> removeFuture = storeLogFuture.thenCompose(x ->
         writeOnlyMap.eval(key, WriteEntryView::remove));
      // Log that the remove has completed
      CompletableFuture<Void> removeLogFuture = removeFuture.thenAccept(x ->
         System.out.printf("Removed value and metadata%n"));

      // Wait for the sequence of events to complete
      removeLogFuture.get();

//      FunctionalMapImpl<String, String> functionalMap = FunctionalMapImpl.create(CreateWriteOnlyMap.c);
//      FunctionalMap.ReadOnlyMap<String, String> readOnlyMap = ReadOnlyMapImpl.create(functionalMap);
//      CompletableFuture<Optional<String>> readFuture1 =
//         removeFuture.thenCompose(r -> readOnlyMap.eval("key1", EntryView.ReadEntryView::find));
//      System.out.println(readFuture1.get());
   }
}
