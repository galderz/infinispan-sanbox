package org.infinispan.sandbox;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

public class DoubleStartCacheManager {

   public static void main(String[] args) {
      System.out.println("Starting");
      GlobalConfigurationBuilder global = GlobalConfigurationBuilder.defaultClusteredBuilder();
      global.transport()
            .clusterName("discover-service-poc")
            .initialClusterSize(1);

      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.clustering().cacheMode(CacheMode.REPL_SYNC);
      DefaultCacheManager cacheManager =
            new DefaultCacheManager(global.build(), builder.build(), false);

      try {
         System.out.println("Starting cacheManger first time.");
         cacheManager.start();
//         throw new Exception();
      } catch (Exception e) {
         e.printStackTrace();
         cacheManager.stop();
      }


      try {
         System.out.println("Starting cacheManger second time.");
         System.out.println("startAllowed: " + cacheManager.getStatus().startAllowed());
         cacheManager.start();
         System.out.println("Nothing happening because in failed state");
         System.out.println("startAllowed: " + cacheManager.getStatus().startAllowed());
//         throw new Exception();
      } catch (Exception e) {
         e.printStackTrace();
         cacheManager.stop();
      }

      cacheManager = new DefaultCacheManager(global.build(), builder.build(), false);
      cacheManager.start();
   }

}
