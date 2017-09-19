package org.infinispan.sandbox.old;

import org.infinispan.AdvancedCache;
import org.infinispan.functional.FunctionalMap.WriteOnlyMap;
import org.infinispan.functional.impl.*;
import org.infinispan.manager.DefaultCacheManager;

/**
 * Hello world!
 *
 */
public class CreateWriteOnlyMap  {
   static WriteOnlyMap<String, String> woMap;
   static AdvancedCache<String, String> c;
   public static void main( String[] args ) {
      DefaultCacheManager cacheManager = new DefaultCacheManager();
      AdvancedCache<String, String> cache = cacheManager.<String, String>getCache().getAdvancedCache();
      FunctionalMapImpl<String, String> functionalMap = FunctionalMapImpl.create(cache);
      WriteOnlyMap<String, String> writeOnlyMap = WriteOnlyMapImpl.create(functionalMap);

      //////
      System.out.println(writeOnlyMap != null);
      woMap = writeOnlyMap;
      c = cache;
   }
}
