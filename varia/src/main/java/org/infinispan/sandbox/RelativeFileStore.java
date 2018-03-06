package org.infinispan.sandbox;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

public class RelativeFileStore {

   public static void main(String[] args) {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.persistence().addSingleFileStore()
         .location("~/TMP/file");

      DefaultCacheManager cm = new DefaultCacheManager(builder.build());
      Cache<Object, Object> cache = cm.getCache();

      cache.put(1, "v1");
      cache.evict(1);
      System.out.println(cache.get(1));
   }

}
