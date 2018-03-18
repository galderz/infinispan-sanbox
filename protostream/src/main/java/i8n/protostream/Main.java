package i8n.protostream;

import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.commons.io.ByteBuffer;
import org.infinispan.protostream.SerializationContext;

import java.io.IOException;

public class Main {

   public static void main(String[] args) throws Exception {
      final ProtoStreamMarshaller m = new ProtoStreamMarshaller();

      final Player p = new Player("Clemont", 10);
      final byte[] bytes = m.objectToByteBuffer(p);

      final Player pp = (Player) m.objectFromByteBuffer(bytes);
      System.out.println(p.equals(pp));
   }

}
