package com.mervyn.dynamiducts.item;

import com.mervyn.dynamiducts.block.DuctBlock;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

public class DuctBlockItem extends BlockItem {

  public DuctBlockItem(Block block, Properties properties, String... tooltipKeys) {
    super(block, properties);
  }

  @Override
  public InteractionResult place(BlockPlaceContext context) {
    if (getBlock() instanceof DuctBlock duct && duct.isCraftingOnly()) {
      return InteractionResult.FAIL;
    }
    return super.place(context);
  }
}
