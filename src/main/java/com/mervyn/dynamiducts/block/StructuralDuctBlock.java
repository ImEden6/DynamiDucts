package com.mervyn.dynamiducts.block;

import com.mervyn.dynamiducts.blockentity.StructuralDuctBlockEntity;
import com.mervyn.dynamiducts.core.duct.DuctToken;
import com.mervyn.dynamiducts.init.DDBlockEntities;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class StructuralDuctBlock extends DuctBlock {

  public static final MapCodec<StructuralDuctBlock> CODEC = simpleCodec(StructuralDuctBlock::new);
  private static final DuctToken[] TOKENS = {DuctToken.STRUCTURAL};
  private Supplier<BlockEntityType<StructuralDuctBlockEntity>> beTypeSupplier;

  public StructuralDuctBlock(Properties properties) {
    super(properties);
  }

  public StructuralDuctBlock withBlockEntityType(
      Supplier<BlockEntityType<StructuralDuctBlockEntity>> supplier) {
    this.beTypeSupplier = supplier;
    return this;
  }

  @Override
  protected MapCodec<? extends DuctBlock> codec() {
    return CODEC;
  }

  @Override
  public DuctToken[] getDuctTokens() {
    return TOKENS;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    if (beTypeSupplier != null) {
      return new StructuralDuctBlockEntity(beTypeSupplier.get(), pos, state);
    }
    return new StructuralDuctBlockEntity(DDBlockEntities.STRUCTURAL_DUCT.get(), pos, state);
  }
}
