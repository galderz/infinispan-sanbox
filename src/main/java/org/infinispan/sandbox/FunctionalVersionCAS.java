package org.infinispan.sandbox;

import org.infinispan.container.versioning.NumericVersion;
import org.infinispan.functional.FunctionalMap;
import org.infinispan.functional.MetaParam;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.infinispan.container.versioning.InequalVersionComparisonResult.EQUAL;

public class FunctionalVersionCAS {

   public static void main( String[] args ) throws Exception {
      CreateFunctionalMaps.main(args);
      FunctionalMap.ReadOnlyMap<String, String> readOnlyMap = CreateFunctionalMaps.ro;
      FunctionalMap.WriteOnlyMap<String, String> writeOnlyMap = CreateFunctionalMaps.wo;
      FunctionalMap.ReadWriteMap<String, String> readWriteMap = CreateFunctionalMaps.rw;

      CompletableFuture<Void> f0 = writeOnlyMap.eval("key", "value1",
         (v, writeView) -> writeView.set(v, new MetaParam.MetaEntryVersion(new NumericVersion(1))));
      CompletableFuture<Boolean> f1 = f0.thenCompose(x -> readWriteMap.eval("key", "value2", (v, readWriteView) -> {
         // .class does not give generics, so use a helper type() method
         Class<MetaParam.MetaEntryVersion> clazz = MetaParam.MetaEntryVersion.class;
         Optional<MetaParam.MetaEntryVersion> metaParam = readWriteView.findMetaParam(clazz);
         return metaParam.map(metaVersion -> {
            if (metaVersion.get().compareTo(new NumericVersion(1)) == EQUAL) {
               readWriteView.set("uno", new MetaParam.MetaEntryVersion(new NumericVersion(200)));
               return true; // version matches
            }
            return false; // version does not match
         }).orElse(false); // version not present
      }));

      System.out.println(f1.get());
   }

}
