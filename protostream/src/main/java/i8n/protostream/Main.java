package i8n.protostream;

import org.infinispan.client.hotrod.marshall.ProtoStreamMarshaller;
import org.infinispan.commons.io.ByteBuffer;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.annotations.ProtoSchemaBuilder;

import java.io.IOException;

public class Main {

   public static void main(String[] args) throws Exception {
      final ProtoStreamMarshaller m = new ProtoStreamMarshaller();

      final SerializationContext serialCtx = m.getSerializationContext();
      ProtoSchemaBuilder protoSchemaBuilder = new ProtoSchemaBuilder();
      protoSchemaBuilder
         .fileName("player.proto")
         .addClass(Player.class)
         .build(serialCtx);

      final Player p = new Player("Clemont", 10);
      System.out.println("[write] " + p);
      final byte[] bytes = m.objectToByteBuffer(p);

      final Player pp = (Player) m.objectFromByteBuffer(bytes);
      System.out.println("[read]  " + p);
      System.out.println("Equals? " + p.equals(pp));
   }

}
