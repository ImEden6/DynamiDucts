package com.mervyn.thermaducts.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class DuctBlockItemRenderer implements SpecialModelRenderer<DuctBlockEntityRenderer.DuctRenderState> {

  private static DuctBlockItemRenderer instance;
  private static DuctBlockEntityRenderer delegateRenderer;

  private DuctBlockItemRenderer() {
  }

  public static DuctBlockItemRenderer get() {
    if (instance == null) {
      instance = new DuctBlockItemRenderer();
    }
    return instance;
  }

  private static DuctBlockEntityRenderer getDelegate() {
    if (delegateRenderer == null) {
      delegateRenderer = new DuctBlockEntityRenderer(null);
    }
    return delegateRenderer;
  }

  public static class Unbaked implements SpecialModelRenderer.Unbaked<DuctBlockEntityRenderer.DuctRenderState> {
    public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

    @Override
    public MapCodec<Unbaked> type() {
      return MAP_CODEC;
    }

    @Override
    public SpecialModelRenderer<DuctBlockEntityRenderer.DuctRenderState> bake(
        net.minecraft.client.renderer.special.SpecialModelRenderer.BakingContext context) {
      return DuctBlockItemRenderer.get();
    }
  }

  @Override
  public DuctBlockEntityRenderer.DuctRenderState extractArgument(ItemStack stack) {
    DuctBlockEntityRenderer.DuctRenderState state = new DuctBlockEntityRenderer.DuctRenderState();
    state.itemRender = true;
    state.connectionMask = 0; // Center block only
    state.partialTick = 0.0f;
    if (stack.getItem() instanceof BlockItem blockItem) {
      Block block = blockItem.getBlock();
      state.blockState = block.defaultBlockState();
      state.tex = DuctBlockEntityRenderer.getDuctTextures(block);
    }
    return state;
  }

  @Override
  public void submit(
      DuctBlockEntityRenderer.DuctRenderState state,
      PoseStack poseStack,
      SubmitNodeCollector collector,
      int packedLight,
      int packedOverlay,
      boolean hasFoil,
      int color) {
    // Set lightCoords on the state since it is passed into DuctBlockEntityRenderer
    // methods
    state.lightCoords = packedLight;
    getDelegate().submit(state, poseStack, collector, null);
  }

  @Override
  public void getExtents(Consumer<org.joml.Vector3fc> consumer) {
    consumer.accept(new org.joml.Vector3f(0, 0, 0));
    consumer.accept(new org.joml.Vector3f(1, 1, 1));
  }
}
