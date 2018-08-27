package org.infinispan.clusterfailover;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

public class MultiCache {

   public static void main(String[] args) {
      ConfigurationBuilder builder = new ConfigurationBuilder();

      builder
         .addServer().host("localhost").port(11332)
         .addServer().host("localhost").port(11342)
         .addCluster("site2")
            .addClusterNode("localhost", 11432)
            .addClusterNode("localhost", 11442);

      RemoteCacheManager remoteCMgr = new RemoteCacheManager(builder.build());
      RemoteCache<String, String> remotecache = remoteCMgr.getCache("default");
      RemoteCache<String, String> remotecache1 = remoteCMgr.getCache("repl");
      remotecache.put("a", "a");
      remotecache1.put("b", "b");
   }

}
