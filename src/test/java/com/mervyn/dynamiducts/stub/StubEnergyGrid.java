package com.mervyn.dynamiducts.stub;

import com.mervyn.dynamiducts.duct.energy.EnergyGrid;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class StubEnergyGrid extends EnergyGrid {

  private int sendableEnergy = 0;
  private int usedEnergy = 0;
  private int receiveCallCount;
  private int lastMaxReceive;

  public StubEnergyGrid(
      ServerLevel level, int transferLimit, int capacityPerDuct) {
    super(level, transferLimit, capacityPerDuct);
  }

  public void setSendableEnergy(int amount) {
    this.sendableEnergy = amount;
  }

  @Override
  public int getSendableEnergy() {
    return sendableEnergy;
  }

  @Override
  public void useEnergy(int amount) {
    usedEnergy += amount;
    super.useEnergy(amount);
  }

  @Override
  public int receiveEnergy(int maxReceive, TransactionContext ctx) {
    receiveCallCount++;
    lastMaxReceive = maxReceive;
    return storage.insert(maxReceive, ctx);
  }

  public int getUsedEnergy() {
    return usedEnergy;
  }

  public int getReceiveCallCount() {
    return receiveCallCount;
  }

  public int getLastMaxReceive() {
    return lastMaxReceive;
  }

  public void reset() {
    usedEnergy = 0;
    receiveCallCount = 0;
    lastMaxReceive = 0;
  }
}
