package org.infinispan.sandbox.client;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.sandbox.client.InfinispanUtil.LazyRemoteCacheManager;

public class InfinispanUtilTest {

   public static void main(String[] args) {
      try (LazyRemoteCacheManager lazyRemote = InfinispanUtil.lazyRemoteCacheManager()) {
         lazyRemote
            .andThen(RemoteCacheManager::getCache)
            .andThen(cache -> {
               cache.put("k1", "v1");
               return cache;
            })
            .andThen(cache -> {
               final Object v = cache.get("k1");
               System.out.println(v);
               return cache;
            })
            .apply(new ConfigurationBuilder());
      }
   }

}
