package com.mervyn.thermaducts.init;

import com.mervyn.thermaducts.ThermaDucts;
import com.mervyn.thermaducts.menu.AttachmentMenu;
import com.mervyn.thermaducts.menu.RelayMenu;
import com.mervyn.thermaducts.menu.TransportConfigMenu;
import com.mervyn.thermaducts.menu.TransportMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DDMenuTypes {

  public static final DeferredRegister<MenuType<?>> MENU_TYPES =
      DeferredRegister.create(Registries.MENU, ThermaDucts.MODID);

  public static final DeferredHolder<MenuType<?>, MenuType<AttachmentMenu>> ATTACHMENT_MENU =
      MENU_TYPES.register(
          "attachment", () -> IMenuTypeExtension.create(AttachmentMenu::fromNetwork));

  public static final DeferredHolder<MenuType<?>, MenuType<RelayMenu>> RELAY_MENU =
      MENU_TYPES.register("relay", () -> IMenuTypeExtension.create(RelayMenu::fromNetwork));

  public static final DeferredHolder<MenuType<?>, MenuType<TransportMenu>> TRANSPORT_MENU =
      MENU_TYPES.register("transport", () -> IMenuTypeExtension.create(TransportMenu::fromNetwork));

  public static final DeferredHolder<MenuType<?>, MenuType<TransportConfigMenu>>
      TRANSPORT_CONFIG_MENU =
          MENU_TYPES.register(
              "transport_config",
              () -> IMenuTypeExtension.create(TransportConfigMenu::fromNetwork));
}
