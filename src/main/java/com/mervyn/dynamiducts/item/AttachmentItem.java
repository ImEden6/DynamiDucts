package com.mervyn.dynamiducts.item;

import com.mervyn.dynamiducts.block.DuctHitHelper;
import com.mervyn.dynamiducts.blockentity.DuctBlockEntity;
import com.mervyn.dynamiducts.core.attachment.Attachment;
import com.mervyn.dynamiducts.core.attachment.AttachmentPlacementHelper;
import com.mervyn.dynamiducts.core.attachment.AttachmentTier;
import com.mervyn.dynamiducts.mixin.UseOnContextAccessor;
import java.util.function.BiFunction;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class AttachmentItem extends Item {

  private final DDTooltipHelper.AttachmentTooltipType tooltipType;
  private final AttachmentTier tier;
  private final BiFunction<DuctBlockEntity, Direction, Attachment> placer;
  private final boolean requiresExternalSide;

  public AttachmentItem(
      Properties properties,
      DDTooltipHelper.AttachmentTooltipType tooltipType,
      AttachmentTier tier,
      BiFunction<DuctBlockEntity, Direction, Attachment> placer,
      String... tooltipKeys) {
    this(properties, tooltipType, tier, true, placer, tooltipKeys);
  }

  public AttachmentItem(
      Properties properties,
      DDTooltipHelper.AttachmentTooltipType tooltipType,
      AttachmentTier tier,
      boolean requiresExternalSide,
      BiFunction<DuctBlockEntity, Direction, Attachment> placer,
      String... tooltipKeys) {
    super(properties);
    this.tooltipType = tooltipType;
    this.tier = tier;
    this.requiresExternalSide = requiresExternalSide;
    this.placer = placer;
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    var level = context.getLevel();
    var pos = context.getClickedPos();
    var hitResult = ((UseOnContextAccessor) context).dynamiducts$getHitResult();

    if (!level.isClientSide() && level.getBlockEntity(pos) instanceof DuctBlockEntity ductBE) {
      var hit = DuctHitHelper.resolve(ductBE.getBlockState(), ductBE, pos, hitResult);
      var side = hit.side();
      boolean canPlace =
          requiresExternalSide
              ? AttachmentPlacementHelper.canPlaceTransferAttachment(ductBE, side)
              : AttachmentPlacementHelper.canPlaceFilterAttachment(ductBE, side);
      if (!canPlace) {
        return InteractionResult.PASS;
      }

      Attachment attachment = placer.apply(ductBE, side);
      if (attachment != null) {
        ductBE.setAttachment(side, attachment);
        if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
          context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
      }
    }
    return InteractionResult.PASS;
  }

  public DDTooltipHelper.AttachmentTooltipType getTooltipType() {
    return tooltipType;
  }

  public AttachmentTier getTier() {
    return tier;
  }
}
