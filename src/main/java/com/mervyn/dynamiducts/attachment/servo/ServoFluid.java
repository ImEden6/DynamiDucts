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
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

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

    ResourceHandler<FluidResource> source = level.getCapability(
        Capabilities.Fluid.BLOCK, parent.getBlockPos().relative(side), side.getOpposite());
    if (source == null) return;

    int maxInput = tier.fluidDrainAmount();
    if (maxInput <= 0) return;

    FluidResource toDrain = source.getResource(0);
    if (toDrain.isEmpty()) return;
    if (!filter.matchesFluid(toDrain.toStack((int) source.getAmountAsLong(0)))) return;

    try (var tx = Transaction.openRoot()) {
      int drained = (int) Math.min(source.getAmountAsLong(0), maxInput);
      int extracted = source.extract(0, toDrain, drained, tx);
      if (extracted <= 0) return;

      int filled = grid.getTank().insert(0, toDrain, extracted, tx);
      if (filled > 0) {
        if (filled < extracted) {
          source.insert(0, toDrain, extracted - filled, tx);
        }
        tx.commit();
        grid.syncVisualIfChanged();
      }
    }
  }
}
