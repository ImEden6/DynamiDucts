package com.mervyn.dynamiducts.init;

import com.mervyn.dynamiducts.attachment.filter.FilterFluid;
import com.mervyn.dynamiducts.attachment.filter.FilterItem;
import com.mervyn.dynamiducts.attachment.relay.Relay;
import com.mervyn.dynamiducts.attachment.retriever.RetrieverFluid;
import com.mervyn.dynamiducts.attachment.retriever.RetrieverItem;
import com.mervyn.dynamiducts.attachment.servo.ServoFluid;
import com.mervyn.dynamiducts.attachment.servo.ServoItem;
import com.mervyn.dynamiducts.core.attachment.AttachmentRegistry;
import com.mervyn.dynamiducts.core.attachment.AttachmentTier;
import net.minecraft.nbt.CompoundTag;

public final class DDAttachments {

  private static boolean initialized;

  private DDAttachments() {}

  public static void bootstrap() {
    if (initialized) {
      return;
    }
    initialized = true;

    AttachmentRegistry.register(Relay.ID, (parent, side, tag) -> new Relay(parent, side));

    AttachmentRegistry.register(
        ServoItem.ID, (parent, side, tag) -> new ServoItem(parent, side, tierFromTag(tag)));
    AttachmentRegistry.register(
        ServoFluid.ID, (parent, side, tag) -> new ServoFluid(parent, side, tierFromTag(tag)));
    AttachmentRegistry.register(
        FilterItem.ID, (parent, side, tag) -> new FilterItem(parent, side, tierFromTag(tag)));
    AttachmentRegistry.register(
        FilterFluid.ID, (parent, side, tag) -> new FilterFluid(parent, side, tierFromTag(tag)));
    AttachmentRegistry.register(
        RetrieverItem.ID, (parent, side, tag) -> new RetrieverItem(parent, side, tierFromTag(tag)));
    AttachmentRegistry.register(
        RetrieverFluid.ID,
        (parent, side, tag) -> new RetrieverFluid(parent, side, tierFromTag(tag)));
  }

  private static AttachmentTier tierFromTag(CompoundTag tag) {
    if (!tag.contains("TierIndex")) {
      return AttachmentTier.BASIC;
    }
    return AttachmentTier.byIndex(tag.getInt("TierIndex").orElse(0));
  }
}
