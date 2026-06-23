package com.mervyn.dynamiducts.duct.fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class FluidGridTank implements ResourceHandler<FluidResource> {

  private FluidStack fluid = FluidStack.EMPTY;
  private int capacity;
  private int throughput;

  public FluidGridTank(int capacity, int throughput) {
    this.capacity = capacity;
    this.throughput = throughput;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity;
    if (!fluid.isEmpty() && fluid.getAmount() > capacity) {
      fluid.setAmount(capacity);
    }
  }

  public void setThroughput(int throughput) {
    this.throughput = throughput;
  }

  public int getThroughput() {
    return throughput;
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public FluidResource getResource(int index) {
    return FluidResource.of(fluid);
  }

  @Override
  public long getAmountAsLong(int index) {
    return fluid.getAmount();
  }

  @Override
  public long getCapacityAsLong(int index, FluidResource resource) {
    return capacity;
  }

  @Override
  public boolean isValid(int index, FluidResource resource) {
    return true;
  }

  @Override
  public int insert(int index, FluidResource resource, int amount, TransactionContext ctx) {
    if (resource.isEmpty()) return 0;

    int toFill;
    FluidStack before = fluid.copy();

    if (fluid.isEmpty()) {
      toFill = Math.min(capacity, amount);
      fluid = resource.toStack(toFill);
    } else if (resource.matches(fluid)) {
      toFill = Math.min(capacity - fluid.getAmount(), amount);
      fluid.grow(toFill);
    } else {
      return 0;
    }

    if (toFill > 0) {
      new SnapshotJournal<FluidStack>() {
        @Override
        protected FluidStack createSnapshot() { return before; }
        @Override
        protected void revertToSnapshot(FluidStack snapshot) { fluid = snapshot; }
      }.updateSnapshots(ctx);
    }
    return toFill;
  }

  @Override
  public int extract(int index, FluidResource resource, int amount, TransactionContext ctx) {
    if (fluid.isEmpty() || resource.isEmpty() || !resource.matches(fluid)) return 0;

    int drained = Math.min(fluid.getAmount(), amount);
    if (drained > 0) {
      FluidStack before = fluid.copy();
      fluid.shrink(drained);
      if (fluid.getAmount() <= 0) {
        fluid = FluidStack.EMPTY;
      }
      new SnapshotJournal<FluidStack>() {
        @Override
        protected FluidStack createSnapshot() { return before; }
        @Override
        protected void revertToSnapshot(FluidStack snapshot) { fluid = snapshot; }
      }.updateSnapshots(ctx);
    }
    return drained;
  }

  public FluidStack getFluid() {
    return fluid;
  }

  public boolean isEmpty() {
    return fluid.isEmpty();
  }

  public void setFluid(FluidStack fluid) {
    this.fluid = fluid;
  }
}
