package com.mervyn.dynamiducts.attachment.servo;

import com.mervyn.dynamiducts.DynamiDucts;
import com.mervyn.dynamiducts.blockentity.DuctBlockEntity;
import com.mervyn.dynamiducts.core.attachment.AttachmentTier;
import com.mervyn.dynamiducts.core.attachment.ConnectionBase;
import com.mervyn.dynamiducts.core.duct.DuctToken;
import com.mervyn.dynamiducts.duct.fluid.FluidDuctUnit;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

@SuppressWarnings("removal")
public class ServoFluid extends ConnectionBase {

  public static final Identifier ID =
      Identifier.fromNamespaceAndPath(DynamiDucts.MODID, "servo_fluid");

  public ServoFluid(DuctBlockEntity parent, Direction side, AttachmentTier tier) {
    super(parent, side, tier);
  }

  @Override
  public Identifier getId() {
    return ID;
  }

  @Override
  public boolean isServo() {
    return true;
  }

  @Override
  public boolean canSend() {
    return true;
  }

  @Override
  public void tick() {
    if (!isActive()) return;
    performAction();
  }

  @Override
  protected void performAction() {
    var level = parent.getLevel();
    if (level == null) return;

    var unit = parent.getDuctUnit(DuctToken.FLUID);
    if (!(unit instanceof FluidDuctUnit fluidUnit)) return;
    var grid = fluidUnit.getGrid();
    if (grid == null) return;

    IFluidHandler source =
        IFluidHandler.of(
            level.getCapability(
                Capabilities.Fluid.BLOCK, parent.getBlockPos().relative(side), side.getOpposite()));
    if (source == null) return;

    int maxInput = tier.fluidDrainAmount();
    if (maxInput <= 0) return;

    FluidStack drained = source.drain(maxInput, IFluidHandler.FluidAction.SIMULATE);
    if (drained.isEmpty()) return;
    if (!filter.matchesFluid(drained)) return;

    int filled = grid.fill(drained, IFluidHandler.FluidAction.EXECUTE);
    if (filled > 0) {
      source.drain(filled, IFluidHandler.FluidAction.EXECUTE);
    }
  }
}
