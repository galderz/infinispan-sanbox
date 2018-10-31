package org.infinispan.sandbox.management;

import org.infinispan.commons.util.Either;
import org.infinispan.sandbox.management.CommandlineClient.Err;
import org.infinispan.sandbox.management.CommandlineClient.Ok;
import org.jboss.dmr.ModelNode;
import org.wildfly.extras.creaper.core.online.ModelNodeResult;

public class MgmtViaCmdLine {

   public static void main(String[] args) {
      final Either<Err, Ok> result = CommandlineClient.invoke().apply(
         "oc exec " +
            "-it datagrid-service-0 " +
            "-- /opt/datagrid/bin/cli.sh " +
            "--connect " +
            "--commands=/subsystem=datagrid-infinispan/cache-container=clustered/configurations=CONFIGURATIONS/distributed-cache-configuration=default:read-attribute(name=owners)"
      );

      switch (result.type()) {
         case LEFT:
            result.left().error.printStackTrace();
            break;
         case RIGHT:
            System.out.printf("Command worked fine, output: %n%s", result.right().output);

            final ModelNodeResult numOwners = new ModelNodeResult(ModelNode.fromString(result.right().output));
            numOwners.assertDefinedValue();

            System.out.println("Num owners is: " + numOwners.stringValue());
            break;
      }
   }

}
