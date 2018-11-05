package org.infinispan.sandbox.management;

public class CommandlineClientTest {

   public static void main(String[] args) {
      test("ls");

      try {
         test("abc");
      } catch (Exception e) {
         e.printStackTrace();
      }

      try {
         test("ls \"-\"");
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private static void test(String ls) {
      CommandLine.invoke()
         .andThen(CommandLine.throwIfError())
         .andThen(ok -> {
            System.out.printf("Command worked fine, output: %n%s", ok.output);
            return null;
         })
         .apply(ls);
   }

}
