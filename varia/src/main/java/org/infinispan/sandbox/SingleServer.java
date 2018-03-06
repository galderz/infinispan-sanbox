package org.infinispan.sandbox;

import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.server.hotrod.HotRodServer;
import org.infinispan.server.hotrod.configuration.HotRodServerConfigurationBuilder;

public class SingleServer {

   public static void main(String[] args) {
//      {
//         // org.infinispan.commons.CacheConfigurationException: ISPN000433:
//         // A default cache has been requested, but no cache has been set as
//         // default for this container
//         final DefaultCacheManager cacheManager = new DefaultCacheManager();
//         final HotRodServer server = new HotRodServer();
//         server.start(new HotRodServerConfigurationBuilder().build(), cacheManager);
//      }

      {
         final DefaultCacheManager cacheManager = new DefaultCacheManager();
         cacheManager.defineConfiguration("my-default-cache",
            new ConfigurationBuilder().build());

         final HotRodServerConfigurationBuilder serverCfg =
            new HotRodServerConfigurationBuilder();
         serverCfg.defaultCacheName("my-default-cache");

         final HotRodServer server = new HotRodServer();
         server.start(serverCfg.build(), cacheManager);
      }
   }

}
