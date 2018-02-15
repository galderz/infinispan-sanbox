package org.infinispan.sandbox.client;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AsyncClient {

   public static void main(String[] args) throws ExecutionException, InterruptedException {
      //API entry point, by default it connects to localhost:11222
      RemoteCacheManager cacheContainer = new RemoteCacheManager();

      //obtain a handle to the remote default cache
      RemoteCache<String, String> cache = cacheContainer.getCache();

      //now add something to the cache and make sure it is there
      cache
         .putAsync("car", "ferrari")
         .thenCompose(prev -> cache.getAsync("car"))
         .thenAccept(v -> {
            System.out.println("Value is " + v);
            assert v.equals("ferrari");
         });

      Thread.sleep(60000);
   }

}
