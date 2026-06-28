package com.mervyn.thermaducts.client;

import com.mervyn.thermaducts.ThermaDucts;
import com.mervyn.thermaducts.item.AttachmentItem;
import com.mervyn.thermaducts.item.DDTooltipHelper;
import com.mervyn.thermaducts.item.DuctBlockItem;
import com.mervyn.thermaducts.item.RelayItem;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = ThermaDucts.MODID, value = Dist.CLIENT)
public class ClientTooltipHandler {

  @SubscribeEvent
  public static void onItemTooltip(ItemTooltipEvent event) {
    ItemStack stack = event.getItemStack();
    Item item = stack.getItem();
    List<Component> tooltip = event.getToolTip();

    if (item instanceof DuctBlockItem ductItem) {
      DDTooltipHelper.appendDuctTooltip(stack, ductItem.getBlock(), tooltip);
    } else if (item instanceof AttachmentItem attachmentItem) {
      DDTooltipHelper.appendAttachmentTooltip(
          attachmentItem.getTooltipType(), attachmentItem.getTier(), tooltip);
    } else if (item instanceof RelayItem) {
      DDTooltipHelper.appendRelayTooltip(tooltip);
    }
  }
}
