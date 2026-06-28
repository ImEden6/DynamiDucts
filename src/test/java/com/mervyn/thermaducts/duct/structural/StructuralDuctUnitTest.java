package com.mervyn.dynamiducts.duct.structural;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mervyn.dynamiducts.DuctUnitTestBase;
import com.mervyn.dynamiducts.core.duct.DuctToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StructuralDuctUnitTest extends DuctUnitTestBase {

  private StructuralDuctUnit unit;

  @BeforeEach
  void setUp() {
    unit = new StructuralDuctUnit(parent);
  }

  @Test
  void constructor_createsUnitWithoutError() {
    assertNotNull(unit);
    assertSame(parent, unit.getParent());
  }

  @Test
  void getToken_returnsStructural() {
    assertEquals(DuctToken.STRUCTURAL, unit.getToken());
  }

  @Test
  void getPathWeight_returnsOne() {
    assertEquals(1, unit.getPathWeight());
  }

  @Test
  void createGrid_returnsStructuralGrid() {
    StructuralGrid grid = unit.createGrid(level);
    assertNotNull(grid);
    assertInstanceOf(StructuralGrid.class, grid);
  }

  @Test
  void createTileCacheArray_returnsVoidArrayOfLengthSix() {
    Void[] cache = unit.createTileCacheArray();
    assertEquals(6, cache.length);
  }

  @Test
  void createDuctCacheArray_returnsStructuralArrayOfLengthSix() {
    StructuralDuctUnit[] cache = unit.createDuctCacheArray();
    assertEquals(6, cache.length);
  }

  @Test
  void isNode_returnsFalseInitially() {
    assertFalse(unit.isNode());
  }

  @Test
  void getGrid_returnsNullInitially() {
    assertNull(unit.getGrid());
  }

  @Test
  void setGrid_updatesGrid() {
    StructuralGrid grid = unit.createGrid(level);
    unit.setGrid(grid);
    assertSame(grid, unit.getGrid());
  }

  @Test
  void getPos_returnsParentPos() {
    assertEquals(pos, unit.getPos());
  }

  @Test
  void invalidate_clearsGrid() {
    StructuralGrid grid = unit.createGrid(level);
    unit.setGrid(grid);
    unit.invalidate();
    assertNull(unit.getGrid());
  }

  @Test
  void updateCaches_withNullLevel_doesNotCrash() {
    when(parent.getLevel()).thenReturn(null);
    unit.updateCaches();
    assertFalse(unit.isNode());
  }

  @Test
  void getDuctNeighbor_returnsNullInitially() {
    for (var dir : net.minecraft.core.Direction.values()) {
      assertNull(unit.getDuctNeighbor(dir));
    }
  }

  @Test
  void getTileCache_returnsNullInitially() {
    for (var dir : net.minecraft.core.Direction.values()) {
      assertNull(unit.getTileCache(dir));
    }
  }

  @Test
  void canConnectToTile_withBlocked_returnsFalse() {
    when(parent.getConnectionType(net.minecraft.core.Direction.UP))
        .thenReturn(com.mervyn.dynamiducts.core.network.ConnectionType.BLOCKED);
    assertFalse(unit.canConnectToTile(net.minecraft.core.Direction.UP));
  }

  @Test
  void canConnectToTile_withNormal_returnsTrue() {
    assertTrue(unit.canConnectToTile(net.minecraft.core.Direction.UP));
  }

  @Test
  void canConnectToTile_withForced_returnsTrue() {
    when(parent.getConnectionType(net.minecraft.core.Direction.UP))
        .thenReturn(com.mervyn.dynamiducts.core.network.ConnectionType.FORCED);
    assertTrue(unit.canConnectToTile(net.minecraft.core.Direction.UP));
  }

  @Test
  void canConnectToTile_withEnergy_returnsFalse() {
    when(parent.getConnectionType(net.minecraft.core.Direction.UP))
        .thenReturn(com.mervyn.dynamiducts.core.network.ConnectionType.ENERGY);
    assertFalse(unit.canConnectToTile(net.minecraft.core.Direction.UP));
  }
}
