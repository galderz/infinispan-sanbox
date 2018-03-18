package i8n.protostream;

import org.infinispan.protostream.annotations.ProtoDoc;
import org.infinispan.protostream.annotations.ProtoField;
import org.infinispan.protostream.annotations.ProtoMessage;

import java.util.Objects;

@ProtoMessage(name = "Player")
public class Player {

   private String id;
   private int score;

   // Required for proto schema builder
   public Player() {
   }

   public Player(String id, int score) {
      this.score = score;
      this.id = id;
   }

   @ProtoField(number = 10, required = true)
   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   @ProtoField(number = 20, required = true)
   public int getScore() {
      return score;
   }

   public void setScore(int score) {
      this.score = score;
   }

   @Override
   public String toString() {
      return "Player{" +
         "id='" + id + '\'' +
         ", score=" + score +
         '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Player player = (Player) o;
      return score == player.score &&
         Objects.equals(id, player.id);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, score);
   }

}
