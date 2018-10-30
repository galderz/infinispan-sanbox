package org.infinispan.sandbox.management;

import org.jboss.dmr.ModelNode;
import org.wildfly.extras.creaper.core.online.ModelNodeResult;

public class MgmtViaCmdLine {

   public static void main(String[] args) {
      final CommandlineClient cmdLine = new CommandlineClient();

      final CommandlineClient.Result result = cmdLine.invokeAndReturnResult(
         "oc exec -it datagrid-service-0 -- /opt/datagrid/bin/cli.sh --connect --commands=/subsystem=datagrid-infinispan/cache-container=clustered/configurations=CONFIGURATIONS/distributed-cache-configuration=default:read-attribute(name=owners)"
      );

      if (result.getErrorCode() == 0) {
         System.out.println(result);

         final ModelNodeResult numOwners = new ModelNodeResult(ModelNode.fromString(result.getOutput()));
         numOwners.assertDefinedValue();

         System.out.println("Num owners is: " + numOwners.stringValue());
      } else {
         System.out.println("Error executing command: " + result);
      }

   }

}
