package org.infinispan.sandbox.functional;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

import java.util.Objects;
import java.util.function.Function;

public final class DataGrid {

   private DataGrid() {
   }

   public static Function<ConfigurationBuilder, RemoteCacheManager> withRemoteCacheManager() {
      Function<ConfigurationBuilder, RemoteCacheManager> createFun =
         createRemoteCacheManager();

      return createFun;
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
         public <V> Function<ConfigurationBuilder, V> andThen(Function<? super RemoteCacheManager, ? extends V> after) {
            Objects.requireNonNull(after);

            return cfg -> {
               try {
                  return after.apply(apply(cfg));
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

}
