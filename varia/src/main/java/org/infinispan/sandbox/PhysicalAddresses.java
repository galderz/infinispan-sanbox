package org.infinispan.sandbox;

import org.infinispan.remoting.transport.Transport;

public class PhysicalAddresses {

    public static void main(String[] args) {
        Cluster.withCluster((cm1, cm2) -> {
            Transport t = cm1.getGlobalComponentRegistry().getComponent(Transport.class);
            System.out.println(t.getPhysicalAddresses());
        });
    }

}
