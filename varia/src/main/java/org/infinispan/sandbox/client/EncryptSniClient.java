package org.infinispan.sandbox.client;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

/**
 * To run, copy resources/trust/keystore_server.jks to server configuration
 */
public class EncryptSniClient {

   public static void main(String[] args) {
      ConfigurationBuilder clientBuilder = new ConfigurationBuilder();
      clientBuilder
         .addServer()
            .host("127.0.0.1")
            .port(11222)
         .socketTimeout(3000)
         .security()
            .ssl()
               .enabled(true)
               .trustStoreFileName("src/main/resources/trust/truststore.jks")
               .trustStorePassword("secret".toCharArray())
               .sniHostName("host1")
      ;

      RemoteCacheManager rcm = new RemoteCacheManager(clientBuilder.build());
      RemoteCache<Object, Object> cache = rcm.getCache();
      cache.put(1, "v1");
      System.out.println(cache.get(1));
      rcm.stop();
   }

}
