package org.infinispan.clusterfailover;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

public class Main {

   public static void main(String[] args) {
      ConfigurationBuilder builder = new ConfigurationBuilder();

      builder
         .addServer()
            .host("localhost").port(11332)
         .addServer()
            .host("localhost").port(11342)
         .addCluster("site2")
            .addClusterNode("localhost", 11432)
            .addClusterNode("localhost", 11442);

      RemoteCacheManager remote = new RemoteCacheManager(builder.build());

      RemoteCache<String, String> cache = remote.getCache();
      cache.put("a", "a");
   }

}
