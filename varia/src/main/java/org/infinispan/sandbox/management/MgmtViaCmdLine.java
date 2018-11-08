package org.infinispan.sandbox.management;

import org.jboss.dmr.ModelNode;
import org.wildfly.extras.creaper.core.online.ModelNodeResult;

public class MgmtViaCmdLine {

   public static void main(String[] args) {
      final int cacheServiceNumOwnersMethod1 = numOwnersMethod1("cache-service-0");
      System.out.println("[cache-service] Num owners (method 1) is: " + cacheServiceNumOwnersMethod1);

      final int cacheServiceNumOwnersMethod2 = numOwnersMethod2("cache-service-0");
      System.out.println("[cache-service] Num owners (method 2) is: " + cacheServiceNumOwnersMethod2);

      final int datagridServiceNumOwnersMethod1 = numOwnersMethod1("datagrid-service-0");
      System.out.println("[datagrid-service] Num owners (method 1) is: " + datagridServiceNumOwnersMethod1);

      final int datagridServiceNumOwnersMethod2 = numOwnersMethod2("datagrid-service-0");
      System.out.println("[datagrid-service] Num owners (method 2) is: " + datagridServiceNumOwnersMethod2);
   }

   public static int numOwnersMethod1(String podName) {
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

   public static int numOwnersMethod2(String podName) {
      return CommandLine.invoke()
         .andThen(CommandLine.throwIfError())
         .andThen(CommandLine.toMgmtResult())
         .andThen(ModelNode::asInt)
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
