package org.infinispan.sandbox.client.query;

import junit.framework.Assert;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.Search;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.commons.api.CacheContainerAdmin;
import org.infinispan.commons.configuration.XMLStringConfiguration;
import org.infinispan.protostream.FileDescriptorSource;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.query.dsl.Query;
import org.infinispan.query.dsl.QueryFactory;
import org.infinispan.query.remote.client.MarshallerRegistration;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class CreatePersistentIndex {

   private static final String CUSTOM_ANALYZER_PROTO_SCHEMA = "package sample_bank_account;\n" +
      "/* @Indexed \n" +
      "   @Analyzer(definition = \"standard-with-stop\") */" +
      "message AnalyzerTestEntity {\n" +
      "\t/* @Field(store = Store.YES, analyze = Analyze.YES, analyzer = @Analyzer(definition = \"stemmer\")) */\n" +
      "\toptional string f1 = 1;\n" +
      "\t/* @Field(store = Store.YES, analyze = Analyze.NO, indexNullAs = \"-1\") */\n" +
      "\toptional int32 f2 = 2;\n" +
      "}\n";

   static final String DATA_CACHE_NAME = "test-persistent-indexed";
   private static final String INDEX_LOCKING_CACHE_NAME = "test-persistent-indexed-locking";
   private static final String INDEX_METADATA_CACHE_NAME = "test-persistent-indexed-metadata";
   private static final String INDEX_DATA_CACHE_NAME = "test-persistent-indexed-data";

   public static void main(String[] args) {
      ConfigurationBuilder cfg = new ConfigurationBuilder();
      cfg.marshaller(new ProtoStreamMarshaller());
      RemoteCacheManager cacheContainer = new RemoteCacheManager(cfg.build());

      initProtoSchema(cacheContainer);

      createIndexLockingCache(cacheContainer);
      createIndexMetadataCache(cacheContainer);
      createIndexDataCache(cacheContainer);
      createDataCache(cacheContainer);

      final RemoteCache<String, AnalyzerTestEntity> cache = cacheContainer.getCache(DATA_CACHE_NAME);
      assertNotNull(cache);

      cache.put("analyzed1", new AnalyzerTestEntity("tested 123", 3));
      cache.put("analyzed2", new AnalyzerTestEntity("testing 1234", 3));
      cache.put("analyzed3", new AnalyzerTestEntity("xyz", null));

      final QueryFactory queryFactory = Search.getQueryFactory(cache);
      final Query query = queryFactory
         .create("from sample_bank_account.AnalyzerTestEntity where f1:'test'");
      List<AnalyzerTestEntity> list = query.list();

      System.out.printf("Query returned: %s%n", list);

      assertEquals(2, list.size());
   }

   private static void createDataCache(RemoteCacheManager cacheContainer) {
      String dataCacheName = DATA_CACHE_NAME;

      String xml = String.format(
         "<infinispan>" +
            "<cache-container>" +
               "<distributed-cache name=\"%1$s\">" +
                  "<indexing index=\"LOCAL\">" +
                     "<property name=\"default.indexmanager\">org.infinispan.query.indexmanager.InfinispanIndexManager</property>" +
                     "<property name=\"default.locking_cachename\">%2$s</property>" +
                     "<property name=\"default.metadata_cachename\">%3$s</property>" +
                     "<property name=\"default.data_cachename\">%4$s</property>" +
                  "</indexing>" +
                  "<persistence passivation=\"false\">" +
                     "<file-store " +
                        "shared=\"false\" " +
                        "fetch-state=\"true\" " +
                        "path=\"${jboss.server.data.dir}/datagrid-infinispan/%1$s\"/>" +
                  "</persistence>" +
               "</distributed-cache>" +
            "</cache-container>" +
         "</infinispan>",
         dataCacheName, INDEX_LOCKING_CACHE_NAME, INDEX_METADATA_CACHE_NAME, INDEX_DATA_CACHE_NAME
      );
      System.out.printf("Data cache XML: %n%s%n", prettyXml(xml));

      createCache(dataCacheName, xml, cacheContainer);
   }

   private static void createIndexLockingCache(RemoteCacheManager cacheContainer) {
      final String cacheName = INDEX_LOCKING_CACHE_NAME;

      String xml = indexCacheXml("replicated", false, cacheName);
      System.out.printf("Index locking cache XML: %n%s%n", prettyXml(xml));

       createCache(cacheName, xml, cacheContainer);
   }

   private static void createIndexMetadataCache(RemoteCacheManager cacheContainer) {
      final String cacheName = INDEX_METADATA_CACHE_NAME;

      String xml = indexCacheXml("replicated", true, cacheName);
      System.out.printf("Index metadata cache XML: %n%s%n", prettyXml(xml));

      createCache(cacheName, xml, cacheContainer);
   }

   private static void createIndexDataCache(RemoteCacheManager cacheContainer) {
      String cacheName = INDEX_DATA_CACHE_NAME;

      String xml = indexCacheXml("distributed", false, cacheName);
      System.out.printf("Index metadata cache XML: %n%s%n", prettyXml(xml));

      createCache(cacheName, xml, cacheContainer);
   }

   private static String indexCacheXml(String cacheMode, boolean preload, String cacheName) {
      return String.format(
         "<infinispan>" +
            "<cache-container>" +
               "<%2$s-cache name=\"%1$s\">" +
                  "<indexing index=\"NONE\"/>" +
                  "<persistence passivation=\"false\">" +
                     "<file-store " +
                        "shared=\"false\" " +
                        "fetch-state=\"true\" " +
                        "path=\"${jboss.server.data.dir}/datagrid-infinispan/%4$s\" " +
                        "preload=\"%3$s\" " +
                     "/>" +
                  "</persistence>" +
               "</%2$s-cache>" +
            "</cache-container>" +
         "</infinispan>",
         cacheName, cacheMode, preload, DATA_CACHE_NAME
      );
   }

   private static void createCache(String cacheName, String xml, RemoteCacheManager cacheContainer) {
      cacheContainer.administration().removeCache(cacheName);

      cacheContainer.administration()
         .withFlags(CacheContainerAdmin.AdminFlag.PERMANENT)
         .createCache(cacheName, new XMLStringConfiguration(xml));
   }

   private static String prettyXml(String input) {
      return prettyFormat(input, 2);
   }

   private static String prettyFormat(String input, int indent) {
      try {
         Source xmlInput = new StreamSource(new StringReader(input));
         StringWriter stringWriter = new StringWriter();
         StreamResult xmlOutput = new StreamResult(stringWriter);
         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         transformerFactory.setAttribute("indent-number", indent);
         Transformer transformer = transformerFactory.newTransformer();
         transformer.setOutputProperty(OutputKeys.INDENT, "yes");
         transformer.transform(xmlInput, xmlOutput);
         return xmlOutput.getWriter().toString();
      } catch (Exception e) {
         throw new RuntimeException(e); // simple exception handling, please review it
      }
   }

   static void initProtoSchema(RemoteCacheManager remoteCacheManager) {
      // initialize server-side serialization context
      RemoteCache<String, String> metadataCache = remoteCacheManager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
      metadataCache.put("custom_analyzer.proto", CUSTOM_ANALYZER_PROTO_SCHEMA);
      checkSchemaErrors(metadataCache);

      // initialize client-side serialization context
      try {
         SerializationContext serCtx = ProtoStreamMarshaller.getSerializationContext(remoteCacheManager);
         MarshallerRegistration.registerMarshallers(serCtx);
         serCtx.registerProtoFiles(FileDescriptorSource.fromString("custom_analyzer.proto", CUSTOM_ANALYZER_PROTO_SCHEMA));
         serCtx.registerMarshaller(new AnalyzerTestEntityMarshaller());
      } catch (IOException e) {
         throw new AssertionError(e);
      }
   }

   /**
    * Logs the Protobuf schema errors (if any) and fails the test if there are schema errors.
    */
   private static void checkSchemaErrors(RemoteCache<String, String> metadataCache) {
      if (metadataCache.containsKey(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX)) {
         // The existence of this key indicates there are errors in some files
         String files = metadataCache.get(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX);
         for (String fname : files.split("\n")) {
            String errorKey = fname + ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX;
            System.err.println(String.format(
               "Found errors in Protobuf schema file: %s\n%s\n", fname, metadataCache.get(errorKey))
            );
         }

         Assert.fail("There are errors in the following Protobuf schema files:\n" + files);
      }
   }

}
