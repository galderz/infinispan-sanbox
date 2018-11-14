package org.infinispan.sandbox.management;

import java.util.List;

public class MgmtViaCmdLine {

   public static void main(String[] args) {
      final List<String> results = CommandLine.invoke()
         .andThen(CommandLine.throwIfError())
         .andThen(CommandLine.toMgmtResult())
         .andThen(CommandLine.toStringList())
         .apply(
            "/opt/infinispan-server/bin/ispn-cli.sh " +
               "--connect " +
               "--commands=/subsystem=datagrid-infinispan/cache-container=clustered/configurations=CONFIGURATIONS/distributed-cache-configuration=default:read-children-names(child-type=memory)"
         );

      System.out.println(results.size());
      System.out.println(results.get(0));
   }

}
