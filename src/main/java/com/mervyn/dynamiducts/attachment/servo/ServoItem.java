package com.mervyn.dynamiducts.attachment.servo;

import com.mervyn.dynamiducts.DynamiDucts;
import com.mervyn.dynamiducts.blockentity.DuctBlockEntity;
import com.mervyn.dynamiducts.core.attachment.AttachmentTier;
import com.mervyn.dynamiducts.core.attachment.ConnectionBase;
import com.mervyn.dynamiducts.core.duct.DuctToken;
import com.mervyn.dynamiducts.core.network.Route;
import com.mervyn.dynamiducts.duct.item.ItemDuctUnit;
import com.mervyn.dynamiducts.duct.item.ItemGrid;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class ServoItem extends ConnectionBase {

  public static final Identifier ID =
      Identifier.fromNamespaceAndPath(DynamiDucts.MODID, "servo_item");

  public ServoItem(DuctBlockEntity parent, Direction side, AttachmentTier tier) {
    super(parent, side, tier);
  }

  @Override
  public Identifier getId() {
    return ID;
  }

  @Override
  public boolean isServo() {
    return true;
  }

  @Override
  public boolean canSend() {
    return true;
  }

  @Override
  protected void performAction() {
    var level = parent.getLevel();
    if (level == null) return;

    var unit = parent.getDuctUnit(DuctToken.ITEM);
    if (!(unit instanceof ItemDuctUnit itemUnit)) return;
    if (!(itemUnit.getGrid() instanceof ItemGrid grid)) return;

    ResourceHandler<ItemResource> source = level.getCapability(
        Capabilities.Item.BLOCK, parent.getBlockPos().relative(side), side.getOpposite());
    if (source == null) return;

    int maxSend = filter.getMaxStockOrDefault(tier.stackSize());

    for (int slot = 0; slot < source.size(); slot++) {
      ItemResource peekRes = source.getResource(slot);
      if (peekRes.isEmpty()) continue;
      int peekAmount = (int) source.getAmountAsLong(slot);
      if (peekAmount <= 0) continue;

      ItemStack peek = peekRes.toStack(peekAmount);
      if (!filter.matchesItem(peek)) continue;

      List<Route> routes = grid.getSortedRoutes(itemUnit, filter.getRouteType());
      Route route = findRouteForItem(peek, routes, grid);
      if (route == null) continue;

      try (var tx = Transaction.openRoot()) {
        int extracted = source.extract(slot, peekRes, Math.min(maxSend, peekAmount), tx);
        if (extracted <= 0) continue;

        ItemStack extractedStack = peekRes.toStack(extracted);

        if (tier.multiStack() && extracted < maxSend) {
          for (int s = slot + 1; s < source.size() && extracted < maxSend; s++) {
            ItemResource otherRes = source.getResource(s);
            if (otherRes.isEmpty()) continue;
            if (!otherRes.matches(peekRes.toStack(1))) continue;
            int otherAmount = (int) Math.min(source.getAmountAsLong(s), maxSend - extracted);
            if (otherAmount <= 0) continue;
            int extra = source.extract(s, otherRes, otherAmount, tx);
            if (extra > 0) {
              extracted += extra;
            }
          }
        }

        tx.commit();
        itemUnit.insertItemWithRoute(extractedStack, side, route, tier.speedBoost());
        return;
      }
    }
  }

  private Route findRouteForItem(ItemStack stack, List<Route> routes, ItemGrid grid) {
    ItemResource resource = ItemResource.of(stack);
    int amount = stack.getCount();
    for (Route route : routes) {
      if (route.destination.equals(parent.getBlockPos()) && route.insertionSide == side) continue;
      for (ItemDuctUnit node : grid.getNodeSnapshot()) {
        if (!node.getPos().equals(route.destination)) continue;
        if (!grid.acceptsDestinationItem(node, route.insertionSide, stack)) continue;

        ResourceHandler<ItemResource> target = node.getTileCache(route.insertionSide);
        if (target == null) continue;

        int totalPossible = 0;
        for (int i = 0; i < target.size(); i++) {
          try (var tx = Transaction.openRoot()) {
            int inserted = target.insert(i, resource, amount - totalPossible, tx);
            totalPossible += inserted;
            if (totalPossible >= amount) break;
          }
        }
        if (totalPossible > 0) return route;
      }
    }
    return null;
  }
}
