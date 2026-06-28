package com.mervyn.thermaducts.blockentity;

import com.mervyn.thermaducts.duct.structural.StructuralDuctUnit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class StructuralDuctBlockEntity extends DuctBlockEntity {

  @SuppressWarnings("this-escape")
  public StructuralDuctBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
    initDuctUnits();
  }

  @Override
  protected void initDuctUnits() {
    addDuctUnit(new StructuralDuctUnit(this));
  }
}
