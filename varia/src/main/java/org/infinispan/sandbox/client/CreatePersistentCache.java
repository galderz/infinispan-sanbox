package org.infinispan.sandbox.client;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.configuration.XMLStringConfiguration;

public class CreatePersistentCache {

   public static void main(String[] args) {
      String xml =
         "<infinispan>" +
            "<cache-container>" +
               "<local-cache name=\"persistent-file-store\">" +
                  "<persistence passivation=\"false\">" +
                     "<file-store shared=\"false\" fetch-state=\"true\" path=\"${jboss.server.data.dir}/datagrid-infinispan/persistent-file-store\"/>" +
                  "</persistence>" +
               "</local-cache>" +
            "</cache-container>" +
         "</infinispan>";

      final String cacheName = "persistent-file-store";

      RemoteCacheManager cacheContainer = new RemoteCacheManager();

      cacheContainer.administration().removeCache(cacheName);
      cacheContainer.administration().createCache(cacheName, new XMLStringConfiguration(xml));

      final RemoteCache<Object, Object> cache = cacheContainer.getCache(cacheName);
      assert cache != null;
   }

}
