package com.mervyn.dynamiducts.datagen;

import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DDDataGenerators {

  public static void gatherDataClient(GatherDataEvent.Client event) {
    event.createProvider(DDModelProvider::new);
  }

  public static void gatherDataServer(GatherDataEvent.Server event) {
    event.createBlockAndItemTags(DDBlockTagProvider::new, DDItemTagProvider::new);
    event.createProvider(DDLootTableProvider::new);
    event.createProvider(DDRecipeProvider.Runner::new);
  }
}
