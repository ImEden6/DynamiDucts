package com.mervyn.thermaducts.core.attachment;

import com.mervyn.thermaducts.blockentity.DuctBlockEntity;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;

public class AttachmentRegistry {

  private static final Map<Identifier, AttachmentEntry> REGISTRY = new LinkedHashMap<>();

  public static void register(Identifier id, AttachmentFactory factory) {
    REGISTRY.put(id, new AttachmentEntry(id, factory));
  }

  public static void register(
      Identifier id, BiFunction<DuctBlockEntity, Direction, Attachment> factory) {
    register(id, (parent, side, tag) -> factory.apply(parent, side));
  }

  public static Attachment create(Identifier id, DuctBlockEntity parent, Direction side) {
    return create(id, parent, side, new CompoundTag());
  }

  public static Attachment create(
      Identifier id, DuctBlockEntity parent, Direction side, CompoundTag tag) {
    AttachmentEntry entry = REGISTRY.get(id);
    if (entry == null) return null;
    return entry.factory.create(parent, side, tag);
  }

  public static Map<Identifier, AttachmentEntry> getAll() {
    return Collections.unmodifiableMap(REGISTRY);
  }

  @FunctionalInterface
  public interface AttachmentFactory {
    Attachment create(DuctBlockEntity parent, Direction side, CompoundTag tag);
  }

  public record AttachmentEntry(Identifier id, AttachmentFactory factory) {}
}
