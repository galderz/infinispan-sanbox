package org.infinispan.sandbox.management;

import org.infinispan.commons.util.Either;
import org.infinispan.sandbox.management.CommandlineClient.Err;
import org.infinispan.sandbox.management.CommandlineClient.Ok;

public class CommandlineClientTest {

   public static void main(String[] args) {
      test("ls");
      test("abc");
      test("ls \"-\"");
   }

   private static void test(String ls) {
      final Either<Err, Ok> result = CommandlineClient.invoke().apply(ls);
      switch (result.type()) {
         case LEFT:
            result.left().error.printStackTrace();
            break;
         case RIGHT:
            System.out.printf("Command worked fine, output: %n%s", result.right().output);
            break;
      }
   }

}
