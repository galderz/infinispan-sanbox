package org.infinispan.sandbox;

import org.infinispan.AdvancedCache;
import org.infinispan.commons.api.functional.FunctionalMap.*;
import org.infinispan.functional.impl.*;
import org.infinispan.manager.DefaultCacheManager;

public class CreateFunctionalMaps {
   static ReadOnlyMap<String, String> ro;
   static WriteOnlyMap<String, String> wo;
   static ReadWriteMap<String, String> rw;
   public static void main( String[] args ) throws Exception {
      DefaultCacheManager cacheManager = new DefaultCacheManager();
      AdvancedCache<String, String> cache = cacheManager.<String, String>getCache().getAdvancedCache();
      FunctionalMapImpl<String, String> functionalMap = FunctionalMapImpl.create(cache);

      ReadOnlyMap<String, String> readOnlyMap = ReadOnlyMapImpl.create(functionalMap);
      WriteOnlyMap<String, String> writeOnlyMap = WriteOnlyMapImpl.create(functionalMap);
      ReadWriteMap<String, String> readWriteMap = ReadWriteMapImpl.create(functionalMap);

      ////////////////////////////////////////////////////////////////////////
      ro = readOnlyMap;
      wo = writeOnlyMap;
      rw = readWriteMap;
   }

   public static <V> ReadOnlyMap<String, V> ro() {
      return (ReadOnlyMap<String, V>) ro;
   }

   public static <V> WriteOnlyMap<String, V> wo() {
      return (WriteOnlyMap<String, V>) wo;
   }

   public static <V> ReadWriteMap<String, V> rw() {
      return (ReadWriteMap<String, V>) rw;
   }

}
