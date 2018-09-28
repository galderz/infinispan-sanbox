package org.infinispan.sandbox.functional;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

import java.util.Objects;
import java.util.function.Consumer;

public final class DataGrid {

   private DataGrid() {
   }

   public static Function<ConfigurationBuilder, RemoteCacheManager> withRemoteCacheManager() {
      return createRemoteCacheManager();
   }

   private static Function<ConfigurationBuilder, RemoteCacheManager> createRemoteCacheManager() {
      return new Function<ConfigurationBuilder, RemoteCacheManager>() {
         RemoteCacheManager remoteCacheManager;

         @Override
         public RemoteCacheManager apply(ConfigurationBuilder cfg) {
            System.out.println("Called create");
            this.remoteCacheManager = new RemoteCacheManager(cfg.build());
            return this.remoteCacheManager;
         }

         @Override
         public <V> Function<ConfigurationBuilder, Void> andThenConsume(Consumer<? super RemoteCacheManager> after) {
            Objects.requireNonNull(after);

            return cfg -> {
               try {
                  after.accept(apply(cfg));
                  return null;
               } finally {
                  try {
                     System.out.println("Called destroy");
                     this.remoteCacheManager.stop();
                  } catch (Throwable throwable) {
                     // ignore
                  }
               }
            };
         }

      };
   }

   public interface Function<T, R> extends java.util.function.Function<T, R> {

      default <V> Function<T, Void> andThenConsume(Consumer<? super R> after) {
         Objects.requireNonNull(after);

         return (T t) -> {
            after.accept(apply(t));
            return null;
         };
      }
      
   }

}
