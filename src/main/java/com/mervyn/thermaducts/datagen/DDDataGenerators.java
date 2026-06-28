package com.mervyn.dynamiducts.datagen;

import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DDDataGenerators {

  public static void gatherDataClient(GatherDataEvent.Client event) {
    event.createProvider(DDModelProvider::new);
    // Server-side providers registered here too — in data gen context, both client
    // and server registries are available in a single JVM. Using a single run config
    // avoids HashCache cross-run stale-file deletion.
    event.createBlockAndItemTags(DDBlockTagProvider::new, DDItemTagProvider::new);
    event.createProvider(DDLootTableProvider::new);
    event.createProvider(DDRecipeProvider.Runner::new);
  }
}
