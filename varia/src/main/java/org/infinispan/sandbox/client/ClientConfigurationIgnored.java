package org.infinispan.sandbox.client;

import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

public class ClientConfigurationIgnored {

   public static void main(String[] args) {
      ConfigurationBuilder builder = new ConfigurationBuilder();
      builder.clientIntelligence(ClientIntelligence.BASIC);
      final Configuration cfg = builder.build();
      System.out.println(cfg.clientIntelligence());

      ConfigurationBuilder builderCopy = new ConfigurationBuilder().read(cfg);
      final Configuration cfgCopy = builderCopy.build();
      System.out.println(cfgCopy.clientIntelligence());
   }

}
