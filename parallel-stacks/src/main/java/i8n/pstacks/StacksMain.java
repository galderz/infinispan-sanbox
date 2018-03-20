package i8n.pstacks;

import org.jboss.modules.LocalModuleLoader;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class StacksMain {

   public static void main(String[] args) throws Exception {
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
