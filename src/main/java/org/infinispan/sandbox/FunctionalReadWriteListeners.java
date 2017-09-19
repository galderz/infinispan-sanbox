package org.infinispan.sandbox;

import org.infinispan.functional.EntryView.ReadEntryView;
import org.infinispan.functional.EntryView.WriteEntryView;
import org.infinispan.functional.FunctionalMap.ReadWriteMap;
import org.infinispan.functional.Listeners.ReadWriteListeners.ReadWriteListener;
import org.infinispan.functional.Traversable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class FunctionalReadWriteListeners {

   public static void main( String[] args ) throws Exception {
      CreateFunctionalMaps.main(args);
      ReadWriteMap<String, String> readWriteMap = CreateFunctionalMaps.rw();

      AutoCloseable handler = readWriteMap.listeners().add(new ReadWriteListener<String, String>() {
         @Override
         public void onCreate(ReadEntryView<String, String> created) {
            System.out.printf("onCreate(): %s%n", created.get());
         }

         @Override
         public void onModify(ReadEntryView<String, String> before,
            ReadEntryView<String, String> after) {
            System.out.printf("onModify(), before: %s%n", before.get());
            System.out.printf("onModify(), after: %s%n", after.get());
         }

         @Override
         public void onRemove(ReadEntryView<String, String> removed) {
            System.out.printf("onRemove(): %s%n", removed.get());
         }
      });

      try {
         Map<String, String> entries = new HashMap<>();
         entries.put("key1", "value1");
         entries.put("key2", "value2");

         Traversable<Void> t0 = readWriteMap.evalMany(entries, (v, view) -> view.set(v));
         System.out.printf("Created %d entries %n", t0.count());
         CompletableFuture<Void> f0 = readWriteMap.eval("key1", view -> view.set("new-value1"));
         CompletableFuture<Long> f1 = f0.thenApply(ignore -> {
            Traversable<Void> t1 = readWriteMap.evalMany(entries.keySet(), WriteEntryView::remove);
            return t1.count();
         });
         System.out.printf("Removed %d entries", f1.get());
      } finally {
         handler.close();
      }
   }

}
