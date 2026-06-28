package com.mervyn.thermaducts.duct.energy;

import com.mervyn.thermaducts.core.network.NetworkGrid;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class SuperConductorGrid extends EnergyGrid {

  public SuperConductorGrid(ServerLevel level) {
    super(level, Integer.MAX_VALUE, 0);
  }

  @Override
  public void tickGrid() {
    if (nodeSet.isEmpty()) return;

    beginTick();
    try {
      List<EnergyDuctUnit> snapshot = getNodeSnapshot();
      for (EnergyDuctUnit node : snapshot) {
        if (node.getGrid() != this) continue;
        for (Direction dir : Direction.values()) {
          EnergyHandler source = node.getTileCache(dir);
          if (source == null) continue;

          try (var tx = Transaction.openRoot()) {
            int available = source.extract(Integer.MAX_VALUE, tx);
            if (available <= 0) continue;

            int distributed = distributeToOthers(node, available, snapshot, tx);
            if (distributed > 0) {
              if (distributed < available) {
                source.insert(available - distributed, tx);
              }
              tx.commit();
            }
          }
        }
      }
    } finally {
      endTick();
    }
  }

  private int distributeToOthers(
      EnergyDuctUnit sourceNode,
      int available,
      List<EnergyDuctUnit> snapshot,
      TransactionContext ctx) {
    int totalSent = 0;
    for (EnergyDuctUnit targetNode : snapshot) {
      if (targetNode == sourceNode) continue;
      if (targetNode.getGrid() != this) continue;
      for (Direction dir : Direction.values()) {
        EnergyHandler target = targetNode.getTileCache(dir);
        if (target == null) continue;

        int sent = target.insert(available - totalSent, ctx);
        totalSent += sent;
        if (totalSent >= available) return totalSent;
      }
    }
    return totalSent;
  }

  @Override
  public boolean canAddBlock(EnergyDuctUnit block) {
    return block instanceof SuperConductorDuctUnit;
  }

  @Override
  public boolean canGridsMerge(NetworkGrid<?> other) {
    return other instanceof SuperConductorGrid;
  }

  @Override
  public int receiveEnergy(int maxReceive, TransactionContext ctx) {
    return 0;
  }
}
