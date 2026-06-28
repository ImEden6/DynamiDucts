package com.mervyn.thermaducts.duct.item;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mervyn.thermaducts.DuctUnitTestBase;
import com.mervyn.thermaducts.core.duct.DuctToken;
import com.mervyn.thermaducts.core.network.ConnectionType;
import com.mervyn.thermaducts.core.network.Route;
import com.mervyn.thermaducts.stub.StubItemGrid;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ItemDuctUnitTest extends DuctUnitTestBase {

  private static final int TEST_SPEED = 5;
  private static final boolean TEST_TRANSPARENT = true;
  private static final int TEST_PATH_WEIGHT = 3;

  private ItemDuctUnit unit;
  private StubItemGrid grid;

  @BeforeEach
  void setUp() {
    unit = new ItemDuctUnit(parent, TEST_SPEED, TEST_TRANSPARENT, TEST_PATH_WEIGHT);
    grid = new StubItemGrid(level);
    unit.setGrid(grid);
  }

  @Test
  void constructor_twoArg_setsPathWeightToOne() {
    ItemDuctUnit u = new ItemDuctUnit(parent, TEST_SPEED, TEST_TRANSPARENT);
    assertEquals(1, u.getPathWeight());
  }

  @Test
  void constructor_fourArg_setsPathWeight() {
    assertEquals(TEST_PATH_WEIGHT, unit.getPathWeight());
  }

  @Test
  void getToken_returnsItem() {
    assertEquals(DuctToken.ITEM, unit.getToken());
  }

  @Test
  void getSpeed_returnsConstructorValue() {
    assertEquals(TEST_SPEED, unit.getSpeed());
  }

  @Test
  void isTransparent_returnsConstructorValue() {
    assertEquals(TEST_TRANSPARENT, unit.isTransparent());
  }

  @Test
  void getPathWeight_returnsConstructorValue() {
    assertEquals(TEST_PATH_WEIGHT, unit.getPathWeight());
  }

  @Test
  void createGrid_returnsItemGrid() {
    ItemGrid g = unit.createGrid(level);
    assertNotNull(g);
    assertInstanceOf(ItemGrid.class, g);
  }

  @Test
  void insertItem_noGrid_returnsFalse() {
    unit.setGrid(null);
    assertFalse(unit.insertItem(new ItemStack(Items.DIAMOND), Direction.UP));
  }

  @Test
  void insertItem_emptyStack_returnsFalse() {
    assertFalse(unit.insertItem(ItemStack.EMPTY, Direction.UP));
  }

  @Test
  void insertItem_withGrid_delegatesToGrid() {
    grid.setInsertResult(true);
    ItemStack stack = new ItemStack(Items.DIAMOND, 5);
    boolean result = unit.insertItem(stack.copy(), Direction.UP);
    assertTrue(result);
    assertSame(grid.getLastOrigin(), unit);
    assertEquals(Direction.UP, grid.getLastEntrySide());
    assertEquals(TEST_SPEED, grid.getLastSpeed());
    assertTrue(ItemStack.isSameItemSameComponents(stack, grid.getLastInserted()));
  }

  @Test
  void insertItem_withGridWhenGridReturnsFalse() {
    grid.setInsertResult(false);
    assertFalse(unit.insertItem(new ItemStack(Items.DIAMOND), Direction.UP));
  }

  @Test
  void insertItemWithRoute_createsTravelingItem() {
    Route route =
        new Route.Builder(new BlockPos(10, 0, 10), Direction.DOWN)
            .addStep(Direction.EAST)
            .addStep(Direction.UP)
            .weight(1)
            .build();
    unit.insertItemWithRoute(new ItemStack(Items.IRON_INGOT), Direction.UP, route, 2);
    List<TravelingItem> toAdd = unit.getItemsToAdd();
    assertEquals(1, toAdd.size());
    TravelingItem item = toAdd.getFirst();
    assertSame(route, item.route);
    assertEquals(Direction.UP, item.entrySide);
  }

  @Test
  void addTravelingItem_addsToItemsToAdd() {
    Route route =
        new Route.Builder(new BlockPos(5, 0, 5), Direction.DOWN)
            .addStep(Direction.EAST)
            .build();
    TravelingItem item =
        new TravelingItem(new ItemStack(Items.GOLD_INGOT), route, BlockPos.ZERO, Direction.UP, 3);
    unit.addTravelingItem(item);
    assertEquals(1, unit.getItemsToAdd().size());
    assertSame(item, unit.getItemsToAdd().getFirst());
  }

  @Test
  void transferItem_updatesPositionAndAddsToQueue() {
    Route route =
        new Route.Builder(new BlockPos(5, 0, 5), Direction.DOWN)
            .addStep(Direction.EAST)
            .build();
    TravelingItem item =
        new TravelingItem(new ItemStack(Items.IRON_INGOT), route, new BlockPos(3, 0, 3), Direction.UP, 3);
    unit.transferItem(item);
    assertEquals(BlockPos.ZERO, item.currentPos);
    assertEquals(1, unit.getItemsToAdd().size());
  }

  @Test
  void flushItemsToAdd_movesItemsToMyItems() {
    Route route =
        new Route.Builder(new BlockPos(5, 0, 5), Direction.DOWN)
            .addStep(Direction.EAST)
            .build();
    unit.addTravelingItem(
        new TravelingItem(new ItemStack(Items.DIAMOND), route, BlockPos.ZERO, Direction.UP, 3));
    unit.addTravelingItem(
        new TravelingItem(new ItemStack(Items.EMERALD), route, BlockPos.ZERO, Direction.UP, 3));
    assertTrue(unit.getMyItems().isEmpty());
    unit.flushItemsToAdd();
    assertEquals(2, unit.getMyItems().size());
    assertTrue(unit.getItemsToAdd().isEmpty());
  }

  @Test
  void flushItemsToAdd_empty_doesNothing() {
    unit.flushItemsToAdd();
    assertTrue(unit.getMyItems().isEmpty());
  }

  @Test
  void getMyItems_returnsModifiableList() {
    assertNotNull(unit.getMyItems());
    assertTrue(unit.getMyItems().isEmpty());
  }

  @Test
  void dropAllItems_clientSide_doesNothing() {
    when(parent.getLevel()).thenReturn(mock(net.minecraft.world.level.Level.class));
    when(parent.getLevel().isClientSide()).thenReturn(true);
    Route route =
        new Route.Builder(new BlockPos(5, 0, 5), Direction.DOWN)
            .addStep(Direction.EAST)
            .build();
    unit.addTravelingItem(
        new TravelingItem(new ItemStack(Items.DIAMOND), route, BlockPos.ZERO, Direction.UP, 3));
    unit.flushItemsToAdd();
    unit.dropAllItems();
    verify(level, never()).addFreshEntity(any());
  }

  @Test
  void dropAllItems_serverSide_spawnsEntities() {
    Route route =
        new Route.Builder(new BlockPos(5, 0, 5), Direction.DOWN)
            .addStep(Direction.EAST)
            .build();
    unit.addTravelingItem(
        new TravelingItem(new ItemStack(Items.DIAMOND), route, BlockPos.ZERO, Direction.UP, 3));
    unit.flushItemsToAdd();
    unit.dropAllItems();
    verify(level, atLeastOnce()).addFreshEntity(any());
    assertTrue(unit.getMyItems().isEmpty());
  }

  @Test
  void createCapability_size_returnsOne() {
    ResourceHandler<ItemResource> cap = unit.createCapability(Direction.UP);
    assertEquals(1, cap.size());
  }

  @Test
  void createCapability_getResource_returnsEmpty() {
    ResourceHandler<ItemResource> cap = unit.createCapability(Direction.UP);
    assertTrue(cap.getResource(0).isEmpty());
  }

  @Test
  void createCapability_insert_returnsZero() {
    ResourceHandler<ItemResource> cap = unit.createCapability(Direction.UP);
    try (var tx = Transaction.openRoot()) {
      assertEquals(0, cap.insert(0, ItemResource.EMPTY, 5, tx));
    }
  }

  @Test
  void createCapability_extract_returnsZero() {
    ResourceHandler<ItemResource> cap = unit.createCapability(Direction.UP);
    try (var tx = Transaction.openRoot()) {
      assertEquals(0, cap.extract(0, ItemResource.EMPTY, 64, tx));
    }
  }

  @Test
  void createCapability_getCapacityAsLong_returns64() {
    ResourceHandler<ItemResource> cap = unit.createCapability(Direction.UP);
    assertEquals(64, cap.getCapacityAsLong(0, ItemResource.EMPTY));
  }

  @Test
  void createCapability_isValid_returnsTrue() {
    ResourceHandler<ItemResource> cap = unit.createCapability(Direction.UP);
    assertTrue(cap.isValid(0, ItemResource.of(new ItemStack(Items.STONE))));
  }

  @Test
  void cacheTile_nullLevel_returnsNull() {
    when(parent.getLevel()).thenReturn(null);
    assertNull(unit.cacheTile(Direction.UP));
  }

  @Test
  void tickClientTravelingItems_clientSide_ticksItems() {
    when(parent.getLevel()).thenReturn(mock(net.minecraft.world.level.Level.class));
    when(parent.getLevel().isClientSide()).thenReturn(true);
    Route route =
        new Route.Builder(new BlockPos(5, 0, 5), Direction.DOWN)
            .addStep(Direction.EAST)
            .build();
    TravelingItem item =
        new TravelingItem(new ItemStack(Items.DIAMOND), route, BlockPos.ZERO, Direction.UP, 2);
    TravelingItemSnapshot snapshot = TravelingItemSnapshot.fromTravelingItem(item);
    unit.setClientTravelingItems(List.of(snapshot));
    unit.tickClientTravelingItems();
    assertTrue(unit.getClientTravelingItems().isEmpty());
  }

  @Test
  void tickClientTravelingItems_serverSide_doesNothing() {
    Route route =
        new Route.Builder(new BlockPos(5, 0, 5), Direction.DOWN)
            .addStep(Direction.EAST)
            .build();
    TravelingItem item =
        new TravelingItem(new ItemStack(Items.DIAMOND), route, BlockPos.ZERO, Direction.UP, 2);
    TravelingItemSnapshot snapshot = TravelingItemSnapshot.fromTravelingItem(item);
    unit.setServerTravelingItems(List.of(snapshot));
    unit.tickClientTravelingItems();
    assertEquals(1, unit.getClientTravelingItems().size());
  }

  @Test
  void tickClientTravelingItems_emptyList_doesNothing() {
    when(parent.getLevel()).thenReturn(mock(net.minecraft.world.level.Level.class));
    when(parent.getLevel().isClientSide()).thenReturn(true);
    unit.tickClientTravelingItems();
    assertTrue(unit.getClientTravelingItems().isEmpty());
  }

  @Test
  void setServerTravelingItems_updatesClientListOnServer() {
    Route route =
        new Route.Builder(new BlockPos(5, 0, 5), Direction.DOWN)
            .addStep(Direction.EAST)
            .build();
    TravelingItem item =
        new TravelingItem(new ItemStack(Items.DIAMOND), route, BlockPos.ZERO, Direction.UP, 2);
    TravelingItemSnapshot snapshot = TravelingItemSnapshot.fromTravelingItem(item);
    unit.setServerTravelingItems(List.of(snapshot));
    assertFalse(unit.getClientTravelingItems().isEmpty());
  }

  @Test
  void saveAdditional_noItems_notSaved() {
    CompoundTag tag = new CompoundTag();
    unit.saveAdditional(tag, provider);
    assertFalse(tag.contains("TravelingItems"));
  }

  @Test
  void saveAdditional_withItems_saves() {
    Route route =
        new Route.Builder(new BlockPos(5, 0, 5), Direction.DOWN)
            .addStep(Direction.EAST)
            .build();
    unit.addTravelingItem(
        new TravelingItem(new ItemStack(Items.DIAMOND), route, BlockPos.ZERO, Direction.UP, 3));
    unit.flushItemsToAdd();
    CompoundTag tag = new CompoundTag();
    unit.saveAdditional(tag, provider);
    assertTrue(tag.contains("TravelingItems"));
  }

  @Test
  void loadAdditional_noItems_clearsList() {
    unit.getClientTravelingItems()
        .add(
            new TravelingItemSnapshot(
                new ItemStack(Items.IRON_INGOT),
                Direction.UP,
                Direction.DOWN,
                0,
                1));
    CompoundTag tag = new CompoundTag();
    unit.loadAdditional(tag, provider);
    assertTrue(unit.getClientTravelingItems().isEmpty());
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
