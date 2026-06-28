package com.mervyn.thermaducts.attachment.retriever;

import com.mervyn.thermaducts.ThermaDucts;
import com.mervyn.thermaducts.blockentity.DuctBlockEntity;
import com.mervyn.thermaducts.core.attachment.AttachmentTier;
import com.mervyn.thermaducts.core.attachment.ConnectionBase;
import com.mervyn.thermaducts.core.duct.DuctToken;
import com.mervyn.thermaducts.core.network.Route;
import com.mervyn.thermaducts.duct.item.ItemDuctUnit;
import com.mervyn.thermaducts.duct.item.ItemGrid;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

@SuppressWarnings("removal")
public class RetrieverItem extends ConnectionBase {

  public static final Identifier ID =
      Identifier.fromNamespaceAndPath(ThermaDucts.MODID, "retriever_item");

  public RetrieverItem(DuctBlockEntity parent, Direction side, AttachmentTier tier) {
    super(parent, side, tier);
  }

  @Override
  public Identifier getId() {
    return ID;
  }

  @Override
  public boolean isRetriever() {
    return true;
  }

  @Override
  protected void performAction() {
    var level = parent.getLevel();
    if (level == null) return;

    var unit = parent.getDuctUnit(DuctToken.ITEM);
    if (!(unit instanceof ItemDuctUnit localUnit)) return;
    if (!(localUnit.getGrid() instanceof ItemGrid grid)) return;

    IItemHandler localInv =
        IItemHandler.of(
            level.getCapability(
                Capabilities.Item.BLOCK, parent.getBlockPos().relative(side), side.getOpposite()));
    if (localInv == null) return;

    int maxStack = tier.multiStack() ? tier.stackSize() : Math.min(tier.stackSize(), 64);
    int maxStock = filter.getMaxStock() <= 0 ? Integer.MAX_VALUE : filter.getMaxStock();
    List<Route> routes = grid.getSortedRoutes(localUnit, filter.getRouteType());

    for (Route route : routes) {
      if (route.destination.equals(parent.getBlockPos()) && route.insertionSide == side) continue;
      ItemDuctUnit remoteNode = findNode(route.destination, grid);
      if (remoteNode == null) continue;

      IItemHandler remoteInv =
          IItemHandler.of(
              level.getCapability(
                  Capabilities.Item.BLOCK,
                  remoteNode.getPos().relative(route.insertionSide),
                  route.insertionSide.getOpposite()));
      if (remoteInv == null) continue;

      for (int slot = 0; slot < remoteInv.getSlots(); slot++) {
        ItemStack peek = remoteInv.extractItem(slot, maxStack, true);
        if (peek.isEmpty()) continue;
        if (!filter.matchesItem(peek)) continue;
        int retainSpace = getRetainSpace(localInv, peek, maxStock);
        if (retainSpace <= 0) continue;
        int requestAmount = Math.min(maxStack, retainSpace);
        if (requestAmount <= 0) continue;

        peek = remoteInv.extractItem(slot, requestAmount, true);
        if (peek.isEmpty()) continue;
        if (!canInsertInto(localInv, peek, maxStock)) continue;

        ItemStack extracted = remoteInv.extractItem(slot, requestAmount, false);
        if (extracted.isEmpty()) continue;

        if (tier.multiStack() && extracted.getCount() < requestAmount) {
          for (int s = slot + 1;
              s < remoteInv.getSlots() && extracted.getCount() < requestAmount;
              s++) {
            ItemStack other = remoteInv.extractItem(s, requestAmount - extracted.getCount(), true);
            if (other.isEmpty() || !ItemStack.isSameItemSameComponents(extracted, other)) continue;
            ItemStack extra = remoteInv.extractItem(s, requestAmount - extracted.getCount(), false);
            if (!extra.isEmpty()) {
              extracted.grow(extra.getCount());
            }
          }
        }

        Route returnRoute =
            grid.getRouteCache().getRouteBetween(remoteNode, localUnit, side, grid.getNodeSet());
        if (returnRoute == null) {
          remoteInv.insertItem(slot, extracted, false);
          continue;
        }

        remoteNode.insertItemWithRoute(
            extracted, route.insertionSide, returnRoute, tier.speedBoost());
        return;
      }
    }
  }

  private boolean canInsertInto(IItemHandler handler, ItemStack stack, int maxStock) {
    int retainSpace = getRetainSpace(handler, stack, maxStock);
    if (retainSpace <= 0) {
      return false;
    }

    ItemStack simulated = stack.copyWithCount(Math.min(stack.getCount(), retainSpace));
    for (int i = 0; i < handler.getSlots() && !simulated.isEmpty(); i++) {
      simulated = handler.insertItem(i, simulated, true);
    }
    return simulated.getCount() < Math.min(stack.getCount(), retainSpace);
  }

  private int getRetainSpace(IItemHandler handler, ItemStack stack, int maxStock) {
    if (maxStock == Integer.MAX_VALUE) {
      return Integer.MAX_VALUE;
    }

    int stored = 0;
    for (int i = 0; i < handler.getSlots(); i++) {
      ItemStack existing = handler.getStackInSlot(i);
      if (!existing.isEmpty() && ItemStack.isSameItemSameComponents(existing, stack)) {
        stored += existing.getCount();
        if (stored >= maxStock) {
          return 0;
        }
      }
    }
    stored += countInTransit(stack);
    return Math.max(0, maxStock - stored);
  }

  private int countInTransit(ItemStack stack) {
    var unit = parent.getDuctUnit(DuctToken.ITEM);
    if (!(unit instanceof ItemDuctUnit localUnit)) return 0;
    if (!(localUnit.getGrid() instanceof ItemGrid grid)) return 0;

    int count = 0;
    for (ItemDuctUnit ductUnit : grid.getNodeSnapshot()) {
      for (var item : ductUnit.getMyItems()) {
        if (item.route.destination.equals(parent.getBlockPos())
            && item.route.insertionSide == side
            && ItemStack.isSameItemSameComponents(item.stack, stack)) {
          count += item.stack.getCount();
        }
      }
      for (var item : ductUnit.getItemsToAdd()) {
        if (item.route.destination.equals(parent.getBlockPos())
            && item.route.insertionSide == side
            && ItemStack.isSameItemSameComponents(item.stack, stack)) {
          count += item.stack.getCount();
        }
      }
    }
    for (ItemDuctUnit ductUnit : grid.getIdleSnapshot()) {
      for (var item : ductUnit.getMyItems()) {
        if (item.route.destination.equals(parent.getBlockPos())
            && item.route.insertionSide == side
            && ItemStack.isSameItemSameComponents(item.stack, stack)) {
          count += item.stack.getCount();
        }
      }
      for (var item : ductUnit.getItemsToAdd()) {
        if (item.route.destination.equals(parent.getBlockPos())
            && item.route.insertionSide == side
            && ItemStack.isSameItemSameComponents(item.stack, stack)) {
          count += item.stack.getCount();
        }
      }
    }
    return count;
  }

  private ItemDuctUnit findNode(net.minecraft.core.BlockPos pos, ItemGrid grid) {
    for (ItemDuctUnit node : grid.getNodeSnapshot()) {
      if (node.getPos().equals(pos)) return node;
    }
    return null;
  }
}
