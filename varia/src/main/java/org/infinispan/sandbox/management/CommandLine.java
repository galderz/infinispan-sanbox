package org.infinispan.sandbox.management;

import org.infinispan.commons.util.Either;
import org.jboss.dmr.ModelNode;
import org.wildfly.extras.creaper.core.online.ModelNodeResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Function;

public final class CommandLine {

   public static final class Ok {

      final String output;

      Ok(String output) {
         this.output = output;
      }

   }

   public static final class Err {

      final int errorCode;
      final RuntimeException error;

      Err(int errorCode, RuntimeException error) {
         this.errorCode = errorCode;
         this.error = error;
      }

   }

   public static Function<String, Either<Err, Ok>> invoke() {
      return cmd -> {
         try {
            System.out.printf("Invoking on command line: '%s'%n", cmd);

            ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
            Process p = pb.start();
            int resultCode = p.waitFor();
            String output = getStream(p.getInputStream());
            String error = getStream(p.getErrorStream());

            return resultCode != 0
               ? Either.newLeft(new Err(resultCode, new RuntimeException(error)))
               : Either.newRight(new Ok(output));
         } catch (Exception e) {
            return Either.newLeft(new Err(-1, new RuntimeException(e)));
         }
      };
   }

   public static Function<Ok, ModelNodeResult> toMgmtResult() {
      return ok -> {
         System.out.printf("Received result:%n%s%n", ok.output);

         final ModelNodeResult result =
            new ModelNodeResult(ModelNode.fromString(ok.output));
         result.assertDefinedValue();
         return result;
      };
   }

   public static Function<ModelNodeResult, Integer> asIntResult() {
      return result -> {
         // ModelNode.asInt might not the operation's result as int,
         // hence this helper method to make sure it returns the result as int
         final String stringValue = result.stringValue();
         System.out.printf("String value: %s%n", stringValue);
         return Integer.parseInt(stringValue);
      };
   }

   private static String getStream(InputStream stream) throws IOException {
      final BufferedReader br = new BufferedReader(new InputStreamReader(stream));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
         sb.append(line).append("\n");
      }
      return sb.toString();
   }

   public static Function<Either<Err, Ok>, Ok> throwIfError() {
      return result -> {
         switch (result.type()) {
            case LEFT:
               throw result.left().error;
            case RIGHT:
               return result.right();
            default:
               throw new IllegalStateException("No other possible type: " + result.type());
         }
      };
   }

}
