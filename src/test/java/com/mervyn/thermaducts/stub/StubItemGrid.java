package com.mervyn.thermaducts.stub;

import com.mervyn.thermaducts.duct.item.ItemDuctUnit;
import com.mervyn.thermaducts.duct.item.ItemGrid;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

public class StubItemGrid extends ItemGrid {

  private boolean insertResult = true;
  private ItemStack lastInserted;
  private ItemDuctUnit lastOrigin;
  private Direction lastEntrySide;
  private int lastSpeed;
  private int insertCallCount;

  public StubItemGrid(ServerLevel level) {
    super(level);
  }

  public void setInsertResult(boolean result) {
    this.insertResult = result;
  }

  @Override
  public boolean insertNewItem(
      ItemStack stack, ItemDuctUnit origin, Direction entrySide, int speed) {
    insertCallCount++;
    lastInserted = stack;
    lastOrigin = origin;
    lastEntrySide = entrySide;
    lastSpeed = speed;
    return insertResult;
  }

  @Override
  public void syncTravelersAt(ItemDuctUnit itemUnit) {}

  public ItemStack getLastInserted() {
    return lastInserted;
  }

  public ItemDuctUnit getLastOrigin() {
    return lastOrigin;
  }

  public Direction getLastEntrySide() {
    return lastEntrySide;
  }

  public int getLastSpeed() {
    return lastSpeed;
  }

  public int getInsertCallCount() {
    return insertCallCount;
  }

  public void reset() {
    insertCallCount = 0;
    lastInserted = null;
    lastOrigin = null;
    lastEntrySide = null;
    lastSpeed = 0;
  }
}
