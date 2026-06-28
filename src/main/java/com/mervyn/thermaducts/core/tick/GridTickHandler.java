package com.mervyn.dynamiducts.core.tick;

import com.mervyn.dynamiducts.DynamiDucts;
import com.mervyn.dynamiducts.core.network.NetworkManager;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = DynamiDucts.MODID)
public class GridTickHandler {

  @SubscribeEvent
  public static void onLevelTickPre(LevelTickEvent.Pre event) {
    if (event.getLevel() instanceof ServerLevel serverLevel) {
      NetworkManager.get(serverLevel).tickStart();
    }
  }

  @SubscribeEvent
  public static void onLevelTickPost(LevelTickEvent.Post event) {
    if (event.getLevel() instanceof ServerLevel serverLevel) {
      NetworkManager.get(serverLevel).tickEnd();
    }
  }

  @SubscribeEvent
  public static void onLevelUnload(LevelEvent.Unload event) {
    if (event.getLevel() instanceof ServerLevel serverLevel) {
      NetworkManager.remove(serverLevel);
    }
  }
}
