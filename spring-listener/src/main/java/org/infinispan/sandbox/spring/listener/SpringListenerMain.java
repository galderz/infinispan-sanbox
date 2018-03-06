package org.infinispan.sandbox.spring.listener;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.annotation.ClientCacheEntryCreated;
import org.infinispan.client.hotrod.annotation.ClientListener;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.event.ClientCacheEntryCreatedEvent;
import org.infinispan.spring.starter.remote.InfinispanRemoteConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@EnableAutoConfiguration
public class SpringListenerMain {

   @Configuration
   public static class HotRodConfiguration {

      @Bean
      public InfinispanRemoteConfigurer configuration() {
         return () -> {
            return new ConfigurationBuilder()
               // TODO Does not work: https://issues.jboss.org/browse/ISPN-8919
               // .clientIntelligence(ClientIntelligence.BASIC)
               .build();
         };
      }

   }

   public static void main(String... args) throws InterruptedException {
      ConfigurableApplicationContext context = SpringApplication.run(SpringListenerMain.class, args);

      RemoteCacheManager remoteCacheManager = context.getBean(RemoteCacheManager.class);
      RemoteCache<String, String> cache = remoteCacheManager.getCache("default");
      cache.addClientListener(new EventListener());

      for (int i = 0; i < 10; ++i) {
         cache.put(Long.toString(System.currentTimeMillis() + i), "Yeah, Infinispan is cool!");
      }

      AtomicInteger numOfEntries = new AtomicInteger();
      cache.entrySet().forEach(e -> {
         System.out.println(numOfEntries.getAndIncrement() + " " + e);
      });
      System.out.println("Toal entries: " + numOfEntries.get());

      TimeUnit.HOURS.sleep(1);
   }

   @ClientListener
   public static final class EventListener {

      @ClientCacheEntryCreated
      public void handleCreatedEvent(ClientCacheEntryCreatedEvent e) {
         System.out.println(e);
      }

   }

}
