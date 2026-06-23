package com.mervyn.dynamiducts.duct.energy;

import com.mervyn.dynamiducts.blockentity.DuctBlockEntity;
import com.mervyn.dynamiducts.core.duct.DuctToken;
import com.mervyn.dynamiducts.core.duct.DuctUnit;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class EnergyDuctUnit extends DuctUnit<EnergyDuctUnit, EnergyGrid, EnergyHandler> {

  private final int transferLimit;
  private final int capacityPerDuct;
  private int energyForGrid;

  public EnergyDuctUnit(DuctBlockEntity parent, int transferLimit, int capacityPerDuct) {
    super(parent);
    this.transferLimit = transferLimit;
    this.capacityPerDuct = capacityPerDuct;
  }

  @Override
  protected EnergyHandler[] createTileCacheArray() {
    return new EnergyHandler[6];
  }

  @Override
  protected EnergyDuctUnit[] createDuctCacheArray() {
    return new EnergyDuctUnit[6];
  }

  @Override
  public DuctToken getToken() {
    return DuctToken.ENERGY;
  }

  @Override
  public EnergyGrid createGrid(ServerLevel level) {
    return new EnergyGrid(level, transferLimit, capacityPerDuct);
  }

  @Override
  public int getPathWeight() {
    return 1;
  }

  @Override
  public EnergyHandler cacheTile(Direction side) {
    if (parent.getLevel() == null) return null;
    return parent.getLevel().getCapability(
        Capabilities.Energy.BLOCK,
        parent.getBlockPos().relative(side),
        side.getOpposite());
  }

  @Override
  public boolean tickPass(int pass) {
    if (grid == null) return false;
    if (pass != 0) return true;

    int sendable = grid.getSendableEnergy();
    if (sendable <= 0) return true;

    for (Direction dir : Direction.values()) {
      EnergyHandler target = tileCache[dir.ordinal()];
      if (target == null) continue;

      try (var tx = Transaction.openRoot()) {
        int sent = target.insert(sendable, tx);
        if (sent > 0) {
          tx.commit();
          grid.useEnergy(sent);
          sendable = grid.getSendableEnergy();
          if (sendable <= 0) break;
        }
      }
    }
    return true;
  }

  public EnergyHandler createCapability(Direction side) {
    return new EnergyHandler() {
      @Override
      public int insert(int maxReceive, TransactionContext ctx) {
        if (grid == null) return 0;
        return grid.receiveEnergy(maxReceive, ctx);
      }

      @Override
      public int extract(int maxExtract, TransactionContext ctx) {
        return 0;
      }

      @Override
      public long getAmountAsLong() {
        return grid != null ? grid.getStorage().getAmountAsLong() : 0;
      }

      @Override
      public long getCapacityAsLong() {
        return grid != null ? grid.getStorage().getCapacityAsLong() : 0;
      }
    };
  }

  @Override
  public boolean canConnectTo(EnergyDuctUnit other) {
    return other.transferLimit == this.transferLimit
        || this instanceof SuperConductorDuctUnit
        || other instanceof SuperConductorDuctUnit;
  }

  public int getTransferLimit() {
    return transferLimit;
  }

  public int getEnergyForGrid() {
    return energyForGrid;
  }

  public void setEnergyForGrid(int energy) {
    this.energyForGrid = energy;
  }

  @Override
  public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
    if (grid != null && isNode()) {
      tag.putInt("EnergyShare", grid.getNodeShare(this));
    } else {
      tag.putInt("EnergyShare", energyForGrid);
    }
  }

  @Override
  public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
    energyForGrid = tag.getInt("EnergyShare").orElse(0);
  }
}
