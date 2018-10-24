package org.infinispan.sandbox.client.query;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class UsePersistentIndex {

   public static void main(String[] args) {
      ConfigurationBuilder cfg = new ConfigurationBuilder();
      cfg.marshaller(new ProtoStreamMarshaller());
      RemoteCacheManager cacheContainer = new RemoteCacheManager(cfg.build());

      CreatePersistentIndex.initProtoSchema(cacheContainer);

      String dataCacheName = CreatePersistentIndex.DATA_CACHE_NAME;
      final RemoteCache<String, AnalyzerTestEntity> cache = cacheContainer.getCache(dataCacheName);
      assertNotNull(cache);

      final QueryFactory queryFactory = Search.getQueryFactory(cache);
      final Query query = queryFactory
         .create("from sample_bank_account.AnalyzerTestEntity where f1:'test'");
      List<AnalyzerTestEntity> list = query.list();

      System.out.printf("Query returned: %s%n", list);

      assertEquals(2, list.size());
   }

}
