package com.mervyn.dynamiducts.item;

import com.mervyn.dynamiducts.attachment.relay.Relay;
import com.mervyn.dynamiducts.block.DuctHitHelper;
import com.mervyn.dynamiducts.blockentity.StructuralDuctBlockEntity;
import com.mervyn.dynamiducts.mixin.UseOnContextAccessor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class RelayItem extends Item {

  public RelayItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    var level = context.getLevel();
    var pos = context.getClickedPos();
    var hitResult = ((UseOnContextAccessor) context).dynamiducts$getHitResult();

    if (!level.isClientSide()
        && level.getBlockEntity(pos) instanceof StructuralDuctBlockEntity ductBE) {
      var hit = DuctHitHelper.resolve(ductBE.getBlockState(), ductBE, pos, hitResult);
      var side = hit.side();
      if (ductBE.getAttachment(side) == null) {
        ductBE.setAttachment(side, new Relay(ductBE, side));
        if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
          context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
      }
    }
    return InteractionResult.PASS;
  }
}
