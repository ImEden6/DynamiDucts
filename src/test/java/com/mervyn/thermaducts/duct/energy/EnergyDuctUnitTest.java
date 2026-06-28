package com.mervyn.thermaducts.duct.energy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mervyn.thermaducts.DuctUnitTestBase;
import com.mervyn.thermaducts.blockentity.DuctBlockEntity;
import com.mervyn.thermaducts.core.duct.DuctToken;
import com.mervyn.thermaducts.stub.StubEnergyGrid;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnergyDuctUnitTest extends DuctUnitTestBase {

  private static final int TEST_TRANSFER_LIMIT = 1000;
  private static final int TEST_CAPACITY = 5000;

  private TestableEnergyDuctUnit unit;
  private StubEnergyGrid grid;

  private static class TestableEnergyDuctUnit extends EnergyDuctUnit {
    private final EnergyHandler[] testCaches = new EnergyHandler[6];

    TestableEnergyDuctUnit(DuctBlockEntity parent, int transferLimit, int capacityPerDuct) {
      super(parent, transferLimit, capacityPerDuct);
    }

    void setTileCache(Direction dir, EnergyHandler storage) {
      testCaches[dir.ordinal()] = storage;
    }

    @Override
    public EnergyHandler cacheTile(Direction side) {
      return testCaches[side.ordinal()];
    }
  }

  @BeforeEach
  void setUp() {
    unit = new TestableEnergyDuctUnit(parent, TEST_TRANSFER_LIMIT, TEST_CAPACITY);
    grid = new StubEnergyGrid(level, TEST_TRANSFER_LIMIT, TEST_CAPACITY);
    unit.setGrid(grid);
  }

  @Test
  void constructor_createsUnit() {
    assertNotNull(unit);
    assertSame(parent, unit.getParent());
  }

  @Test
  void getToken_returnsEnergy() {
    assertEquals(DuctToken.ENERGY, unit.getToken());
  }

  @Test
  void getPathWeight_returnsOne() {
    assertEquals(1, unit.getPathWeight());
  }

  @Test
  void getTransferLimit_returnsConstructorValue() {
    assertEquals(TEST_TRANSFER_LIMIT, unit.getTransferLimit());
  }

  @Test
  void getEnergyForGrid_returnsZeroInitially() {
    assertEquals(0, unit.getEnergyForGrid());
  }

  @Test
  void setEnergyForGrid_setsValue() {
    unit.setEnergyForGrid(500);
    assertEquals(500, unit.getEnergyForGrid());
  }

  @Test
  void createGrid_returnsEnergyGridWithCorrectParams() {
    EnergyGrid g = unit.createGrid(level);
    assertNotNull(g);
    assertInstanceOf(EnergyGrid.class, g);
  }

  @Test
  void canConnectTo_sameTransferLimit_returnsTrue() {
    EnergyDuctUnit other = new EnergyDuctUnit(parent, TEST_TRANSFER_LIMIT, TEST_CAPACITY);
    assertTrue(unit.canConnectTo(other));
  }

  @Test
  void canConnectTo_differentTransferLimit_returnsFalse() {
    EnergyDuctUnit other = new EnergyDuctUnit(parent, 500, TEST_CAPACITY);
    assertFalse(unit.canConnectTo(other));
  }

  @Test
  void canConnectTo_thisIsSuperConductor_returnsTrueForAny() {
    EnergyDuctUnit superCon = new SuperConductorDuctUnit(parent);
    EnergyDuctUnit other = new EnergyDuctUnit(parent, 500, TEST_CAPACITY);
    assertTrue(superCon.canConnectTo(other));
  }

  @Test
  void canConnectTo_otherIsSuperConductor_returnsTrue() {
    EnergyDuctUnit superCon = new SuperConductorDuctUnit(parent);
    assertTrue(unit.canConnectTo(superCon));
  }

  @Test
  void createCapability_insert_delegatesToGrid() {
    EnergyHandler cap = unit.createCapability(Direction.UP);
    grid.getStorage().modifyEnergyStored(200);
    try (var tx = Transaction.openRoot()) {
      int received = cap.insert(500, tx);
      assertTrue(received > 0);
      tx.commit();
    }
  }

  @Test
  void createCapability_insert_noGrid_returnsZero() {
    unit.setGrid(null);
    EnergyHandler cap = unit.createCapability(Direction.UP);
    try (var tx = Transaction.openRoot()) {
      assertEquals(0, cap.insert(500, tx));
    }
  }

  @Test
  void createCapability_extract_returnsZero() {
    EnergyHandler cap = unit.createCapability(Direction.UP);
    try (var tx = Transaction.openRoot()) {
      assertEquals(0, cap.extract(100, tx));
    }
  }

  @Test
  void createCapability_getAmountAsLong_delegatesToGrid() {
    EnergyHandler cap = unit.createCapability(Direction.UP);
    assertEquals(grid.getStorage().getAmountAsLong(), cap.getAmountAsLong());
  }

  @Test
  void createCapability_getCapacityAsLong_delegatesToGrid() {
    EnergyHandler cap = unit.createCapability(Direction.UP);
    assertEquals(grid.getStorage().getCapacityAsLong(), cap.getCapacityAsLong());
  }

  @Test
  void tickPass_wrongPass_returnsTrueWithoutAction() {
    boolean result = unit.tickPass(1);
    assertTrue(result);
    assertEquals(0, grid.getUsedEnergy());
  }

  @Test
  void tickPass_noGrid_returnsFalse() {
    unit.setGrid(null);
    assertFalse(unit.tickPass(0));
  }

  @Test
  void tickPass_noSendableEnergy_returnsTrue() {
    grid.setSendableEnergy(0);
    assertTrue(unit.tickPass(0));
  }

  @Test
  void tickPass_withSendableEnergy_sendsToTileCaches() {
    grid.setSendableEnergy(500);
    EnergyHandler target = mock(EnergyHandler.class);
    when(target.insert(anyInt(), any(TransactionContext.class))).thenReturn(200);
    unit.setTileCache(Direction.DOWN, target);
    unit.tickPass(0);
    verify(target).insert(eq(500), any(TransactionContext.class));
    assertEquals(200, grid.getUsedEnergy());
  }

  @Test
  void tickPass_tileInsertReturnsZero_skipsTile() {
    grid.setSendableEnergy(500);
    EnergyHandler target = mock(EnergyHandler.class);
    when(target.insert(anyInt(), any(TransactionContext.class))).thenReturn(0);
    unit.setTileCache(Direction.UP, target);
    unit.tickPass(0);
    verify(target).insert(eq(500), any(TransactionContext.class));
  }

  @Test
  void tickPass_multipleTiles_sendsToAll() {
    grid.setSendableEnergy(1000);
    EnergyHandler target1 = mock(EnergyHandler.class);
    when(target1.insert(anyInt(), any(TransactionContext.class))).thenReturn(300);

    EnergyHandler target2 = mock(EnergyHandler.class);
    when(target2.insert(anyInt(), any(TransactionContext.class))).thenReturn(200);

    unit.setTileCache(Direction.NORTH, target1);
    unit.setTileCache(Direction.SOUTH, target2);
    unit.tickPass(0);
    verify(target1).insert(eq(1000), any(TransactionContext.class));
    verify(target2).insert(eq(700), any(TransactionContext.class));
  }

  @Test
  void cacheTile_nullLevel_returnsNull() {
    when(parent.getLevel()).thenReturn(null);
    EnergyDuctUnit realUnit = new EnergyDuctUnit(parent, TEST_TRANSFER_LIMIT, TEST_CAPACITY);
    assertNull(realUnit.cacheTile(Direction.UP));
  }

  @Test
  void saveAdditional_savesEnergyShare() {
    unit.setEnergyForGrid(750);
    CompoundTag tag = new CompoundTag();
    unit.saveAdditional(tag, provider);
    assertEquals(750, tag.getInt("EnergyShare").orElse(0));
  }

  @Test
  void loadAdditional_loadsEnergyShare() {
    CompoundTag tag = new CompoundTag();
    tag.putInt("EnergyShare", 1234);
    unit.loadAdditional(tag, provider);
    assertEquals(1234, unit.getEnergyForGrid());
  }

  @Test
  void loadAdditional_defaultIsZero() {
    CompoundTag tag = new CompoundTag();
    unit.loadAdditional(tag, provider);
    assertEquals(0, unit.getEnergyForGrid());
  }
}
