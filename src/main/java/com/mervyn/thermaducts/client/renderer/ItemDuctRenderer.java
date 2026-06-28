package com.mervyn.dynamiducts.client.renderer;

import com.mervyn.dynamiducts.blockentity.DuctBlockEntity;
import com.mervyn.dynamiducts.blockentity.ItemDuctBlockEntity;
import com.mervyn.dynamiducts.core.duct.DuctToken;
import com.mervyn.dynamiducts.duct.item.ItemDuctUnit;
import com.mervyn.dynamiducts.duct.item.TravelingItemSnapshot;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;

public class ItemDuctRenderer extends DuctBlockEntityRenderer {

  private static final float ITEM_SCALE = 0.35F;
  private static final int MAX_ITEMS_PER_DUCT = 16;
  private static final float SPIN_SPEED = 0.5F;

  public ItemDuctRenderer(BlockEntityRendererProvider.Context context) {
    super(context);
  }

  @Override
  public void submit(
      DuctRenderState state,
      PoseStack poseStack,
      net.minecraft.client.renderer.SubmitNodeCollector collector,
      net.minecraft.client.renderer.state.level.CameraRenderState cameraRenderState) {
    super.submit(state, poseStack, collector, cameraRenderState);

    DuctBlockEntity be = state.blockEntity;
    if (be instanceof ItemDuctBlockEntity itemBE) {
      renderTravelingItems(itemBE, state, poseStack, collector);
    }
  }

  private void renderTravelingItems(
      ItemDuctBlockEntity be,
      DuctRenderState state,
      PoseStack poseStack,
      net.minecraft.client.renderer.SubmitNodeCollector collector) {
    var unit = be.getDuctUnit(DuctToken.ITEM);
    if (!(unit instanceof ItemDuctUnit itemUnit)) return;
    if (be.getLevel() == null) return;
    if (!itemUnit.isTransparent()) return;
    float gameTime = (be.getLevel().getGameTime() + state.partialTick) * SPIN_SPEED;
    float spinAngle = gameTime % 360;

    int rendered = 0;
    ItemStackRenderState itemRenderState = new ItemStackRenderState();
    for (TravelingItemSnapshot tItem : itemUnit.getClientTravelingItems()) {
      if (rendered >= MAX_ITEMS_PER_DUCT) break;

      float travel = tItem.getProgress(state.partialTick) - 0.5F;
      Direction direction = travel < 0.0F ? tItem.getOldDirection() : tItem.getDirection();
      float offsetX = direction.getStepX() * travel;
      float offsetY = direction.getStepY() * travel;
      float offsetZ = direction.getStepZ() * travel;

      poseStack.pushPose();
      poseStack.translate(0.5F + offsetX, 0.5F + offsetY - 0.05F, 0.5F + offsetZ);
      poseStack.mulPose(Axis.YP.rotationDegrees(spinAngle));
      poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);

      itemRenderState.clear();
      Minecraft.getInstance()
          .getItemModelResolver()
          .updateForTopItem(
              itemRenderState, tItem.getStack(), ItemDisplayContext.FIXED, be.getLevel(), null, 0);
      itemRenderState.submit(
          poseStack,
          collector,
          state.lightCoords,
          net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
          0);

      poseStack.popPose();
      rendered++;
    }
  }
}
