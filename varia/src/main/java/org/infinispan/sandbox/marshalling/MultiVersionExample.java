package org.infinispan.sandbox.marshalling;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.commons.marshall.Externalizer;
import org.infinispan.commons.marshall.SerializeWith;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.sandbox.Cluster;

public class MultiVersionExample {

   public static void main(String[] args) {
      Cluster.withReplCluster((cm1, cm2) -> {
         rwV1(cm1, cm2);
         rwV2(cm1, cm2);
         rwV3(cm1, cm2);
      });
   }

   static void rwV1(EmbeddedCacheManager cm1, EmbeddedCacheManager cm2) {
      Cache<String, PersonV1> c1 = cm1.getCache();
      Cache<String, PersonV1> c2 = cm2.getCache();

      PersonV1 p = new PersonV1("Boo", LocalDate.of(1967, Month.APRIL, 20));
      c1.put("p1", p);
      PersonV1 pR = c2.get("p1");
      assertEquals(p.name, pR.name);
      assertEquals(p.birthday, pR.birthday);
   }

   static void rwV2(EmbeddedCacheManager cm1, EmbeddedCacheManager cm2) {
      Cache<String, PersonV2> c1 = cm1.getCache();
      Cache<String, PersonV2> c2 = cm2.getCache();

      PersonV2 p = new PersonV2("Moo");
      c1.put("p2", p);
      PersonV2 pR = c2.get("p2");
      assertEquals(p.name, pR.name);
   }

   static void rwV3(EmbeddedCacheManager cm1, EmbeddedCacheManager cm2) {
      Cache<String, PersonV3> c1 = cm1.getCache();
      Cache<String, PersonV3> c2 = cm2.getCache();

      PersonV1 par1 = new PersonV1("Boo", LocalDate.of(1967, Month.APRIL, 20));
      PersonV2 par2 = new PersonV2("Moo");
      PersonV3 p = new PersonV3("Doo", LocalDate.of(1967, Month.APRIL, 20), Arrays.asList(par1, par2));

      c1.put("p3", p);
      PersonV3 pR = c2.get("p3");
      assertEquals(p.name, pR.name);
      assertEquals(p.birthday, pR.birthday);
      assertEquals(2, pR.parents.size());
   }

   interface Person {
      int getVersion();
   }

   @SerializeWith(PersonExternalizer.class)
   static class PersonV1 implements Person {
      final String name;
      final LocalDate birthday;

      PersonV1(String name, LocalDate birthday) {
         this.name = name;
         this.birthday = birthday;
      }

      @Override
      public int getVersion() {
         return 1;
      }
   }

   @SerializeWith(PersonExternalizer.class)
   static class PersonV2 implements Person {
      final String name;

      PersonV2(String name) {
         this.name = name;
      }

      @Override
      public int getVersion() {
         return 2;
      }
   }

   @SerializeWith(PersonExternalizer.class)
   static class PersonV3 implements Person {
      final String name;
      final LocalDate birthday;
      final List<Person> parents;

      PersonV3(String name, LocalDate birthday, List<Person> parents) {
         this.name = name;
         this.birthday = birthday;
         this.parents = parents;
      }

      @Override
      public int getVersion() {
         return 3;
      }
   }

   public static class PersonExternalizer implements Externalizer<Person> {

      @Override
      public void writeObject(ObjectOutput out, Person obj) throws IOException {
         int ver = obj.getVersion();
         out.writeByte(ver);
         switch (ver) {
            case 1:
               out.writeUTF(((PersonV1) obj).name);
               out.writeObject(((PersonV1) obj).birthday);
               break;
            case 2:
               out.writeUTF(((PersonV2) obj).name);
               break;
            case 3:
               out.writeUTF(((PersonV3) obj).name);
               out.writeObject(((PersonV3) obj).birthday);
               out.writeObject(((PersonV3) obj).parents);
               break;
            default:
               throw new IOException("Unexpected person version: " + ver);
         }
      }

      @Override
      public Person readObject(ObjectInput in) throws IOException, ClassNotFoundException {
         byte ver = in.readByte();
         switch (ver) {
            case 1:
               return new PersonV1(in.readUTF(), (LocalDate) in.readObject());
            case 2:
               return new PersonV2(in.readUTF());
            case 3:
               return new PersonV3(in.readUTF(), (LocalDate) in.readObject(), (List<Person>) in.readObject());
            default:
               throw new IOException("Unexpected person version: " + ver);
         }
      }

   }

}
