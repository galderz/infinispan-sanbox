package org.infinispan.sandbox;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;

public class Listeners {

   public static void main(String[] args) throws Exception {
      DefaultCacheManager manager = new DefaultCacheManager();
      manager.defineConfiguration("local", new ConfigurationBuilder().build());
      Cache<Object, Object> cache = manager.getCache("local");

      cache.addListener(new AnyListener<>());
      cache.remove(1);
      Thread.sleep(2000);
   }

   @Listener
   public static class AnyListener<K, V> {

      @CacheEntryRemoved
      public void removed(CacheEntryRemovedEvent<K, V> e) {
         // JCache listeners notified only once, so do it after the event
         if (!e.isPre())
            System.out.printf("Removed: key=%s,value=%s,oldValue=%s%n", e.getKey(), e.getValue(), e.getOldValue());
      }

   }

}
