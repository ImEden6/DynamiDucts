package com.mervyn.thermaducts.duct.energy;

import com.mervyn.thermaducts.blockentity.DuctBlockEntity;
import net.minecraft.server.level.ServerLevel;

public class SuperConductorDuctUnit extends EnergyDuctUnit {

  public SuperConductorDuctUnit(DuctBlockEntity parent) {
    super(parent, Integer.MAX_VALUE, 0);
  }

  @Override
  public EnergyGrid createGrid(ServerLevel level) {
    return new SuperConductorGrid(level);
  }

  @Override
  public boolean canConnectTo(EnergyDuctUnit other) {
    return other instanceof SuperConductorDuctUnit;
  }

  @Override
  public boolean tickPass(int pass) {
    return true;
  }
}
