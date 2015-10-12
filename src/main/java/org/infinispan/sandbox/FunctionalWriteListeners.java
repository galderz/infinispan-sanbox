package org.infinispan.sandbox;

import org.infinispan.commons.api.functional.EntryView.WriteEntryView;
import org.infinispan.commons.api.functional.FunctionalMap.WriteOnlyMap;

import java.util.HashMap;
import java.util.Map;

public class FunctionalWriteListeners {

   public static void main( String[] args ) throws Exception {
      CreateFunctionalMaps.main(args);
      WriteOnlyMap<String, String> writeOnlyMap = CreateFunctionalMaps.wo();

      // `written` is a ReadEntryView of the written entry
      try(AutoCloseable handler = writeOnlyMap.listeners().onWrite(written ->
            System.out.printf("Written: %s%n", written.find()))) {
         Map<String, String> entries = new HashMap<>();
         entries.put("key1", "value1");
         entries.put("key2", "value2");

         writeOnlyMap.evalMany(entries, (v, writeView) -> writeView.set(v))
            .thenCompose(ignore -> writeOnlyMap.evalAll(WriteEntryView::remove))
            .get(); // Wait for completable future
      }

   }

}
