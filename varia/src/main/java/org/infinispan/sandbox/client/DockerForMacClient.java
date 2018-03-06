package org.infinispan.sandbox.client;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

public class DockerForMacClient {

   public static void main(String[] args) {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder
         .addServer().host("127.0.0.1").port(11222)
         .clientIntelligence(ClientIntelligence.BASIC);

      RemoteCacheManager cacheContainer = new RemoteCacheManager(builder.build());
      RemoteCache<String, String> cache = cacheContainer.getCache();
      cache.put("hello", "world");
      final String value = cache.get("hello");
      System.out.printf("Value is: %s%n", value);
   }

}
