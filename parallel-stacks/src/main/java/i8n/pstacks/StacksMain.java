package i8n.pstacks;

import org.infinispan.Cache;
import org.infinispan.commons.hash.Hash;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.container.DataContainer;
import org.infinispan.manager.DefaultCacheManager;
import org.jboss.modules.LocalModuleLoader;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class StacksMain {

   public static void main(String[] args) throws Exception {
      // Thread.currentThread().setContextClassLoader(DefaultCacheManager.class.getClassLoader());

      final DefaultCacheManager cacheManager =
         new DefaultCacheManager("infinispan-cfg.xml");
      final Cache<Integer, String> cache =
         cacheManager.getCache("test");
      final DataContainer dc = cache
         .getAdvancedCache()
         .getComponentRegistry()
         .getComponent(DataContainer.class);

      Map<String, DataContainer> dataContainers = new HashMap<>();
      dataContainers.put("test", dc);

      final ParallelInfinispan parallel = new ParallelInfinispan(dataContainers);
      final Cache<Integer, String> parallelCache = parallel
         .cacheManager("infinispan-cfg.xml")
         .getCache("test");
      parallelCache.put(249, "Lugia");

      System.out.println(cache.get(249));
   }

   @SuppressWarnings("unused")
   private static void testParallelClassLoading() throws Exception {
      final File repoRoot = ModulesUtil.getResourceFile(StacksMain.class, "test/repo");
      ModuleLoader moduleLoader = new LocalModuleLoader(new File[]{repoRoot});

      final Path tmpdir = Files.createTempDirectory("repository");

      System.setProperty("maven.repo.local", tmpdir.toAbsolutePath().toString());
      System.setProperty("remote.maven.repo", "http://repository.jboss.org/nexus/content/groups/public/");

      ModuleIdentifier moduleId = ModuleIdentifier.fromString("sample.maven");
      Module module = moduleLoader.loadModule(moduleId);

      final Class<?> clazz = module.getClassLoader().loadClass("org.infinispan.commons.api.BasicCache");
      System.out.println(clazz);

      ModuleIdentifier moduleId91 = ModuleIdentifier.fromString("sample.maven:91");
      Module module91 = moduleLoader.loadModule(moduleId91);

      final Class<?> clazz91 = module91.getClassLoader().loadClass("org.infinispan.commons.api.BasicCache");
      System.out.println(clazz91);
   }

}
