package com.mervyn.dynamiducts.duct.fluid;

import com.mervyn.dynamiducts.core.attachment.Attachment;
import com.mervyn.dynamiducts.core.attachment.ConnectionBase;
import com.mervyn.dynamiducts.core.network.NetworkGrid;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class FluidGrid extends NetworkGrid<FluidDuctUnit> {

  protected final FluidGridTank tank;
  protected final int capacityPerDuct;
  protected final int throughputPerDuct;
  private FluidStack lastSyncedFluid = FluidStack.EMPTY;
  private int lastSyncedRenderLevel = -1;

  public FluidGrid(ServerLevel level, int capacityPerDuct, int throughputPerDuct) {
    super(level);
    this.capacityPerDuct = capacityPerDuct;
    this.throughputPerDuct = throughputPerDuct;
    this.tank = new FluidGridTank(capacityPerDuct, throughputPerDuct);
  }

  @Override
  public void addBlock(FluidDuctUnit unit) {
    super.addBlock(unit);
    collectFluidFrom(unit);
  }

  private void collectFluidFrom(FluidDuctUnit unit) {
    FluidStack fluid = unit.getFluidForGrid();
    if (!fluid.isEmpty()) {
      try (var tx = Transaction.openRoot()) {
        tank.insert(0, FluidResource.of(fluid), fluid.getAmount(), tx);
        tx.commit();
      }
      unit.setFluidForGrid(FluidStack.EMPTY);
    }
  }

  @Override
  public void removeBlock(FluidDuctUnit unit) {
    if (!tank.isEmpty() && !unit.isBeingDestroyed()) {
      FluidStack share = getNodeShare(unit);
      if (!share.isEmpty()) {
        unit.setFluidForGrid(share);
        try (var tx = Transaction.openRoot()) {
          tank.extract(0, FluidResource.of(share), share.getAmount(), tx);
          tx.commit();
        }
      }
    }
    super.removeBlock(unit);
  }

  @Override
  public void onMergeFrom(NetworkGrid<?> source) {
    if (source instanceof FluidGrid fluidSource && !fluidSource.tank.isEmpty()) {
      FluidResource res = fluidSource.tank.getResource(0);
      if (!res.isEmpty()) {
        int amount = (int) fluidSource.tank.getAmountAsLong(0);
        try (var tx = Transaction.openRoot()) {
          tank.insert(0, res, amount, tx);
          tx.commit();
        }
      }
    }
  }

  @Override
  public void balanceGrid() {
    int totalDucts = Math.max(1, nodeSet.size() + idleSet.size());
    tank.setCapacity(totalDucts * capacityPerDuct);
    tank.setThroughput(throughputPerDuct);
  }

  @Override
  public void onMajorGridChange() {
    super.onMajorGridChange();
    lastSyncedFluid = FluidStack.EMPTY;
    lastSyncedRenderLevel = -1;
  }

  @Override
  public void tickGrid() {
    super.tickGrid();
    beginTick();
    try {
      tickTemperature();
      if (nodeSet.isEmpty() || tank.isEmpty()) {
        syncVisualIfChanged();
        return;
      }

      int available = getEffectiveThroughput();
      if (available <= 0) {
        syncVisualIfChanged();
        return;
      }

      for (FluidDuctUnit node : getNodeSnapshot()) {
        if (node.getGrid() != this) continue;
        for (Direction dir : Direction.values()) {
          ResourceHandler<FluidResource> target = node.getTileCache(dir);
          if (target == null) continue;
          if (hasServoOnSide(node, dir)) continue;

          FluidResource toSend = tank.getResource(0);
          if (toSend.isEmpty()) break;

          try (var tx = Transaction.openRoot()) {
            int drained = tank.extract(0, toSend, available, tx);
            if (drained <= 0) continue;

            int filled = target.insert(toSend, drained, tx);
            if (filled > 0) {
              if (filled < drained) {
                tank.insert(0, toSend, drained - filled, tx);
              }
              tx.commit();
              available -= filled;
              if (available <= 0) {
                syncVisualIfChanged();
                return;
              }
            }
          }
        }
      }
      syncVisualIfChanged();
    } finally {
      endTick();
    }
  }

  private void tickTemperature() {
    List<FluidDuctUnitTemperate> tempUnits = new ArrayList<>();
    for (FluidDuctUnit unit : getNodeSnapshot()) {
      if (unit instanceof FluidDuctUnitTemperate t) tempUnits.add(t);
    }
    for (FluidDuctUnit unit : getIdleSnapshot()) {
      if (unit instanceof FluidDuctUnitTemperate t) tempUnits.add(t);
    }
    for (FluidDuctUnitTemperate temperate : tempUnits) {
      temperate.tickTemperature();
      if (level instanceof ServerLevel serverLevel && serverLevel.getRandom().nextInt(10) == 0) {
        temperate.spawnSmokeParticles(serverLevel);
      }
    }
  }

  private int getEffectiveThroughput() {
    if (tank.isEmpty()) return 0;
    int capacity = (int) tank.getCapacityAsLong(0, tank.getResource(0));
    int amount = (int) tank.getAmountAsLong(0);
    int throughput = tank.getThroughput();

    if (amount >= capacity * 3 / 4) return throughput;
    if (amount <= capacity / 4) return throughput / 2;
    return throughput * 3 / 4;
  }

  public FluidGridTank getTank() {
    return tank;
  }

  public int fill(FluidStack resource, boolean simulate) {
    try (var tx = Transaction.openRoot()) {
      int filled = tank.insert(0, FluidResource.of(resource), resource.getAmount(), tx);
      if (!simulate && filled > 0) {
        tx.commit();
        syncVisualIfChanged();
      }
      return filled;
    }
  }

  public FluidStack getNodeShare(FluidDuctUnit unit) {
    if (tank.isEmpty()) return FluidStack.EMPTY;
    int totalDucts = nodeSet.size() + idleSet.size();
    if (totalDucts <= 0) return FluidStack.EMPTY;
    FluidResource res = tank.getResource(0);
    if (res.isEmpty()) return FluidStack.EMPTY;
    int amount = (int) tank.getAmountAsLong(0);
    int share = amount / totalDucts;
    if (share <= 0) return FluidStack.EMPTY;
    return res.toStack(share);
  }

  public void syncVisualIfChanged() {
    FluidResource res = tank.getResource(0);
    FluidStack current =
        res.isEmpty() ? FluidStack.EMPTY : res.toStack((int) tank.getAmountAsLong(0));
    int renderLevel = getRenderLevel();
    if (isSameVisual(lastSyncedFluid, current) && lastSyncedRenderLevel == renderLevel) return;

    lastSyncedFluid = current.copy();
    lastSyncedRenderLevel = renderLevel;

    FluidStack renderFluid =
        current.isEmpty() || renderLevel <= 0
            ? FluidStack.EMPTY
            : current.copyWithAmount(renderLevel);

    for (FluidDuctUnit unit : getNodeSnapshot()) syncVisualToClient(unit, renderFluid);
    for (FluidDuctUnit unit : getIdleSnapshot()) syncVisualToClient(unit, renderFluid);
  }

  private void syncVisualToClient(FluidDuctUnit unit, FluidStack fluid) {
    unit.setRenderFluid(fluid);
    unit.getParent().setChanged();
    level.sendBlockUpdated(
        unit.getPos(),
        unit.getParent().getBlockState(),
        unit.getParent().getBlockState(),
        Block.UPDATE_CLIENTS);
  }

  private int getRenderLevel() {
    if (tank.isEmpty()) return 0;
    long cap = tank.getCapacityAsLong(0, tank.getResource(0));
    if (cap <= 0) return 0;

    long fullPercent = 10000L * tank.getAmountAsLong(0) / cap;
    if (fullPercent <= 700) {
      return 1;
    }
    if (fullPercent <= 2500) {
      return 2;
    }
    if (fullPercent <= 4500) {
      return 3;
    }
    if (fullPercent <= 6500) {
      return 4;
    }
    if (fullPercent <= 8500) {
      return 5;
    }
    return 6;
  }

  private static boolean hasServoOnSide(FluidDuctUnit unit, Direction side) {
    Attachment att = unit.getParent().getAttachment(side);
    return att instanceof ConnectionBase conn && conn.isServo();
  }

  private static boolean isSameVisual(FluidStack a, FluidStack b) {
    if (a.isEmpty() || b.isEmpty()) return a.isEmpty() == b.isEmpty();
    return FluidStack.isSameFluidSameComponents(a, b);
  }
}
