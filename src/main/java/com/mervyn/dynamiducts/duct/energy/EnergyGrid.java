package com.mervyn.dynamiducts.duct.energy;

import com.mervyn.dynamiducts.core.network.NetworkGrid;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class EnergyGrid extends NetworkGrid<EnergyDuctUnit> {

  protected final GridEnergyStorage storage;
  protected final int transferLimit;
  protected final int capacityPerDuct;
  private int currentEnergyShare;
  private int extraEnergy;

  public EnergyGrid(ServerLevel level, int transferLimit, int capacityPerDuct) {
    super(level);
    this.transferLimit = transferLimit;
    this.capacityPerDuct = capacityPerDuct;
    this.storage = new GridEnergyStorage(capacityPerDuct, transferLimit);
  }

  @Override
  public void addNode(EnergyDuctUnit unit) {
    super.addNode(unit);
    int stored = unit.getEnergyForGrid();
    if (stored > 0) {
      storage.modifyEnergyStored(stored);
      unit.setEnergyForGrid(0);
    }
  }

  @Override
  public void removeBlock(EnergyDuctUnit unit) {
    if (unit.isNode() && !nodeSet.isEmpty()) {
      unit.setEnergyForGrid(getNodeShare(unit));
    }
    super.removeBlock(unit);
  }

  @Override
  public void balanceGrid() {
    storage.setCapacity(Math.max(1, nodeSet.size()) * capacityPerDuct);
  }

  @Override
  public void tickGrid() {
    super.tickGrid();
    if (nodeSet.isEmpty() || storage.getAmountAsLong() <= 0) return;

    int stored = (int) storage.getAmountAsLong();
    currentEnergyShare = stored / nodeSet.size();
    extraEnergy = stored % nodeSet.size();

    beginTick();
    try {
      for (EnergyDuctUnit node : getNodeSnapshot()) {
        if (node.getGrid() != this) continue;
        if (!node.tickPass(0) || node.getGrid() == null) break;
      }
    } finally {
      endTick();
    }
  }

  @Override
  public boolean canAddBlock(EnergyDuctUnit block) {
    return block.getTransferLimit() == transferLimit;
  }

  @Override
  public boolean canGridsMerge(NetworkGrid<?> other) {
    return super.canGridsMerge(other) && ((EnergyGrid) other).transferLimit == this.transferLimit;
  }

  public int getSendableEnergy() {
    return Math.min(transferLimit, currentEnergyShare == 0 ? extraEnergy : currentEnergyShare);
  }

  public void useEnergy(int amount) {
    try (var tx = Transaction.openRoot()) {
      storage.extract(amount, tx);
      tx.commit();
    }
    if (amount > currentEnergyShare) {
      extraEnergy -= (amount - currentEnergyShare);
      extraEnergy = Math.max(0, extraEnergy);
    }
  }

  public int receiveEnergy(int maxReceive, TransactionContext ctx) {
    return storage.insert(maxReceive, ctx);
  }

  public boolean isPowered() {
    return storage.getAmountAsLong() > 0;
  }

  public int getNodeShare(EnergyDuctUnit unit) {
    int stored = (int) storage.getAmountAsLong();
    if (nodeSet.size() <= 1) return stored;
    if (isFirstBlock(unit)) {
      return stored / nodeSet.size() + stored % nodeSet.size();
    }
    return stored / nodeSet.size();
  }

  public GridEnergyStorage getStorage() {
    return storage;
  }

  public int getTransferLimit() {
    return transferLimit;
  }
}
