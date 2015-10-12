package org.infinispan.sandbox;

import org.infinispan.commons.api.functional.EntryVersion;
import org.infinispan.commons.api.functional.FunctionalMap;
import org.infinispan.commons.api.functional.MetaParam;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.infinispan.commons.api.functional.EntryVersion.CompareResult.EQUAL;

public class FunctionalVersionCAS {

   public static void main( String[] args ) throws Exception {
      CreateFunctionalMaps.main(args);
      FunctionalMap.ReadOnlyMap<String, String> readOnlyMap = CreateFunctionalMaps.ro;
      FunctionalMap.WriteOnlyMap<String, String> writeOnlyMap = CreateFunctionalMaps.wo;
      FunctionalMap.ReadWriteMap<String, String> readWriteMap = CreateFunctionalMaps.rw;

      CompletableFuture<Void> f0 = writeOnlyMap.eval("key", "value1",
         (v, writeView) -> writeView.set(v, new MetaParam.MetaEntryVersion<>(new EntryVersion.NumericEntryVersion(1))));
      CompletableFuture<Boolean> f1 = f0.thenCompose(x -> readWriteMap.eval("key", "value2", (v, readWriteView) -> {
         // .class does not give generics, so use a helper type() method
         Class<MetaParam.MetaEntryVersion<Long>> clazz = MetaParam.MetaEntryVersion.type();
         Optional<MetaParam.MetaEntryVersion<Long>> metaParam = readWriteView.findMetaParam(clazz);
         return metaParam.map(metaVersion -> {
            if (metaVersion.get().compareTo(new EntryVersion.NumericEntryVersion(1)) == EQUAL) {
               readWriteView.set("uno", new MetaParam.MetaEntryVersion<>(new EntryVersion.NumericEntryVersion(200)));
               return true; // version matches
            }
            return false; // version does not match
         }).orElse(false); // version not present
      }));

      System.out.println(f1.get());
   }

}
