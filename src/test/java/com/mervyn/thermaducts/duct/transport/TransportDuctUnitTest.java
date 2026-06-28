package com.mervyn.dynamiducts.duct.transport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.mervyn.dynamiducts.DuctUnitTestBase;
import com.mervyn.dynamiducts.blockentity.DuctBlockEntity;
import com.mervyn.dynamiducts.core.duct.DuctToken;
import com.mervyn.dynamiducts.core.network.ConnectionType;
import com.mervyn.dynamiducts.stub.StubTransportGrid;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransportDuctUnitTest extends DuctUnitTestBase {

  private static class TestableTransportDuctUnit extends TransportDuctUnit {
    TestableTransportDuctUnit(DuctBlockEntity parent, boolean longRange) {
      super(parent, longRange);
    }

    void setCachedDuct(int side, TransportDuctUnit duct) {
      ductCache[side] = duct;
    }
  }

  private TestableTransportDuctUnit unit;
  private TestableTransportDuctUnit longRangeUnit;

  @BeforeEach
  void setUp() {
    unit = new TestableTransportDuctUnit(parent, false);
    longRangeUnit = new TestableTransportDuctUnit(parent, true);
  }

  @Test
  void constructor_createsRegularUnit() {
    assertNotNull(unit);
    assertFalse(unit.isLongRange());
  }

  @Test
  void constructor_createsLongRangeUnit() {
    assertNotNull(longRangeUnit);
    assertTrue(longRangeUnit.isLongRange());
  }

  @Test
  void getToken_returnsTransport() {
    assertEquals(DuctToken.TRANSPORT, unit.getToken());
  }

  @Test
  void getPathWeight_regular_returnsOne() {
    assertEquals(1, unit.getPathWeight());
  }

  @Test
  void getPathWeight_longRange_returnsZero() {
    assertEquals(0, longRangeUnit.getPathWeight());
  }

  @Test
  void isLongRange_regular_returnsFalse() {
    assertFalse(unit.isLongRange());
  }

  @Test
  void isLongRange_longRange_returnsTrue() {
    assertTrue(longRangeUnit.isLongRange());
  }

  @Test
  void isEndpoint_returnsFalseInitially() {
    assertFalse(unit.isEndpoint());
  }

  @Test
  void getEndpointName_returnsUnnamedWhenEmpty() {
    assertEquals("Unnamed", unit.getEndpointName());
  }

  @Test
  void getRawEndpointName_returnsEmptyStringInitially() {
    assertTrue(unit.getRawEndpointName().isEmpty());
  }

  @Test
  void setEndpointName_updatesName() {
    unit.setEndpointName("Home");
    assertEquals("Home", unit.getEndpointName());
    assertEquals("Home", unit.getRawEndpointName());
  }

  @Test
  void setEndpointName_null_setsEmpty() {
    unit.setEndpointName("Test");
    unit.setEndpointName(null);
    assertTrue(unit.getRawEndpointName().isEmpty());
  }

  @Test
  void setEndpointName_marksDirty() {
    unit.setEndpointName("Home");
    verify(parent, atLeastOnce()).setChanged();
  }

  @Test
  void getEndpointIcon_returnsEmptyInitially() {
    assertTrue(unit.getEndpointIcon().isEmpty());
  }

  @Test
  void setEndpointIcon_storesCopyWithCountOne() {
    ItemStack stack = new ItemStack(net.minecraft.world.item.Items.DIAMOND, 5);
    unit.setEndpointIcon(stack);
    assertEquals(1, unit.getEndpointIcon().getCount());
    assertFalse(unit.getEndpointIcon().isEmpty());
  }

  @Test
  void setEndpointIcon_empty_setsEmpty() {
    unit.setEndpointIcon(ItemStack.EMPTY);
    assertTrue(unit.getEndpointIcon().isEmpty());
  }

  @Test
  void trySetEndpoint_withNoNeighbor_setsForced() {
    boolean result = unit.trySetEndpoint(Direction.UP);
    assertTrue(result);
    assertTrue(unit.isEndpoint());
    assertEquals(Direction.UP.ordinal(), unit.getEndpointSide());
    verify(parent).setConnectionType(Direction.UP, ConnectionType.FORCED);
  }

  @Test
  void trySetEndpoint_toggle_clearsEndpoint() {
    unit.trySetEndpoint(Direction.UP);
    unit.trySetEndpoint(Direction.UP);
    assertFalse(unit.isEndpoint());
    assertEquals(-1, unit.getEndpointSide());
    verify(parent, times(2)).setConnectionType(eq(Direction.UP), any());
  }

  @Test
  void trySetEndpoint_withDuctNeighbor_returnsFalse() {
    TestableTransportDuctUnit neighbor = new TestableTransportDuctUnit(parent, false);
    unit.setCachedDuct(Direction.UP.ordinal(), neighbor);
    boolean result = unit.trySetEndpoint(Direction.UP);
    assertFalse(result);
    assertFalse(unit.isEndpoint());
  }

  @Test
  void isEndpoint_afterSetting_returnsTrue() {
    unit.trySetEndpoint(Direction.UP);
    assertTrue(unit.isEndpoint());
  }

  @Test
  void isNode_whenEndpoint_returnsTrue() {
    unit.trySetEndpoint(Direction.UP);
    assertTrue(unit.isNode());
  }

  @Test
  void createGrid_returnsTransportGrid() {
    TransportGrid grid = unit.createGrid(level);
    assertNotNull(grid);
    assertInstanceOf(TransportGrid.class, grid);
  }

  @Test
  void getAvailableDestinations_noGrid_returnsEmpty() {
    assertTrue(unit.getAvailableDestinations().isEmpty());
  }

  @Test
  void getAvailableDestinations_withGrid_delegates() {
    StubTransportGrid grid = new StubTransportGrid(level);
    TransportRoute route =
        new TransportRoute(
            new BlockPos(10, 0, 10), (byte) 0, List.of(), 1);
    grid.setRoutes(List.of(route));
    unit.setGrid(grid);
    List<TransportRoute> routes = unit.getAvailableDestinations();
    assertEquals(1, routes.size());
    assertSame(route, routes.getFirst());
  }

  @Test
  void getRouteTo_findsMatchingDestination() {
    StubTransportGrid grid = new StubTransportGrid(level);
    BlockPos dest = new BlockPos(10, 0, 10);
    TransportRoute route =
        new TransportRoute(dest, (byte) 1, List.of(Direction.EAST), 1);
    grid.setRoutes(List.of(route));
    unit.setGrid(grid);
    TransportRoute found = unit.getRouteTo(dest);
    assertNotNull(found);
    assertSame(route, found);
  }

  @Test
  void getRouteTo_noMatch_returnsNull() {
    StubTransportGrid grid = new StubTransportGrid(level);
    TransportRoute route =
        new TransportRoute(new BlockPos(10, 0, 10), (byte) 1, List.of(), 1);
    grid.setRoutes(List.of(route));
    unit.setGrid(grid);
    assertNull(unit.getRouteTo(new BlockPos(99, 0, 99)));
  }

  @Test
  void sendPlayerToDest_noRoute_returnsFalse() {
    Player player = mock(Player.class);
    assertFalse(unit.sendPlayerToDest(player, new BlockPos(10, 0, 10)));
  }

  @Test
  void sendPlayerToDest_clientLevel_returnsFalse() {
    StubTransportGrid grid = new StubTransportGrid(level);
    TransportRoute route =
        new TransportRoute(new BlockPos(10, 0, 10), (byte) 1, List.of(), 1);
    grid.setRoutes(List.of(route));
    unit.setGrid(grid);
    when(parent.getLevel()).thenReturn(mock(net.minecraft.world.level.Level.class));
    Player player = mock(Player.class);
    assertFalse(unit.sendPlayerToDest(player, new BlockPos(10, 0, 10)));
  }

  @Test
  void saveAdditional_savesEndpointSide() {
    unit.trySetEndpoint(Direction.UP);
    CompoundTag tag = new CompoundTag();
    unit.saveAdditional(tag, provider);
    assertEquals((byte) Direction.UP.ordinal(), tag.getByte("EndpointSide").orElse((byte) -1));
  }

  @Test
  void loadAdditional_loadsEndpointSide() {
    CompoundTag tag = new CompoundTag();
    tag.putByte("EndpointSide", (byte) Direction.NORTH.ordinal());
    unit.loadAdditional(tag, provider);
    assertTrue(unit.isEndpoint());
    assertEquals(Direction.NORTH.ordinal(), unit.getEndpointSide());
  }

  @Test
  void saveAdditional_savesEndpointName() {
    unit.setEndpointName("TestName");
    CompoundTag tag = new CompoundTag();
    unit.saveAdditional(tag, provider);
    assertEquals("TestName", tag.getString("EndpointName").orElse(""));
  }

  @Test
  void loadAdditional_loadsEndpointName() {
    CompoundTag tag = new CompoundTag();
    tag.putString("EndpointName", "LoadedName");
    unit.loadAdditional(tag, provider);
    assertEquals("LoadedName", unit.getRawEndpointName());
  }

  @Test
  void saveAdditional_emptyEndpointName_notSaved() {
    CompoundTag tag = new CompoundTag();
    unit.saveAdditional(tag, provider);
    assertFalse(tag.contains("EndpointName"));
  }

  @Test
  void loadAdditional_defaultEndpointSideIsMinusOne() {
    CompoundTag tag = new CompoundTag();
    unit.loadAdditional(tag, provider);
    assertEquals(-1, unit.getEndpointSide());
  }

  @Test
  void canConnectToTile_withBlocked_returnsFalse() {
    when(parent.getConnectionType(Direction.UP))
        .thenReturn(ConnectionType.BLOCKED);
    assertFalse(unit.canConnectToTile(Direction.UP));
  }

  @Test
  void canConnectToTile_withEnergy_returnsFalse() {
    when(parent.getConnectionType(Direction.UP))
        .thenReturn(ConnectionType.ENERGY);
    assertFalse(unit.canConnectToTile(Direction.UP));
  }
}
