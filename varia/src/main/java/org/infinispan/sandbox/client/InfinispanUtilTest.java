package org.infinispan.sandbox.client;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

public class InfinispanUtilTest {

   public static void main(String[] args) {
      InfinispanUtil
         .createRemoteCacheManager()
         .andThenConsume(remote -> {
            final RemoteCache<Object, Object> cache = remote.getCache();
            cache.put("k1", "v1");
            final Object v = cache.get("k1");
            System.out.println(v);
         })
         .accept(new ConfigurationBuilder());

//      InfinispanUtil
//         .createRemoteCacheManager()
//         .andThen(remote -> {
//            final RemoteCache<Object, Object> cache = remote.getCache();
//            cache.put("k1", "v1");
//            return cache;
//         })
//         .andThen(cache -> {
//            final Object v = cache.get("k1");
//            System.out.println(v);
//            return cache;
//         })
//         .apply(new ConfigurationBuilder());
   }

}
