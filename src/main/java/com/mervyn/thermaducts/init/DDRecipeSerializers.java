package com.mervyn.thermaducts.init;

import com.mervyn.thermaducts.ThermaDucts;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DDRecipeSerializers {

  public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
      DeferredRegister.create(Registries.RECIPE_SERIALIZER, ThermaDucts.MODID);
}
