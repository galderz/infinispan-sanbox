package org.infinispan.sandbox.client;

import io.netty.buffer.ByteBuf;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.annotation.ClientListener;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.event.impl.ClientListenerNotifier;
import org.infinispan.client.hotrod.impl.RemoteCacheImpl;
import org.infinispan.client.hotrod.impl.operations.AddClientListenerOperation;
import org.infinispan.client.hotrod.impl.operations.OperationsFactory;
import org.infinispan.client.hotrod.impl.protocol.Codec;
import org.infinispan.client.hotrod.impl.protocol.Codec28;
import org.infinispan.client.hotrod.impl.protocol.CodecFactory;
import org.infinispan.client.hotrod.impl.protocol.HeaderParams;
import org.infinispan.client.hotrod.impl.transport.netty.ChannelFactory;

import java.lang.annotation.Annotation;
import java.util.Set;

import static org.infinispan.client.hotrod.impl.Util.await;

public class ClientListenerParamOverride {

   public static void main(String[] args) {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.addServer().host("127.0.0.1");

      final ExtendedRemoteCacheManager remote =
         new ExtendedRemoteCacheManager(builder.build());

      final RemoteCache<String, String> cache = remote.getCache("namedCache");
      cache.put("k1", "v1");
      System.out.println(cache.get("k1"));

      final ExtendedRemoteCache<String, String> extendedCache = remote.getExtendedCache("namedCache");
      ClientListener listenerParams = new ClientListener() {
         @Override
         public Class<? extends Annotation> annotationType() {
            return ClientListener.class;
         }

         @Override
         public String filterFactoryName() {
            return "c";
         }

         @Override
         public String converterFactoryName() {
            return "d";
         }

         @Override
         public boolean useRawData() {
            return false;
         }

         @Override
         public boolean includeCurrentState() {
            return false;
         }
      };

      extendedCache.addClientListener(
         new ParameterListener(), null, null, listenerParams);
   }

   @ClientListener // annotation required but config values inside ignored
   private static final class ParameterListener {
   }

   static class ExtendedRemoteCacheManager extends RemoteCacheManager {

      public ExtendedRemoteCacheManager(Configuration configuration) {
         super(configuration);
      }

      public <K, V> ExtendedRemoteCache<K, V> getExtendedCache(String cacheName) {
         final Configuration cfg = getConfiguration();
         final RemoteCache<K, V> remoteCache = super.getCache(cacheName);
         return new ExtendedRemoteCache(remoteCache, cacheName, cfg, channelFactory, listenerNotifier);
      }

   }

   static class ExtendedRemoteCache<K, V> extends RemoteCacheImpl<K, V> {

      final Configuration cfg;
      final ChannelFactory channelFactory;
      final ClientListenerNotifier listenerNotifier;
      final RemoteCache delegate;

      public ExtendedRemoteCache(RemoteCache delegate, String name
            , Configuration cfg
            , ChannelFactory channelFactory
            , ClientListenerNotifier listenerNotifier
      ) {
         super(delegate.getRemoteCacheManager(), name);
         this.cfg = cfg;
         this.channelFactory = channelFactory;
         this.listenerNotifier = listenerNotifier;
         this.delegate = delegate;
      }

      void addClientListener(Object listener
         , Object[] filterFactoryParams
         , Object[] converterFactoryParams
         , ClientListener overrides
      ) {
         assertRemoteCacheManagerIsStarted();
         byte[][] marshalledFilterParams = marshallParams(filterFactoryParams);
         byte[][] marshalledConverterParams = marshallParams(converterFactoryParams);

         final OperationsFactory operationsFactory =
            new OperationsFactory(channelFactory, getName(), false
               , new ExtendedCodec28(overrides, CodecFactory.getCodec(cfg.version()))
               , listenerNotifier, cfg);

         AddClientListenerOperation op = operationsFactory.newAddClientListenerOperation(
            listener, marshalledFilterParams, marshalledConverterParams,
            delegate.getDataFormat());
         // No timeout: transferring initial state can take a while, socket timeout setting is not applicable here
         await(op.execute());
      }


      // Duplicate
      private byte[][] marshallParams(Object[] params) {
         if (params == null)
            return new byte[0][];

         byte[][] marshalledParams = new byte[params.length][];
         for (int i = 0; i < marshalledParams.length; i++) {
            byte[] bytes = keyToBytes(params[i]);// should be small
            marshalledParams[i] = bytes;
         }

         return marshalledParams;
      }

   }

   static class ExtendedCodec28 extends Codec28 {

      final ClientListener overrides;
      final Codec delegate;

      ExtendedCodec28(ClientListener overrides, Codec delegate) {
         this.overrides = overrides;
         this.delegate = delegate;
      }

      @Override
      public void writeClientListenerParams(ByteBuf buf, ClientListener ignore, byte[][] filterFactoryParams, byte[][] converterFactoryParams) {
         delegate.writeClientListenerParams(buf, overrides, filterFactoryParams, converterFactoryParams);
      }

      @Override
      public void writeClientListenerInterests(ByteBuf buf, Set<Class<? extends Annotation>> classes) {
         delegate.writeClientListenerInterests(buf, classes);
      }

      @Override
      public HeaderParams writeHeader(ByteBuf buf, HeaderParams params) {
         return delegate.writeHeader(buf, params);
      }

   }

}
