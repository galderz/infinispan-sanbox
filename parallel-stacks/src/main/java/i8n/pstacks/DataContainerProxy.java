package i8n.pstacks;

import org.infinispan.container.DataContainer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DataContainerProxy {

   static Object create(DataContainer dc, Class c, ClassLoader cl) {
      //Thread.currentThread().setContextClassLoader(cl);
      return Proxy.newProxyInstance(cl,
         new Class[] {c}, new DataContainerInvocationHandler(dc));
   }

   private static final class DataContainerInvocationHandler implements InvocationHandler {

      final DataContainer dc;

      private DataContainerInvocationHandler(DataContainer dc) {
         this.dc = dc;
      }

      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         System.out.printf("Invoked method: %s%n", method.getName());
         switch (method.getName()) {
            case "peek":
               return dc.peek(args);
            case "put":
               dc.put(args[0], args[1], null);
               return null;
            default:
               return method.invoke(dc, args);
         }
      }

   }

}
