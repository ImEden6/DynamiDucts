package com.mervyn.dynamiducts.core.attachment;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;

@SuppressWarnings("removal")
public class FilterLogic {

  public static final int ROUTE_TYPE_NEAREST = 0;
  public static final int ROUTE_TYPE_FURTHEST = 1;
  public static final int ROUTE_TYPE_RANDOM = 2;
  public static final int ROUTE_TYPE_ROUND_ROBIN = 3;
  public static final int ROUTE_TYPE_COUNT = 4;

  private final int slotCount;
  private final ItemStack[] filterStacks;
  private boolean whitelist = false;
  private boolean matchComponents = false;
  private boolean matchModId = false;
  private int routeType = ROUTE_TYPE_NEAREST;
  private int maxStock = -1;

  private Set<String> cachedModIds;
  private Set<String> cachedFluidModIds;
  private boolean needsRecalc = true;

  public FilterLogic(int slotCount) {
    this.slotCount = Math.max(1, slotCount);
    this.filterStacks = new ItemStack[this.slotCount];
    for (int i = 0; i < filterStacks.length; i++) {
      filterStacks[i] = ItemStack.EMPTY;
    }
  }

  public boolean matchesItem(ItemStack stack) {
    if (stack.isEmpty()) return false;

    if (matchModId) {
      recalcIfNeeded();
      if (!cachedModIds.isEmpty()) {
        String mod = BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace();
        if (cachedModIds.contains(mod)) return whitelist;
      }
    }

    boolean found = false;
    for (ItemStack filter : filterStacks) {
      if (filter.isEmpty()) continue;
      if (matchesFilterEntry(stack, filter)) {
        found = true;
        break;
      }
    }
    return whitelist == found;
  }

  private boolean matchesFilterEntry(ItemStack stack, ItemStack filter) {
    if (matchComponents) {
      return ItemStack.isSameItemSameComponents(stack, filter);
    }
    return ItemStack.isSameItem(stack, filter);
  }

  private void recalcIfNeeded() {
    if (!needsRecalc) return;
    needsRecalc = false;
    cachedModIds = new HashSet<>();
    cachedFluidModIds = new HashSet<>();
    for (ItemStack filter : filterStacks) {
      if (!filter.isEmpty()) {
        cachedModIds.add(BuiltInRegistries.ITEM.getKey(filter.getItem()).getNamespace());
        FluidStack fluid = getFilterFluid(filter);
        if (!fluid.isEmpty()) {
          cachedFluidModIds.add(BuiltInRegistries.FLUID.getKey(fluid.getFluid()).getNamespace());
        }
      }
    }
  }

  public void markDirty() {
    needsRecalc = true;
  }

  public boolean matchesFluid(FluidStack stack) {
    if (stack.isEmpty()) return false;

    if (matchModId) {
      recalcIfNeeded();
      if (!cachedFluidModIds.isEmpty()) {
        String mod = BuiltInRegistries.FLUID.getKey(stack.getFluid()).getNamespace();
        if (cachedFluidModIds.contains(mod)) return whitelist;
      }
    }

    boolean found = false;
    for (ItemStack filter : filterStacks) {
      if (filter.isEmpty()) continue;
      FluidStack filterFluid = getFilterFluid(filter);
      if (!filterFluid.isEmpty() && FluidStack.isSameFluidSameComponents(stack, filterFluid)) {
        found = true;
        break;
      }
    }
    return whitelist == found;
  }

  private static FluidStack getFilterFluid(ItemStack filter) {
    return FluidUtil.getFluidContained(filter).orElse(FluidStack.EMPTY);
  }

  private boolean hasNoFilters() {
    for (ItemStack filter : filterStacks) {
      if (!filter.isEmpty()) return false;
    }
    return true;
  }

  public boolean isEmpty() {
    return hasNoFilters();
  }

  public ItemStack getFilterStack(int slot) {
    if (slot < 0 || slot >= filterStacks.length) return ItemStack.EMPTY;
    return filterStacks[slot];
  }

  public void setFilterStack(int slot, ItemStack stack) {
    if (slot >= 0 && slot < filterStacks.length) {
      filterStacks[slot] = stack.copy();
      markDirty();
    }
  }

  public boolean isWhitelist() {
    return whitelist;
  }

  public void setWhitelist(boolean whitelist) {
    this.whitelist = whitelist;
  }

  public boolean isMatchComponents() {
    return matchComponents;
  }

  public void setMatchComponents(boolean matchComponents) {
    this.matchComponents = matchComponents;
  }

  public boolean isMatchModId() {
    return matchModId;
  }

  public void setMatchModId(boolean matchModId) {
    this.matchModId = matchModId;
  }

  public int getMaxStock() {
    return maxStock;
  }

  public void setMaxStock(int maxStock) {
    this.maxStock = maxStock;
  }

  public int getMaxStockOrDefault(int fallback) {
    return maxStock > 0 ? maxStock : fallback;
  }

  public int getRouteType() {
    return routeType;
  }

  public void setRouteType(int routeType) {
    this.routeType = Math.floorMod(routeType, ROUTE_TYPE_COUNT);
  }

  public int getSlotCount() {
    return slotCount;
  }

  public CompoundTag save(HolderLookup.Provider provider) {
    CompoundTag tag = new CompoundTag();
    tag.putBoolean("Whitelist", whitelist);
    tag.putBoolean("MatchComponents", matchComponents);
    tag.putBoolean("MatchModId", matchModId);
    tag.putInt("RouteType", routeType);
    tag.putInt("MaxStock", maxStock);

    ListTag items = new ListTag();
    for (ItemStack stack : filterStacks) {
      if (stack.isEmpty()) {
        items.add(new CompoundTag());
      } else {
        items.add(
            ItemStack.OPTIONAL_CODEC
                .encodeStart(RegistryOps.create(NbtOps.INSTANCE, provider), stack)
                .result()
                .orElse(new CompoundTag()));
      }
    }
    tag.put("Filters", items);
    return tag;
  }

  public void load(CompoundTag tag, HolderLookup.Provider provider) {
    whitelist = tag.getBoolean("Whitelist").orElse(false);
    matchComponents = tag.getBoolean("MatchComponents").orElse(false);
    matchModId = tag.getBoolean("MatchModId").orElse(false);
    routeType =
        tag.contains("RouteType")
            ? Math.floorMod(tag.getInt("RouteType").orElse(0), ROUTE_TYPE_COUNT)
            : ROUTE_TYPE_NEAREST;
    maxStock = tag.getInt("MaxStock").orElse(-1);

    tag.getList("Filters")
        .ifPresent(
            items -> {
              for (int i = 0; i < Math.min(items.size(), filterStacks.length); i++) {
                final int index = i;
                items
                    .getCompound(index)
                    .ifPresent(
                        itemTag -> {
                          if (itemTag.isEmpty()) {
                            filterStacks[index] = ItemStack.EMPTY;
                          } else {
                            filterStacks[index] =
                                ItemStack.OPTIONAL_CODEC
                                    .parse(RegistryOps.create(NbtOps.INSTANCE, provider), itemTag)
                                    .result()
                                    .orElse(ItemStack.EMPTY);
                          }
                        });
              }
            });
    markDirty();
  }
}
