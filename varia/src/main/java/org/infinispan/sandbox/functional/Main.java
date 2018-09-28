package org.infinispan.sandbox.functional;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

import java.util.function.Consumer;

public class Main {

   public static void main(String[] args) {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder
         .addServer().host("127.0.0.1").port(11222)
         .clientIntelligence(ClientIntelligence.BASIC);

      Consumer<RemoteCacheManager> testOk = remoteCacheManager -> {
         RemoteCache<String, String> cache = remoteCacheManager.getCache();
         cache.put("hello", "world");
         final String value = cache.get("hello");
         System.out.printf("Value is: %s%n", value);
      };

      DataGrid
         .withRemoteCacheManager()
         .andThenConsume(testOk)
         .apply(builder);

      Consumer<RemoteCacheManager> testFail = remoteCacheManager -> {
         System.out.println("Throwing exception...");
         throw new RuntimeException("boo");
      };

      DataGrid
         .withRemoteCacheManager()
         .andThenConsume(testFail)
         .apply(builder);
   }

}
