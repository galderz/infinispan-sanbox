package org.infinispan.sandbox.management;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CommandlineClient {

   class Result {
      private final int errorCode;
      private final String output;
      private final String error;

      public Result(int errorCode, String output, String error) {
         this.errorCode = errorCode;
         this.output = output;
         this.error = error;
      }

      public int getErrorCode() {
         return errorCode;
      }

      public String getOutput() {
         return output;
      }

      @Override
      public String toString() {
         return "Result{" +
            "errorCode=" + errorCode +
            ", output='" + output + '\'' +
            ", error='" + error + '\'' +
            '}';
      }
   }

   public Result invokeAndReturnResult(String command) {
      try {
         ProcessBuilder pb = new ProcessBuilder(command.split(" "));
         Process p = pb.start();
         int resultCode = p.waitFor();
         String output = getStream(p.getInputStream());
         String error = getStream(p.getErrorStream());

         final Result result = new Result(resultCode, output, error);

         if (result.errorCode != 0) {
            throw new IllegalStateException(
               "Expected error code 0 but got " + result.errorCode
                  + ". Output: " + result.output
                  + ". Error: " + result.error
            );
         }

         return result;
      } catch (Exception e) {
         throw new IllegalStateException("Command " + command + " failed.", e);
      }
   }

   private String getStream(InputStream stream) throws IOException {
      final BufferedReader br = new BufferedReader(new InputStreamReader(stream));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
         sb.append(line).append("\n");
      }
      return sb.toString();
   }

   public void invoke(String command) {
      Result result = invokeAndReturnResult(command);
      if (result.errorCode != 0) {
         throw new IllegalStateException("Expected error code 0 but got " + result.errorCode + ". Output: " + result.output);
      }
   }
}
