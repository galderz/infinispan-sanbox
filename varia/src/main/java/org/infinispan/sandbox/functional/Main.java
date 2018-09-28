package org.infinispan.sandbox.functional;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

import java.util.function.Function;

public class Main {

   public static void main(String[] args) {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder
         .addServer().host("127.0.0.1").port(11222)
         .clientIntelligence(ClientIntelligence.BASIC);

      Function<RemoteCacheManager, Void> testOk = remoteCacheManager -> {
         RemoteCache<String, String> cache = remoteCacheManager.getCache();
         cache.put("hello", "world");
         final String value = cache.get("hello");
         System.out.printf("Value is: %s%n", value);

         return null;
      };

      DataGrid
         .withRemoteCacheManager().andThen(testOk)
         .apply(builder);

      Function<RemoteCacheManager, Void> testFail = remoteCacheManager -> {
         System.out.println("Throwing exception...");
         throw new RuntimeException("boo");
      };

      DataGrid
         .withRemoteCacheManager().andThen(testFail)
         .apply(builder);
   }

}
