package com.mervyn.dynamiducts.attachment.retriever;

import com.mervyn.dynamiducts.DynamiDucts;
import com.mervyn.dynamiducts.blockentity.DuctBlockEntity;
import com.mervyn.dynamiducts.core.attachment.AttachmentTier;
import com.mervyn.dynamiducts.core.attachment.ConnectionBase;
import com.mervyn.dynamiducts.core.duct.DuctToken;
import com.mervyn.dynamiducts.duct.fluid.FluidDuctUnit;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class RetrieverFluid extends ConnectionBase {

  public static final Identifier ID =
      Identifier.fromNamespaceAndPath(DynamiDucts.MODID, "retriever_fluid");

  public RetrieverFluid(DuctBlockEntity parent, Direction side, AttachmentTier tier) {
    super(parent, side, tier);
  }

  @Override
  public Identifier getId() {
    return ID;
  }

  @Override
  public boolean isRetriever() {
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

    int maxInput = tier.fluidDrainAmount();
    if (maxInput <= 0) return;

    for (FluidDuctUnit node : grid.getNodeSnapshot()) {
      for (Direction dir : Direction.values()) {
        ResourceHandler<FluidResource> source = node.getTileCache(dir);
        if (source == null) continue;

        FluidResource toDrain = source.getResource(0);
        if (toDrain.isEmpty()) continue;
        if (!filter.matchesFluid(toDrain.toStack((int) source.getAmountAsLong(0)))) continue;

        try (var tx = Transaction.openRoot()) {
          int drained = (int) Math.min(source.getAmountAsLong(0), maxInput);
          if (drained <= 0) continue;

          int extracted = source.extract(0, toDrain, drained, tx);
          if (extracted <= 0) continue;

          int filled = grid.getTank().insert(0, toDrain, extracted, tx);
          if (filled > 0) {
            if (filled < extracted) {
              source.insert(0, toDrain, extracted - filled, tx);
            }
            tx.commit();
            grid.syncVisualIfChanged();
            return;
          }
        }
      }
    }
  }
}
