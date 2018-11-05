package org.infinispan.sandbox.management;

import org.jboss.dmr.ModelNode;
import org.wildfly.extras.creaper.core.online.ModelNodeResult;

public class MgmtViaCmdLine {

   public static void main(String[] args) {
      final String podName = "datagrid-service-0";
      final int numOwners = numOwners(podName);

      System.out.println("Num owners is: " + numOwners);
   }

   public static int numOwners(String podName) {
      return CommandLine.invoke()
         .andThen(CommandLine.throwIfError())
         .andThen(ok -> {
            final ModelNodeResult numOwners = new ModelNodeResult(ModelNode.fromString(ok.output));
            numOwners.assertDefinedValue();
            return Integer.parseInt(numOwners.stringValue());
         })
         .apply(
            String.format(
               "oc exec " +
                  " -it %s " +
                  " -- /opt/datagrid/bin/cli.sh " +
                  " --connect " +
                  " --commands=/subsystem=datagrid-infinispan/cache-container=clustered/configurations=CONFIGURATIONS/distributed-cache-configuration=default:read-attribute(name=owners)"
               , podName
            )
         );
   }

}
