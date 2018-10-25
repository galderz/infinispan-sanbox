package org.infinispan.sandbox.client;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.logging.Log;
import org.infinispan.commons.logging.LogFactory;

import java.util.Objects;
import java.util.function.Consumer;

public class InfinispanUtil {

   private static final Log log = LogFactory.getLog(InfinispanUtil.class);

   private InfinispanUtil() {
   }

   public static Function<ConfigurationBuilder, RemoteCacheManager> createRemoteCacheManager() {
      return new Function<ConfigurationBuilder, RemoteCacheManager>() {

         RemoteCacheManager remoteCacheManager;

         @Override
         public RemoteCacheManager apply(ConfigurationBuilder cfg) {
            System.out.println("Called create");
            this.remoteCacheManager = new RemoteCacheManager(cfg.build());
            return this.remoteCacheManager;
         }

         @Override
         public Consumer<ConfigurationBuilder> andThenConsume(Consumer<? super RemoteCacheManager> after) {
            Objects.requireNonNull(after);

            return cfg -> {
               try {
                  after.accept(apply(cfg));
               } catch (Throwable t) {
                  System.err.println("Unexpected exception");
                  t.printStackTrace();
                  throw t;
               } finally {
                  try {
                     System.out.println("Stopping remote cache manager");
                     this.remoteCacheManager.stop();
                     System.out.println("Stopped remote cache manager");
                  } catch (Throwable throwable) {
                     // ignore
                  }
               }
            };
         }

      };
   }

   public interface Function<T, R> extends java.util.function.Function<T, R> {

      default Consumer<T> andThenConsume(Consumer<? super R> after) {
         Objects.requireNonNull(after);

         return (T t) -> {
            after.accept(apply(t));
         };
      }

   }
   
}
