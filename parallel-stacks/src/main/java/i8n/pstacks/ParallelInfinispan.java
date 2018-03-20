package i8n.pstacks;

import org.infinispan.AdvancedCache;
import org.infinispan.Cache;
import org.infinispan.CacheCollection;
import org.infinispan.CacheSet;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.factories.GlobalComponentRegistry;
import org.infinispan.filter.KeyFilter;
import org.infinispan.health.Health;
import org.infinispan.lifecycle.ComponentStatus;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.notifications.cachelistener.filter.CacheEventConverter;
import org.infinispan.notifications.cachelistener.filter.CacheEventFilter;
import org.infinispan.remoting.transport.Address;
import org.infinispan.remoting.transport.Transport;
import org.infinispan.stats.CacheContainerStats;
import org.jboss.modules.LocalModuleLoader;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ParallelInfinispan {

   ModuleLoader moduleLoader;

   public ParallelInfinispan() throws Exception {
      final Path tmpdir = Files.createTempDirectory("repository");
      System.setProperty("maven.repo.local", tmpdir.toAbsolutePath().toString());
      System.setProperty("remote.maven.repo", "http://repository.jboss.org/nexus/content/groups/public/");
      System.setProperty("jboss.modules.system.pkgs", "javax,sun");

      final File repoRoot = ModulesUtil.getResourceFile(StacksMain.class, "test/repo");
      moduleLoader = new LocalModuleLoader(new File[]{repoRoot});
   }

   public EmbeddedCacheManager cacheManager() throws Exception {
      return new ParallelEmbeddedCacheManager();
   };

   private final class ParallelEmbeddedCacheManager implements EmbeddedCacheManager {

      ModuleIdentifier moduleId = ModuleIdentifier.fromString("sample.maven:91");
      final Module module;

      final Object delegate;

      private ParallelEmbeddedCacheManager() throws Exception {
         module = moduleLoader.loadModule(moduleId);

         Thread.currentThread().setContextClassLoader(module.getClassLoader());

         final Class<?> clazz = module.getClassLoader()
            .loadClass("org.infinispan.manager.DefaultCacheManager");

         this.delegate = clazz.newInstance();

         final Object cfgBuilder = newConfigurationBuilder();
         final Object cfg = invokeBuildOnConfigurationBuilder(cfgBuilder);

         final Method[] ms = delegate.getClass().getDeclaredMethods();
         final Method method = Arrays.stream(ms)
            .filter(m ->
               m.getName().equals("defineConfiguration")
                  && m.getGenericParameterTypes().length == 2)
            .findFirst()
            .get();

         method.invoke(delegate, "test", cfg);
      }

      Object newConfigurationBuilder() throws Exception {
         final Class<?> clazz = module.getClassLoader()
            .loadClass("org.infinispan.configuration.cache.ConfigurationBuilder");

         return clazz.newInstance();
      }

      Object invokeBuildOnConfigurationBuilder(Object obj) throws Exception {
         final Class<?> clazz = module.getClassLoader()
            .loadClass("org.infinispan.configuration.cache.ConfigurationBuilder");
         final Method[] ms = clazz.getDeclaredMethods();
         final Method method = Arrays.stream(ms)
            .filter(m ->
               m.getName().equals("build")
                  && m.getGenericParameterTypes().length == 0)
            .findFirst()
            .get();

         return method.invoke(obj);
      }

      @Override
      public Configuration defineConfiguration(String cacheName, Configuration configuration) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public Configuration defineConfiguration(String cacheName, String templateCacheName, Configuration configurationOverride) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public void undefineConfiguration(String configurationName) {
         // TODO: Customise this generated block
      }

      @Override
      public String getClusterName() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public List<Address> getMembers() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public Address getAddress() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public Address getCoordinator() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public boolean isCoordinator() {
         return false;  // TODO: Customise this generated block
      }

      @Override
      public ComponentStatus getStatus() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public GlobalConfiguration getCacheManagerConfiguration() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public Configuration getCacheConfiguration(String name) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public Configuration getDefaultCacheConfiguration() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public Set<String> getCacheNames() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public boolean isRunning(String cacheName) {
         return false;  // TODO: Customise this generated block
      }

      @Override
      public boolean isDefaultRunning() {
         return false;  // TODO: Customise this generated block
      }

      @Override
      public boolean cacheExists(String cacheName) {
         return false;  // TODO: Customise this generated block
      }

      @Override
      public <K, V> Cache<K, V> createCache(String name, Configuration configuration) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public <K, V> Cache<K, V> getCache(String cacheName, boolean createIfAbsent) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public <K, V> Cache<K, V> getCache(String cacheName, String configurationName) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public <K, V> Cache<K, V> getCache(String cacheName, String configurationTemplate, boolean createIfAbsent) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public EmbeddedCacheManager startCaches(String... cacheNames) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public void removeCache(String cacheName) {
         // TODO: Customise this generated block
      }

      @Override
      public Transport getTransport() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public GlobalComponentRegistry getGlobalComponentRegistry() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public void addCacheDependency(String from, String to) {
         // TODO: Customise this generated block
      }

      @Override
      public CacheContainerStats getStats() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public Health getHealth() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public void close() throws IOException {
         // TODO: Customise this generated block
      }

      @Override
      public <K, V> Cache<K, V> getCache() {
         return null;
      }

      @Override
      public <K, V> Cache<K, V> getCache(String cacheName) {
         final Method[] ms = delegate.getClass().getDeclaredMethods();
         final Method method = Arrays.stream(ms)
            .filter(m ->
               m.getName().equals("getCache")
                  && m.getGenericParameterTypes().length == 1)
            .findFirst()
            .get();

         try {
            return new ParallelCache(method.invoke(delegate, cacheName));
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }

      @Override
      public void start() {
         // TODO: Customise this generated block
      }

      @Override
      public void stop() {
         // TODO: Customise this generated block
      }

      @Override
      public void addListener(Object listener) {
         // TODO: Customise this generated block
      }

      @Override
      public void removeListener(Object listener) {
         // TODO: Customise this generated block
      }

      @Override
      public Set<Object> getListeners() {
         return null;  // TODO: Customise this generated block
      }

   }

   private final class ParallelCache implements Cache {

      final Object delegate;

      private ParallelCache(Object delegate) throws Exception {
         this.delegate = delegate;
      }

      @Override
      public void putForExternalRead(Object key, Object value) {
         // TODO: Customise this generated block
      }

      @Override
      public void putForExternalRead(Object key, Object value, long lifespan, TimeUnit unit) {
         // TODO: Customise this generated block
      }

      @Override
      public void putForExternalRead(Object key, Object value, long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit) {
         // TODO: Customise this generated block
      }

      @Override
      public void evict(Object key) {
         // TODO: Customise this generated block
      }

      @Override
      public Configuration getCacheConfiguration() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public EmbeddedCacheManager getCacheManager() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public AdvancedCache getAdvancedCache() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public ComponentStatus getStatus() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public int size() {
         return 0;  // TODO: Customise this generated block
      }

      @Override
      public boolean isEmpty() {
         return false;  // TODO: Customise this generated block
      }

      @Override
      public boolean containsKey(Object key) {
         return false;  // TODO: Customise this generated block
      }

      @Override
      public boolean containsValue(Object value) {
         return false;  // TODO: Customise this generated block
      }

      @Override
      public Object get(Object key) {
         final Method[] ms = delegate.getClass().getDeclaredMethods();
         final Method method = Arrays.stream(ms)
            .filter(m ->
               m.getName().equals("get")
                  && m.getGenericParameterTypes().length == 1)
            .findFirst()
            .get();

         try {
            return method.invoke(delegate, key);
         } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
         }
      }

      @Override
      public CacheSet keySet() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public CacheCollection values() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public CacheSet<Entry> entrySet() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public void clear() {
         // TODO: Customise this generated block
      }

      @Override
      public void start() {
         // TODO: Customise this generated block
      }

      @Override
      public void stop() {
         // TODO: Customise this generated block
      }

      @Override
      public Object putIfAbsent(Object key, Object value) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public boolean remove(Object key, Object value) {
         return false;  // TODO: Customise this generated block
      }

      @Override
      public boolean replace(Object key, Object oldValue, Object newValue) {
         return false;  // TODO: Customise this generated block
      }

      @Override
      public Object replace(Object key, Object value) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public Object computeIfAbsent(Object key, Function mappingFunction) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public Object computeIfPresent(Object key, BiFunction remappingFunction) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public Object compute(Object key, BiFunction remappingFunction) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public Object merge(Object key, Object value, BiFunction remappingFunction) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public String getName() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public String getVersion() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public Object put(Object key, Object value) {
         final Method[] ms = delegate.getClass().getDeclaredMethods();
         final Method method = Arrays.stream(ms)
            .filter(m ->
               m.getName().equals("put")
                  && m.getGenericParameterTypes().length == 2)
            .findFirst()
            .get();

         try {
            return method.invoke(delegate, key, value);
         } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
         }
      }

      @Override
      public Object put(Object key, Object value, long lifespan, TimeUnit unit) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public Object putIfAbsent(Object key, Object value, long lifespan, TimeUnit unit) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public void putAll(Map map, long lifespan, TimeUnit unit) {
         // TODO: Customise this generated block
      }

      @Override
      public Object replace(Object key, Object value, long lifespan, TimeUnit unit) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public boolean replace(Object key, Object oldValue, Object value, long lifespan, TimeUnit unit) {
         return false;  // TODO: Customise this generated block
      }

      @Override
      public Object put(Object key, Object value, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public Object putIfAbsent(Object key, Object value, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public void putAll(Map map, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
         // TODO: Customise this generated block
      }

      @Override
      public Object replace(Object key, Object value, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public boolean replace(Object key, Object oldValue, Object value, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit) {
         return false;  // TODO: Customise this generated block
      }

      @Override
      public Object remove(Object key) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public void putAll(Map m) {
         // TODO: Customise this generated block
      }

      @Override
      public CompletableFuture putAsync(Object key, Object value) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public CompletableFuture putAsync(Object key, Object value, long lifespan, TimeUnit unit) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public CompletableFuture putAsync(Object key, Object value, long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public CompletableFuture<Void> putAllAsync(Map data) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public CompletableFuture<Void> putAllAsync(Map data, long lifespan, TimeUnit unit) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public CompletableFuture<Void> putAllAsync(Map data, long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public CompletableFuture<Void> clearAsync() {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public CompletableFuture putIfAbsentAsync(Object key, Object value) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public CompletableFuture putIfAbsentAsync(Object key, Object value, long lifespan, TimeUnit unit) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public CompletableFuture putIfAbsentAsync(Object key, Object value, long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public CompletableFuture removeAsync(Object key) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public CompletableFuture<Boolean> removeAsync(Object key, Object value) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public CompletableFuture replaceAsync(Object key, Object value) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public CompletableFuture replaceAsync(Object key, Object value, long lifespan, TimeUnit unit) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public CompletableFuture replaceAsync(Object key, Object value, long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public CompletableFuture<Boolean> replaceAsync(Object key, Object oldValue, Object newValue) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public CompletableFuture<Boolean> replaceAsync(Object key, Object oldValue, Object newValue, long lifespan, TimeUnit unit) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public CompletableFuture<Boolean> replaceAsync(Object key, Object oldValue, Object newValue, long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public CompletableFuture getAsync(Object key) {
         return null;  // TODO: Customise this generated block
      }

      @Override
      public boolean startBatch() {
         return false;  // TODO: Customise this generated block
      }

      @Override
      public void endBatch(boolean successful) {
         // TODO: Customise this generated block
      }

      @Override
      public void addListener(Object listener, KeyFilter filter) {
         // TODO: Customise this generated block
      }

      @Override
      public void addFilteredListener(Object listener, CacheEventFilter filter, CacheEventConverter converter, Set filterAnnotations) {
         // TODO: Customise this generated block
      }

      @Override
      public void addListener(Object listener, CacheEventFilter filter, CacheEventConverter converter) {
         // TODO: Customise this generated block
      }

      @Override
      public void addListener(Object listener) {
         // TODO: Customise this generated block
      }

      @Override
      public void removeListener(Object listener) {
         // TODO: Customise this generated block
      }

      @Override
      public Set<Object> getListeners() {
         return null;  // TODO: Customise this generated block
      }
   }

}
