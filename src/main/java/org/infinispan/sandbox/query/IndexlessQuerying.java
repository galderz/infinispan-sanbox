package org.infinispan.sandbox.query;

import org.infinispan.Cache;
import org.infinispan.affinity.KeyAffinityService;
import org.infinispan.affinity.KeyAffinityServiceFactory;
import org.infinispan.affinity.impl.RndKeyGenerator;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.query.Search;
import org.infinispan.query.SearchManager;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.sandbox.Cluster;

import java.util.List;
import java.util.concurrent.Executors;

public class IndexlessQuerying {

   public static void main(String[] args) throws Exception {
      Cluster.withCluster((cm1, cm2) -> {
         KeyAffinityService<Object> affinity1 = createKeyAffinityService(cm1);
         KeyAffinityService<Object> affinity2 = createKeyAffinityService(cm2);

         Object key1 = affinity1.getKeyForAddress(cm1.getAddress());
         Object key2 = affinity2.getKeyForAddress(cm2.getAddress());

         Cache<Object, String> cache1 = cm1.getCache();
         cache1.put(key1, "Will Burns & John Osborne");
         cache1.put(key2, "Kimberly Palko & John Halbert");

         QueryFactory queryFactory = Search.getQueryFactory(cache1);
         Query query = queryFactory.from(String.class).having("value").like("%Will%")
               .toBuilder().build();
         List<Object> list = query.list();
         System.out.println(list);
      });
   }

   private static KeyAffinityService<Object> createKeyAffinityService(EmbeddedCacheManager cm) {
      return KeyAffinityServiceFactory.newLocalKeyAffinityService(cm.getCache(),
            new RndKeyGenerator(), Executors.newSingleThreadExecutor(), 100);
   }

}
