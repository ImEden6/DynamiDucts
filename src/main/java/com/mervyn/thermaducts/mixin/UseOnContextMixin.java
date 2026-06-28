package com.mervyn.thermaducts.mixin;

import com.mervyn.thermaducts.accessor.UseOnContextAccessor;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(UseOnContext.class)
public interface UseOnContextMixin extends UseOnContextAccessor {

  @Override
  @Accessor("hitResult")
  BlockHitResult thermaducts$getHitResult();
}
