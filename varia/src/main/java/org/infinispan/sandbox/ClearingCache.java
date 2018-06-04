package org.infinispan.sandbox;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

public class ClearingCache {

   public static void main(String[] args) {
      DefaultCacheManager cacheManager = new DefaultCacheManager();
      cacheManager.defineConfiguration("clear-c", new ConfigurationBuilder().build());
      final Cache<Integer, Integer> cache = cacheManager.getCache("clear-c");

      cache.put(1, 1);
      cache.put(2, 1);
      cache.put(3, 1);

      System.out.println(cache.size());

      cache.keySet().stream().forEach(Cache::remove);

      System.out.println(cache.size());
   }

}
