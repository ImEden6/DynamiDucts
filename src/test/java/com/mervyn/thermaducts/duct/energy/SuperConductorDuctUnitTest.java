package com.mervyn.thermaducts.duct.energy;

import static org.junit.jupiter.api.Assertions.*;

import com.mervyn.thermaducts.DuctUnitTestBase;
import com.mervyn.thermaducts.core.duct.DuctToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SuperConductorDuctUnitTest extends DuctUnitTestBase {

  private SuperConductorDuctUnit unit;

  @BeforeEach
  void setUp() {
    unit = new SuperConductorDuctUnit(parent);
  }

  @Test
  void constructor_createsUnit() {
    assertNotNull(unit);
    assertSame(parent, unit.getParent());
  }

  @Test
  void getTransferLimit_returnsMaxValue() {
    assertEquals(Integer.MAX_VALUE, unit.getTransferLimit());
  }

  @Test
  void getToken_returnsEnergy() {
    assertEquals(DuctToken.ENERGY, unit.getToken());
  }

  @Test
  void getPathWeight_inheritsOne() {
    assertEquals(1, unit.getPathWeight());
  }

  @Test
  void createGrid_returnsSuperConductorGrid() {
    EnergyGrid grid = unit.createGrid(level);
    assertNotNull(grid);
    assertInstanceOf(SuperConductorGrid.class, grid);
  }

  @Test
  void canConnectTo_otherSuperConductor_returnsTrue() {
    SuperConductorDuctUnit other = new SuperConductorDuctUnit(parent);
    assertTrue(unit.canConnectTo(other));
  }

  @Test
  void canConnectTo_regularEnergyDuctUnit_returnsFalse() {
    EnergyDuctUnit other = new EnergyDuctUnit(parent, 1000, 5000);
    assertFalse(unit.canConnectTo(other));
  }

  @Test
  void canConnectTo_regularEnergyDuctUnit_reverseCheck_returnsTrueFromOther() {
    EnergyDuctUnit regular = new EnergyDuctUnit(parent, 1000, 5000);
    assertTrue(regular.canConnectTo(unit));
  }

  @Test
  void tickPass_returnsTrueImmediately() {
    assertTrue(unit.tickPass(0));
    assertTrue(unit.tickPass(1));
  }

  @Test
  void getEnergyForGrid_inheritsZero() {
    assertEquals(0, unit.getEnergyForGrid());
  }

  @Test
  void setEnergyForGrid_inherited() {
    unit.setEnergyForGrid(999);
    assertEquals(999, unit.getEnergyForGrid());
  }
}
