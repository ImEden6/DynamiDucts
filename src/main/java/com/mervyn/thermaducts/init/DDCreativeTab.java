package com.mervyn.thermaducts.init;

import com.mervyn.thermaducts.ThermaDucts;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DDCreativeTab {

  public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
      DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ThermaDucts.MODID);

  public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB =
      CREATIVE_TABS.register(
          "main",
          () ->
              CreativeModeTab.builder()
                  .title(Component.translatable("itemGroup.thermaducts"))
                  .icon(() -> DDItems.ENERGY_DUCT_BASIC.get().getDefaultInstance())
                  .displayItems(
                      (parameters, output) -> {
                        DDItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                      })
                  .build());
}
