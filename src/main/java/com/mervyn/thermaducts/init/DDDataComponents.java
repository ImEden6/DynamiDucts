package com.mervyn.thermaducts.init;

import com.mervyn.thermaducts.ThermaDucts;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class DDDataComponents {

  public static final DeferredRegister.DataComponents DATA_COMPONENTS =
      DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ThermaDucts.MODID);

  public static final DeferredHolder<DataComponentType<?>, DataComponentType<Byte>>
      DUCT_PATH_WEIGHT =
          DATA_COMPONENTS.registerComponentType(
              "duct_path_weight", builder -> builder.persistent(Codec.BYTE).cacheEncoding());

  private DDDataComponents() {}
}
