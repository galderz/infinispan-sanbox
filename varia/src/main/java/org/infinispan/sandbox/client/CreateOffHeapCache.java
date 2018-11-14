package org.infinispan.sandbox.client;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.configuration.XMLStringConfiguration;

import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

public class CreateOffHeapCache {

   public static final Random R = new Random();

   public static void main(String[] args) {
      String xml =
         "<infinispan>" +
            "<cache-container>" +
               "<distributed-cache name=\"ephemeral-off-heap\">" +
                  "<memory>" +
                     "<off-heap/>" +
                  "</memory>" +
               "</distributed-cache>" +
            "</cache-container>" +
         "</infinispan>";

      final String cacheName = "ephemeral-off-heap";

      RemoteCacheManager cacheContainer = new RemoteCacheManager();

      final RemoteCache<UUID, UUID> cache =
         cacheContainer.administration().getOrCreateCache(cacheName, new XMLStringConfiguration(xml));

      IntStream.range(0, 100_000_000)
         .peek(i -> {
            if (i % 10 == 0)
               System.out.printf("Stored %d entries%n", i);
         })
         .forEach(
            i -> cache.put(UUID.randomUUID(), UUID.randomUUID())
         );
   }

}
