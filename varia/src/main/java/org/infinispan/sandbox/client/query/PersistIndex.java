package org.infinispan.sandbox.client.query;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.api.CacheContainerAdmin;
import org.infinispan.commons.configuration.XMLStringConfiguration;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

public class PersistIndex {

   public static void main(String[] args) {
      String dataCacheName = "persistent-data";

      RemoteCacheManager cacheContainer = new RemoteCacheManager();

      createIndexLockingCache(cacheContainer);
      createIndexMetadataCache(cacheContainer);
      createIndexDataCache(cacheContainer);
      createDataCache(dataCacheName, cacheContainer);

      
   }

   private static void createDataCache(String dataCacheName, RemoteCacheManager cacheContainer) {
      String xml = String.format(
         "<infinispan>" +
            "<cache-container>" +
               "<distributed-cache name=\"%1$s\">" +
                  "<indexing index=\"LOCAL\">" +
                     "<property name=\"default.indexmanager\">org.infinispan.query.indexmanager.InfinispanIndexManager</property>" +
                     "<property name=\"default.metadata_cachename\">persistent-indexed-metadata</property>" +
                     "<property name=\"default.data_cachename\">persistent-indexed-data</property>" +
                     "<property name=\"default.locking_cachename\">persistent-indexed-locking</property>" +
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
         dataCacheName
      );
      System.out.printf("Data cache XML: %n%s%n", prettyXml(xml));

      createCache(dataCacheName, xml, cacheContainer);
   }

   private static void createIndexLockingCache(RemoteCacheManager cacheContainer) {
      String cacheName = "persistent-index-locking";

      String xml = indexCacheXml("replicated", false, cacheName);
      System.out.printf("Index locking cache XML: %n%s%n", prettyXml(xml));

       createCache(cacheName, xml, cacheContainer);
   }

   private static void createIndexMetadataCache(RemoteCacheManager cacheContainer) {
      String cacheName = "persistent-index-metadata";

      String xml = indexCacheXml("replicated", true, cacheName);
      System.out.printf("Index metadata cache XML: %n%s%n", prettyXml(xml));

      createCache(cacheName, xml, cacheContainer);
   }

   private static void createIndexDataCache(RemoteCacheManager cacheContainer) {
      String cacheName = "persistent-index-data";

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
                        "path=\"${jboss.server.data.dir}/datagrid-infinispan/%1$s\" " +
                        "preload=\"%3$s\" " +
                     "/>" +
                  "</persistence>" +
               "</%2$s-cache>" +
            "</cache-container>" +
         "</infinispan>",
         cacheName, cacheMode, preload
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

}
