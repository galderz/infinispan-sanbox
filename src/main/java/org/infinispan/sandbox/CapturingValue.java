package org.infinispan.sandbox;

import org.infinispan.commons.api.functional.FunctionalMap.ReadWriteMap;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CapturingValue {
   public static void main( String[] args ) throws Exception {
      CreateFunctionalMaps.main(args);
      ReadWriteMap<String, String> readWriteMap = CreateFunctionalMaps.rw;

      // First, do a put-if-absent like operation
      CompletableFuture<Boolean> putIfAbsentFuture = readWriteMap.eval("key1", readWriteView -> {
         Optional<String> opt = readWriteView.find();
         boolean isAbsent = !opt.isPresent();
         if (isAbsent) readWriteView.set("value1");
         return isAbsent;
      });

      // Chain the put if absent with an operation to log whether the put-if-absent was successful
      CompletableFuture<Void> logPutIfAbsentFuture = putIfAbsentFuture.thenAccept(
         success -> System.out.printf("Put if absent successful? %s%n", success));

      // Next, chain a replace operation comparing a captured value via equals
      String oldValue = "value1";
      CompletableFuture<Boolean> replaceFuture = logPutIfAbsentFuture.thenCompose(x ->
         readWriteMap.eval("key1", "value2", (v, readWriteView) ->
            readWriteView.find().map(prev -> {
               if (prev.equals(oldValue)) {
                  readWriteView.set(v);
                  return true; // Old value matches so set new value and return success
               }
               return false; // Old value does not match
            }).orElse(false) // No value found in the map
      ));

      // Finally, log the result of replace
      CompletableFuture<Void> logReplaceFuture = replaceFuture.thenAccept(
         replaced -> System.out.printf("Replace successful? %s%n", replaced));

      // Wait for the chain of operations to complete
      logReplaceFuture.get();
   }
}
