package com.mervyn.dynamiducts.duct.fluid;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mervyn.dynamiducts.DuctUnitTestBase;
import com.mervyn.dynamiducts.core.duct.DuctToken;
import com.mervyn.dynamiducts.core.network.ConnectionType;
import com.mervyn.dynamiducts.stub.StubFluidGrid;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FluidDuctUnitTest extends DuctUnitTestBase {

  private static final int TEST_CAPACITY = 1000;
  private static final int TEST_THROUGHPUT = 100;
  private static final boolean TEST_TRANSPARENT = true;

  private FluidDuctUnit unit;
  private StubFluidGrid grid;

  private static FluidStack mockFluid(int amount) {
    FluidStack stack = mock(FluidStack.class);
    when(stack.getAmount()).thenReturn(amount);
    when(stack.isEmpty()).thenReturn(false);
    return stack;
  }

  private static FluidStack mockFluidWithCopy(int amount) {
    FluidStack stack = mock(FluidStack.class);
    FluidStack copy = mock(FluidStack.class);
    when(copy.getAmount()).thenReturn(amount);
    when(stack.getAmount()).thenReturn(amount);
    when(stack.isEmpty()).thenReturn(false);
    when(copy.isEmpty()).thenReturn(false);
    when(stack.copy()).thenReturn(copy);
    return stack;
  }

  @BeforeEach
  void setUp() {
    unit = new FluidDuctUnit(parent, TEST_CAPACITY, TEST_THROUGHPUT, TEST_TRANSPARENT);
    grid = new StubFluidGrid(level, TEST_CAPACITY, TEST_THROUGHPUT);
    unit.setGrid(grid);
  }

  @Test
  void constructor_createsUnit() {
    assertNotNull(unit);
    assertSame(parent, unit.getParent());
  }

  @Test
  void getToken_returnsFluid() {
    assertEquals(DuctToken.FLUID, unit.getToken());
  }

  @Test
  void getPathWeight_returnsOne() {
    assertEquals(1, unit.getPathWeight());
  }

  @Test
  void isTransparent_returnsConstructorValue() {
    assertTrue(unit.isTransparent());
  }

  @Test
  void isTransparent_opaque_returnsFalse() {
    FluidDuctUnit opaque = new FluidDuctUnit(parent, TEST_CAPACITY, TEST_THROUGHPUT, false);
    assertFalse(opaque.isTransparent());
  }

  @Test
  void createGrid_returnsFluidGridWithCorrectParams() {
    FluidGrid g = unit.createGrid(level);
    assertNotNull(g);
    assertInstanceOf(FluidGrid.class, g);
  }

  @Test
  void getFluidForGrid_returnsEmptyInitially() {
    assertTrue(unit.getFluidForGrid().isEmpty());
  }

  @Test
  void setFluidForGrid_setsFluid() {
    FluidStack fluid = mockFluid(500);
    unit.setFluidForGrid(fluid);
    assertEquals(500, unit.getFluidForGrid().getAmount());
  }

  @Test
  void setFluidForGrid_null_setsEmpty() {
    unit.setFluidForGrid(null);
    assertTrue(unit.getFluidForGrid().isEmpty());
  }

  @Test
  void getVisualFluid_returnsEmptyInitially() {
    assertTrue(unit.getVisualFluid().isEmpty());
  }

  @Test
  void setRenderFluid_storesCopy() {
    FluidStack fluid = mockFluidWithCopy(4);
    unit.setRenderFluid(fluid);
    assertEquals(4, unit.getVisualFluid().getAmount());
    assertNotSame(fluid, unit.getVisualFluid());
  }

  @Test
  void setRenderFluid_null_setsEmpty() {
    unit.setRenderFluid(null);
    assertTrue(unit.getVisualFluid().isEmpty());
  }

  @Test
  void setRenderFluid_empty_setsEmpty() {
    unit.setRenderFluid(FluidStack.EMPTY);
    assertTrue(unit.getVisualFluid().isEmpty());
  }

  @Test
  void getVisualFluidLevel_returnsZeroWhenEmpty() {
    assertEquals(0, unit.getVisualFluidLevel());
  }

  @Test
  void getVisualFluidLevel_clampsBetweenOneAndSix() {
    FluidStack fluid = mockFluid(1);
    unit.setRenderFluid(fluid);
    int level = unit.getVisualFluidLevel();
    assertTrue(level >= 1 && level <= 6);
  }

  @Test
  void createCapability_size_returnsGridTankSize() {
    ResourceHandler<FluidResource> cap = unit.createCapability(Direction.UP);
    assertEquals(grid.getTank().size(), cap.size());
  }

  @Test
  void createCapability_getResource_returnsGridFluid() {
    ResourceHandler<FluidResource> cap = unit.createCapability(Direction.UP);
    assertTrue(cap.getResource(0).isEmpty());
  }

  @Test
  void createCapability_getCapacityAsLong_returnsGridCapacity() {
    ResourceHandler<FluidResource> cap = unit.createCapability(Direction.UP);
    assertEquals(grid.getTank().getCapacityAsLong(0, FluidResource.EMPTY), cap.getCapacityAsLong(0, FluidResource.EMPTY));
  }

  @Test
  void createCapability_isValid_returnsTrue() {
    ResourceHandler<FluidResource> cap = unit.createCapability(Direction.UP);
    assertTrue(cap.isValid(0, FluidResource.EMPTY));
  }

  @Test
  void createCapability_insert_delegatesToTank() {
    ResourceHandler<FluidResource> cap = unit.createCapability(Direction.UP);
    FluidStack fluid = mockFluid(100);
    try (var tx = Transaction.openRoot()) {
      int filled = cap.insert(0, FluidResource.of(fluid), 100, tx);
      assertTrue(filled >= 0);
    }
  }

  @Test
  void createCapability_insert_noGrid_returnsZero() {
    unit.setGrid(null);
    ResourceHandler<FluidResource> cap = unit.createCapability(Direction.UP);
    FluidStack fluid = mockFluid(100);
    try (var tx = Transaction.openRoot()) {
      assertEquals(0, cap.insert(0, FluidResource.of(fluid), 100, tx));
    }
  }

  @Test
  void createCapability_extract_returnsZero() {
    ResourceHandler<FluidResource> cap = unit.createCapability(Direction.UP);
    try (var tx = Transaction.openRoot()) {
      assertEquals(0, cap.extract(0, FluidResource.EMPTY, 100, tx));
    }
  }

  @Test
  void markBeingDestroyed_setsFlag() {
    assertFalse(unit.isBeingDestroyed());
    unit.markBeingDestroyed();
    assertTrue(unit.isBeingDestroyed());
  }

  @Test
  void cacheTile_nullLevel_returnsNull() {
    when(parent.getLevel()).thenReturn(null);
    assertNull(unit.cacheTile(Direction.UP));
  }

  @Test
  void saveAdditional_noData_notSaved() {
    CompoundTag tag = new CompoundTag();
    unit.saveAdditional(tag, provider);
    assertFalse(tag.contains("Fluid"));
    assertFalse(tag.contains("RenderFluid"));
  }

  @Test
  void saveAdditional_withFluidForGrid_saves() {
    FluidStack fluid = mockFluid(250);
    unit.setFluidForGrid(fluid);
    CompoundTag tag = new CompoundTag();
    unit.saveAdditional(tag, provider);
    assertTrue(tag.contains("Fluid"));
  }

  @Test
  void loadAdditional_withFluid_loads() {
    FluidStack fluid = mockFluid(250);
    unit.setFluidForGrid(fluid);
    CompoundTag tag = new CompoundTag();
    unit.saveAdditional(tag, provider);
    unit.setFluidForGrid(FluidStack.EMPTY);
    unit.loadAdditional(tag, provider);
    assertFalse(unit.getFluidForGrid().isEmpty());
    assertEquals(250, unit.getFluidForGrid().getAmount());
  }

  @Test
  void saveAdditional_withRenderFluid_saves() {
    FluidStack fluid = mockFluid(4);
    unit.setRenderFluid(fluid);
    CompoundTag tag = new CompoundTag();
    unit.saveAdditional(tag, provider);
    assertTrue(tag.contains("RenderFluid"));
  }

  @Test
  void loadAdditional_withRenderFluid_loads() {
    FluidStack fluid = mockFluid(4);
    unit.setRenderFluid(fluid);
    CompoundTag tag = new CompoundTag();
    unit.saveAdditional(tag, provider);
    unit.setRenderFluid(FluidStack.EMPTY);
    unit.loadAdditional(tag, provider);
    assertFalse(unit.getVisualFluid().isEmpty());
    assertEquals(4, unit.getVisualFluid().getAmount());
  }

  @Test
  void canConnectToTile_withBlocked_returnsFalse() {
    when(parent.getConnectionType(Direction.UP)).thenReturn(ConnectionType.BLOCKED);
    assertFalse(unit.canConnectToTile(Direction.UP));
  }

  @Test
  void canConnectToTile_withEnergy_returnsFalse() {
    when(parent.getConnectionType(Direction.UP)).thenReturn(ConnectionType.ENERGY);
    assertFalse(unit.canConnectToTile(Direction.UP));
  }
}
