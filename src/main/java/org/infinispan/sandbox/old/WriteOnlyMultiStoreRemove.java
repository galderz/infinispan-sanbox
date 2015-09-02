package org.infinispan.sandbox.old;

import org.infinispan.commons.api.functional.EntryView;
import org.infinispan.commons.api.functional.FunctionalMap;
import org.infinispan.functional.impl.FunctionalMapImpl;
import org.infinispan.functional.impl.ReadOnlyMapImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class WriteOnlyMultiStoreRemove {
   public static void main( String[] args ) throws Exception {
      CreateWriteOnlyMap.main(args);

      FunctionalMap.WriteOnlyMap<String, String> writeOnlyMap = CreateWriteOnlyMap.woMap;

      Map<String, String> data = new HashMap<>();
      data.put("key1", "value1");
      data.put("key2", "value2");
      data.put("key3", "value3");

      // Store multiple key/value pairs
      CompletableFuture<Void> storeFuture = writeOnlyMap.evalMany(data, (v, writeView) -> writeView.set(v));
      // Log that the writen has completed
      CompletableFuture<Void> storeLogFuture = storeFuture.thenAccept(x ->
         System.out.printf("Stored all values%n"));
      // Remove all key/value pairs
      CompletableFuture<Void> removeFuture = storeLogFuture.thenCompose(x ->
         writeOnlyMap.evalMany(data.keySet(), EntryView.WriteEntryView::remove));
      // Log that the remove has completed
      CompletableFuture<Void> removeLogFuture = removeFuture.thenAccept(x ->
         System.out.printf("Removed all values%n"));

      // Wait for the sequence of events to complete
      removeLogFuture.get();

      FunctionalMapImpl<String, String> functionalMap = FunctionalMapImpl.create(CreateWriteOnlyMap.c);
      FunctionalMap.ReadOnlyMap<String, String> readOnlyMap = ReadOnlyMapImpl.create(functionalMap);
      CompletableFuture<Optional<String>> readFuture1 =
         removeFuture.thenCompose(r -> readOnlyMap.eval("key1", EntryView.ReadEntryView::find));
      System.out.println(readFuture1.get());
   }

}
