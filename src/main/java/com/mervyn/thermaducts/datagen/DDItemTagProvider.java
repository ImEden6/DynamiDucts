package com.mervyn.thermaducts.datagen;

import com.mervyn.thermaducts.ThermaDucts;
import com.mervyn.thermaducts.init.DDItems;
import com.mervyn.thermaducts.init.DDTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagCopyingItemTagProvider;

public class DDItemTagProvider extends BlockTagCopyingItemTagProvider {

  public DDItemTagProvider(
      PackOutput output,
      CompletableFuture<HolderLookup.Provider> lookupProvider,
      CompletableFuture<TagLookup<Block>> blockTags) {
    super(output, lookupProvider, blockTags, ThermaDucts.MODID);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    tag(DDTags.INGOTS_LEAD).add(DDItems.LEAD_INGOT.get());
    tag(DDTags.INGOTS_TIN).add(DDItems.TIN_INGOT.get());
    tag(DDTags.INGOTS_SILVER).add(DDItems.SILVER_INGOT.get());
    tag(DDTags.INGOTS_INVAR).add(DDItems.INVAR_INGOT.get());
    tag(DDTags.INGOTS_ELECTRUM).add(DDItems.ELECTRUM_INGOT.get());
    tag(DDTags.INGOTS_BRONZE).add(DDItems.BRONZE_INGOT.get());
    tag(DDTags.INGOTS_SIGNALUM).add(DDItems.SIGNALUM_INGOT.get());
    tag(DDTags.INGOTS_ENDERIUM).add(DDItems.ENDERIUM_INGOT.get());
    tag(DDTags.INGOTS_LUMIUM).add(DDItems.LUMIUM_INGOT.get());

    tag(DDTags.NUGGETS_LEAD).add(DDItems.LEAD_NUGGET.get());
    tag(DDTags.NUGGETS_TIN).add(DDItems.TIN_NUGGET.get());
    tag(DDTags.NUGGETS_SILVER).add(DDItems.SILVER_NUGGET.get());
    tag(DDTags.NUGGETS_INVAR).add(DDItems.INVAR_NUGGET.get());
    tag(DDTags.NUGGETS_ELECTRUM).add(DDItems.ELECTRUM_NUGGET.get());
    tag(DDTags.NUGGETS_BRONZE).add(DDItems.BRONZE_NUGGET.get());
    tag(DDTags.NUGGETS_SIGNALUM).add(DDItems.SIGNALUM_NUGGET.get());
    tag(DDTags.NUGGETS_ENDERIUM).add(DDItems.ENDERIUM_NUGGET.get());
    tag(DDTags.NUGGETS_LUMIUM).add(DDItems.LUMIUM_NUGGET.get());

    tag(DDTags.HARDENED_GLASS).add(Items.TINTED_GLASS);
  }
}
