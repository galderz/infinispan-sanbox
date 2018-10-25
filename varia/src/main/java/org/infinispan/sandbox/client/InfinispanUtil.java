package org.infinispan.sandbox.client;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.logging.Log;
import org.infinispan.commons.logging.LogFactory;

import java.util.function.Function;

public class InfinispanUtil {

   private static final Log log = LogFactory.getLog(InfinispanUtil.class);

   private InfinispanUtil() {
   }

   public static LazyRemoteCacheManager lazyRemoteCacheManager() {
      return new AutoCloseableFunctionImpl();
   }

   private static final class AutoCloseableFunctionImpl implements LazyRemoteCacheManager {

      RemoteCacheManager remoteCacheManager;

      @Override
      public RemoteCacheManager apply(ConfigurationBuilder cfg) {
         System.out.println("Called create");
         this.remoteCacheManager = new RemoteCacheManager(cfg.build());
         return this.remoteCacheManager;
      }

      @Override
      public void close() {
         System.out.println("Calling stop...");
         remoteCacheManager.stop();
         System.out.println("Stopped");
      }

   }

   public interface LazyRemoteCacheManager extends Function<ConfigurationBuilder, RemoteCacheManager>, AutoCloseable {

      @Override
      void close();

   }
   
}
