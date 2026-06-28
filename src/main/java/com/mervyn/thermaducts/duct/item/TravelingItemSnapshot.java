package com.mervyn.thermaducts.duct.item;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;

public class TravelingItemSnapshot {

  private final ItemStack stack;
  private final Direction direction;
  private final Direction oldDirection;
  private int progress;
  private final int speed;

  public TravelingItemSnapshot(
      ItemStack stack, Direction direction, Direction oldDirection, int progress, int speed) {
    this.stack = stack.copy();
    this.direction = direction;
    this.oldDirection = oldDirection;
    this.progress = Math.max(0, progress);
    this.speed = Math.max(1, speed);
  }

  public static TravelingItemSnapshot fromTravelingItem(TravelingItem item) {
    return new TravelingItemSnapshot(
        item.stack,
        item.getCurrentDirection(),
        item.getOldDirection(),
        item.ticksInDuct,
        item.speed);
  }

  public static TravelingItemSnapshot load(CompoundTag tag, HolderLookup.Provider provider) {
    CompoundTag stackTag = tag.getCompound("Stack").orElse(null);
    if (stackTag == null) {
      return null;
    }
    ItemStack stack =
        ItemStack.OPTIONAL_CODEC
            .parse(RegistryOps.create(NbtOps.INSTANCE, provider), stackTag)
            .result()
            .orElse(ItemStack.EMPTY);
    if (stack.isEmpty()) {
      return null;
    }

    Direction direction =
        Direction.from3DDataValue(tag.getByte("Direction").orElse((byte) 0).intValue());
    Direction oldDirection =
        Direction.from3DDataValue(tag.getByte("OldDirection").orElse((byte) 0).intValue());
    int progress = tag.getInt("Progress").orElse(0);
    int speed = tag.getInt("Speed").orElse(1);
    return new TravelingItemSnapshot(stack, direction, oldDirection, progress, speed);
  }

  public CompoundTag save(HolderLookup.Provider provider) {
    CompoundTag tag = new CompoundTag();
    tag.put(
        "Stack",
        ItemStack.OPTIONAL_CODEC
            .encodeStart(RegistryOps.create(NbtOps.INSTANCE, provider), stack)
            .result()
            .orElse(new CompoundTag()));
    tag.putByte("Direction", (byte) direction.get3DDataValue());
    tag.putByte("OldDirection", (byte) oldDirection.get3DDataValue());
    tag.putInt("Progress", progress);
    tag.putInt("Speed", speed);
    return tag;
  }

  public ItemStack getStack() {
    return stack;
  }

  public Direction getDirection() {
    return direction;
  }

  public Direction getOldDirection() {
    return oldDirection;
  }

  public int getRawProgress() {
    return progress;
  }

  public int getSpeed() {
    return speed;
  }

  public float getProgress(float partialTick) {
    return Math.min(1.0F, (progress + partialTick) / (float) speed);
  }

  public boolean tickClient() {
    progress++;
    return progress > speed;
  }
}
