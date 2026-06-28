package com.mervyn.dynamiducts.stub;

import com.mervyn.dynamiducts.duct.fluid.FluidGrid;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class StubFluidGrid extends FluidGrid {

  private int fillResult = 0;
  private FluidStack lastFilled;
  private boolean lastSimulate;
  private int fillCallCount;

  public StubFluidGrid(
      ServerLevel level, int capacityPerDuct, int throughputPerDuct) {
    super(level, capacityPerDuct, throughputPerDuct);
  }

  public void setFillResult(int amount) {
    this.fillResult = amount;
  }

  @Override
  public int fill(FluidStack resource, boolean simulate) {
    fillCallCount++;
    lastFilled = resource;
    lastSimulate = simulate;
    try (var tx = Transaction.openRoot()) {
      tank.insert(0, FluidResource.of(resource), resource.getAmount(), tx);
      if (!simulate) tx.commit();
    }
    return fillResult;
  }

  public FluidStack getLastFilled() {
    return lastFilled;
  }

  public boolean isLastSimulate() {
    return lastSimulate;
  }

  public int getFillCallCount() {
    return fillCallCount;
  }

  public void reset() {
    fillCallCount = 0;
    lastFilled = null;
    lastSimulate = false;
  }
}
