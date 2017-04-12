package org.infinispan.sandbox.client;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;

public class PlainAuthClient {

   public static void main(String[] args) {
      RemoteCacheManager client = new RemoteCacheManager();
      RemoteCache<String, String> cache = client.getCache("___protobuf_metadata");
      System.out.println(cache);
   }

}
