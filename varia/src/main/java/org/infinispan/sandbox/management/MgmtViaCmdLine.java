package org.infinispan.sandbox.management;

public class MgmtViaCmdLine {

   public static void main(String[] args) {
      final CommandlineClient cmdLine = new CommandlineClient();

      final CommandlineClient.Result result = cmdLine.invokeAndReturnResult(
         "oc exec " +
            "-it datagrid-service-0 " +
            "-- /opt/datagrid/bin/cli.sh " +
               "--connect " +
               "--commands=\"/subsystem=datagrid-infinispan/cache-container=clustered/configurations=CONFIGURATIONS/distributed-cache-configuration=default:read-attribute(name=owners)\""
      );

      System.out.println(result);
   }

}
