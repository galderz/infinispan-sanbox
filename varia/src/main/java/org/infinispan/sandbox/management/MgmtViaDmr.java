package org.infinispan.sandbox.management;

import org.wildfly.extras.creaper.core.ManagementClient;
import org.wildfly.extras.creaper.core.online.Constants;
import org.wildfly.extras.creaper.core.online.ModelNodeResult;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.OnlineOptions;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;

import java.io.IOException;

public class MgmtViaDmr {

   private static final String NODE0_ADDRESS = System.getProperty("node0.ip", "127.0.0.1");
   private static final int NODE0_PORT = Integer.valueOf(System.getProperty("node0.mgmt.port", "9990"));
   private static final String LOGIN = System.getProperty("login", "admin");
   private static final String PASSWORD = System.getProperty("password", "admin9Pass!");

   public static void main(String[] args) throws IOException {
      OnlineManagementClient onlineClient =
         ManagementClient.online(
            OnlineOptions.standalone()
               .hostAndPort(NODE0_ADDRESS, NODE0_PORT)
               .auth(LOGIN, PASSWORD)
               .build()
      );

      final Operations ops = new Operations(onlineClient);

      ModelNodeResult serverState = ops.readAttribute(Address.root(), Constants.SERVER_STATE);
      serverState.assertDefinedValue();

      System.out.println("Server state is: " + serverState.stringValue());

      ModelNodeResult numOwners = ops.readAttribute(
         Address.subsystem("datagrid-infinispan")
            .and("cache-container", "clustered")
            .and("configurations", "CONFIGURATIONS")
            .and("distributed-cache-configuration", "default")
         , "owners"
      );

      numOwners.assertDefinedValue();

      System.out.println("Num owners is: " + numOwners.stringValue());

      onlineClient.close();
   }

}
