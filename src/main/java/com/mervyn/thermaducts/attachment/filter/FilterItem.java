package com.mervyn.thermaducts.attachment.filter;

import com.mervyn.thermaducts.ThermaDucts;
import com.mervyn.thermaducts.blockentity.DuctBlockEntity;
import com.mervyn.thermaducts.core.attachment.AttachmentTier;
import com.mervyn.thermaducts.core.attachment.ConnectionBase;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

public class FilterItem extends ConnectionBase {

  public static final Identifier ID =
      Identifier.fromNamespaceAndPath(ThermaDucts.MODID, "filter_item");

  public FilterItem(DuctBlockEntity parent, Direction side, AttachmentTier tier) {
    super(parent, side, tier);
  }

  @Override
  public Identifier getId() {
    return ID;
  }

  @Override
  public boolean isFilter() {
    return true;
  }

  @Override
  public boolean canSend() {
    return true;
  }

  @Override
  protected void performAction() {}
}
