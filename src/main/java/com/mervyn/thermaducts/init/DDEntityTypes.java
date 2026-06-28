package com.mervyn.thermaducts.init;

import com.mervyn.thermaducts.ThermaDucts;
import com.mervyn.thermaducts.duct.transport.TransportEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DDEntityTypes {

  public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
      DeferredRegister.create(Registries.ENTITY_TYPE, ThermaDucts.MODID);

  public static final DeferredHolder<EntityType<?>, EntityType<TransportEntity>> TRANSPORT =
      ENTITY_TYPES.register(
          "transport",
          () ->
              EntityType.Builder.<TransportEntity>of(TransportEntity::new, MobCategory.MISC)
                  .sized(0.25F, 0.25F)
                  .noSummon()
                  .fireImmune()
                  .build(
                      net.minecraft.resources.ResourceKey.create(
                          net.minecraft.core.registries.Registries.ENTITY_TYPE,
                          net.minecraft.resources.Identifier.fromNamespaceAndPath(
                              com.mervyn.thermaducts.ThermaDucts.MODID, "transport"))));
}
