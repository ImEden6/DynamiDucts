package com.mervyn.dynamiducts.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;

public class DuctBlockItemRenderer implements NoDataSpecialModelRenderer {

  private static DuctBlockItemRenderer instance;

  private DuctBlockItemRenderer() {}

  public static DuctBlockItemRenderer get() {
    if (instance == null) {
      instance = new DuctBlockItemRenderer();
    }
    return instance;
  }

  @Override
  public void submit(
      PoseStack poseStack,
      SubmitNodeCollector collector,
      int packedLight,
      int packedOverlay,
      boolean hasFoil,
      int color) {
    // We do not have direct access to ItemStack here since this is NoDataSpecialModelRenderer.
    // Wait, how do we get the item stack or blockItem?
    // Ah! NoDataSpecialModelRenderer is registered per-item, but wait!
    // We need to know which block item we are rendering!
    // If we implement SpecialModelRenderer<DuctBlockEntityRenderer.DuctRenderState> instead, we can
    // extract it from the item stack!
    // Yes! Implementing SpecialModelRenderer allows us to use extractArgument(ItemStack stack) to
    // get/prepare the state!
    // Let's implement SpecialModelRenderer<DuctBlockEntityRenderer.DuctRenderState> instead of
    // NoDataSpecialModelRenderer!
  }

  @Override
  public void getExtents(Consumer<org.joml.Vector3fc> consumer) {
    consumer.accept(new org.joml.Vector3f(0, 0, 0));
    consumer.accept(new org.joml.Vector3f(1, 1, 1));
  }
}
