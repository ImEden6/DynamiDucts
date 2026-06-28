package com.mervyn.thermaducts.datagen;

import com.mervyn.thermaducts.ThermaDucts;
import com.mervyn.thermaducts.init.DDBlocks;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

public class DDBlockTagProvider extends BlockTagsProvider {

  public DDBlockTagProvider(
      PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
    super(output, lookupProvider, ThermaDucts.MODID);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    var pickaxe = tag(BlockTags.MINEABLE_WITH_PICKAXE);
    DDBlocks.BLOCKS.getEntries().forEach(entry -> pickaxe.add(entry.get()));
  }
}
