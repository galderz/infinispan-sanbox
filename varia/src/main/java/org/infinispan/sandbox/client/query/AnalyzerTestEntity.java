package org.infinispan.sandbox.client.query;

public class AnalyzerTestEntity {

   public String f1;

   public Integer f2;

   public AnalyzerTestEntity(String f1, Integer f2) {
      this.f1 = f1;
      this.f2 = f2;
   }

   @Override
   public String toString() {
      return "AnalyzerTestEntity{f1='" + f1 + "', f2=" + f2 + '}';
   }

}
