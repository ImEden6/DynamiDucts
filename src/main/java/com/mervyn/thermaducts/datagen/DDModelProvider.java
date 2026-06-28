package com.mervyn.thermaducts.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mervyn.thermaducts.ThermaDucts;
import com.mervyn.thermaducts.block.DuctBlock;
import com.mervyn.thermaducts.init.DDBlocks;
import com.mervyn.thermaducts.init.DDItems;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

public class DDModelProvider extends ModelProvider {

  public static final Map<String, String> DUCT_TEXTURES = new HashMap<>();

  static {
    DUCT_TEXTURES.put("energy_duct_basic", "lead");
    DUCT_TEXTURES.put("energy_duct_hardened", "invar");
    DUCT_TEXTURES.put("energy_duct_reinforced", "electrum");
    DUCT_TEXTURES.put("energy_duct_signalum", "signalum");
    DUCT_TEXTURES.put("energy_duct_resonant", "enderium");
    DUCT_TEXTURES.put("energy_duct_superconductor", "enderium");
    DUCT_TEXTURES.put("energy_duct_reinforced_empty", "electrum");
    DUCT_TEXTURES.put("energy_duct_signalum_empty", "signalum");
    DUCT_TEXTURES.put("energy_duct_resonant_empty", "enderium");
    DUCT_TEXTURES.put("energy_duct_superconductor_empty", "enderium");

    DUCT_TEXTURES.put("fluid_duct_basic", "copper_trans");
    DUCT_TEXTURES.put("fluid_duct_basic_opaque", "copper");
    DUCT_TEXTURES.put("fluid_duct_hardened", "invar_trans");
    DUCT_TEXTURES.put("fluid_duct_hardened_opaque", "invar");
    DUCT_TEXTURES.put("fluid_duct_energy", "invar_signalum_trans");
    DUCT_TEXTURES.put("fluid_duct_energy_opaque", "invar_signalum");
    DUCT_TEXTURES.put("fluid_duct_super", "invar_trans");
    DUCT_TEXTURES.put("fluid_duct_super_opaque", "invar");

    DUCT_TEXTURES.put("item_duct_basic", "tin_trans");
    DUCT_TEXTURES.put("item_duct_basic_opaque", "tin");
    DUCT_TEXTURES.put("item_duct_dense", "tin_trans");
    DUCT_TEXTURES.put("item_duct_dense_opaque", "tin");
    DUCT_TEXTURES.put("item_duct_vacuum", "tin_trans");
    DUCT_TEXTURES.put("item_duct_vacuum_opaque", "tin");
    DUCT_TEXTURES.put("item_duct_fast", "tin_trans");
    DUCT_TEXTURES.put("item_duct_fast_opaque", "tin_alt");
    DUCT_TEXTURES.put("item_duct_energy", "tin_signalum_trans");
    DUCT_TEXTURES.put("item_duct_energy_opaque", "tin_signalum");
    DUCT_TEXTURES.put("item_duct_energy_fast", "tin_signalum_trans");
    DUCT_TEXTURES.put("item_duct_energy_fast_opaque", "tin_alt_signalum");

    DUCT_TEXTURES.put("transport_duct_basic", "copper");
    DUCT_TEXTURES.put("transport_duct_long_range", "lead");
    DUCT_TEXTURES.put("transport_duct_linking", "enderium");
    DUCT_TEXTURES.put("transport_duct_frame", "copper");

    DUCT_TEXTURES.put("structural_duct", "structure");
    DUCT_TEXTURES.put("lux_duct", "lumium");
  }

  public DDModelProvider(PackOutput output) {
    super(output, ThermaDucts.MODID);
  }

  @Override
  protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
    DDBlocks.BLOCKS
        .getEntries()
        .forEach(
            entry -> {
              Block block = entry.get();
              if (block instanceof DuctBlock) {
                registerDuctBlock(blockModels, itemModels, entry);
              }
            });

    simpleItem(itemModels, DDItems.SERVO_BASIC, "servo_0");
    simpleItem(itemModels, DDItems.SERVO_HARDENED, "servo_1");
    simpleItem(itemModels, DDItems.SERVO_REINFORCED, "servo_2");
    simpleItem(itemModels, DDItems.SERVO_SIGNALUM, "servo_3");
    simpleItem(itemModels, DDItems.SERVO_RESONANT, "servo_4");

    simpleItem(itemModels, DDItems.FILTER_BASIC, "filter_0");
    simpleItem(itemModels, DDItems.FILTER_HARDENED, "filter_1");
    simpleItem(itemModels, DDItems.FILTER_REINFORCED, "filter_2");
    simpleItem(itemModels, DDItems.FILTER_SIGNALUM, "filter_3");
    simpleItem(itemModels, DDItems.FILTER_RESONANT, "filter_4");

    simpleItem(itemModels, DDItems.RETRIEVER_BASIC, "retriever_0");
    simpleItem(itemModels, DDItems.RETRIEVER_HARDENED, "retriever_1");
    simpleItem(itemModels, DDItems.RETRIEVER_REINFORCED, "retriever_2");
    simpleItem(itemModels, DDItems.RETRIEVER_SIGNALUM, "retriever_3");
    simpleItem(itemModels, DDItems.RETRIEVER_RESONANT, "retriever_4");

    simpleItem(itemModels, DDItems.RELAY, "relay");
    simpleItem(itemModels, DDItems.WRENCH, "crescent_hammer");

    simpleItem(itemModels, DDItems.LEAD_INGOT, "ingot_lead");
    simpleItem(itemModels, DDItems.LEAD_NUGGET, "nugget_lead");
    simpleItem(itemModels, DDItems.TIN_INGOT, "ingot_tin");
    simpleItem(itemModels, DDItems.TIN_NUGGET, "nugget_tin");
    simpleItem(itemModels, DDItems.SILVER_INGOT, "ingot_silver");
    simpleItem(itemModels, DDItems.SILVER_NUGGET, "nugget_silver");
    simpleItem(itemModels, DDItems.INVAR_INGOT, "ingot_invar");
    simpleItem(itemModels, DDItems.INVAR_NUGGET, "nugget_invar");
    simpleItem(itemModels, DDItems.ELECTRUM_INGOT, "ingot_electrum");
    simpleItem(itemModels, DDItems.ELECTRUM_NUGGET, "nugget_electrum");
    simpleItem(itemModels, DDItems.BRONZE_INGOT, "ingot_bronze");
    simpleItem(itemModels, DDItems.BRONZE_NUGGET, "nugget_bronze");
    simpleItem(itemModels, DDItems.SIGNALUM_INGOT, "ingot_signalum");
    simpleItem(itemModels, DDItems.SIGNALUM_NUGGET, "nugget_signalum");
    simpleItem(itemModels, DDItems.ENDERIUM_INGOT, "ingot_enderium");
    simpleItem(itemModels, DDItems.ENDERIUM_NUGGET, "nugget_enderium");
    simpleItem(itemModels, DDItems.LUMIUM_INGOT, "ingot_lumium");
    simpleItem(itemModels, DDItems.LUMIUM_NUGGET, "nugget_lumium");
  }

  private void registerDuctBlock(
      BlockModelGenerators blockModels,
      ItemModelGenerators itemModels,
      DeferredHolder<Block, ? extends Block> entry) {
    Block block = entry.get();
    String name = entry.getId().getPath();
    String baseTex = DUCT_TEXTURES.getOrDefault(name, "structure");
    Identifier texture =
        Identifier.fromNamespaceAndPath(ThermaDucts.MODID, "block/duct/base/" + baseTex);

    boolean transparentSpecial = name.equals("item_duct_dense") || name.equals("item_duct_vacuum");
    String renderType = (baseTex.contains("trans") || transparentSpecial) ? "cutout" : "solid";

    Identifier centerModelLoc =
        Identifier.fromNamespaceAndPath(ThermaDucts.MODID, "block/" + name + "_center");
    blockModels.modelOutput.accept(
        centerModelLoc,
        () -> {
          JsonObject json = new JsonObject();
          json.addProperty("parent", "block/block");
          json.addProperty("render_type", renderType);
          JsonObject textures = new JsonObject();
          textures.addProperty("particle", texture.toString());
          textures.addProperty("duct", texture.toString());
          json.add("textures", textures);

          JsonArray elements = new JsonArray();
          JsonObject element = new JsonObject();
          JsonArray from = new JsonArray();
          from.add(5);
          from.add(5);
          from.add(5);
          element.add("from", from);
          JsonArray to = new JsonArray();
          to.add(11);
          to.add(11);
          to.add(11);
          element.add("to", to);

          JsonObject faces = new JsonObject();
          for (Direction dir : Direction.values()) {
            JsonObject face = new JsonObject();
            JsonArray uv = new JsonArray();
            uv.add(5);
            uv.add(5);
            uv.add(11);
            uv.add(11);
            face.add("uv", uv);
            face.addProperty("texture", "#duct");
            faces.add(dir.getSerializedName(), face);
          }
          element.add("faces", faces);
          elements.add(element);
          json.add("elements", elements);

          return json;
        });

    Map<Direction, Identifier> armModelLocs = new HashMap<>();
    armModelLocs.put(
        Direction.DOWN,
        armModel(
            blockModels,
            name,
            "down",
            texture,
            renderType,
            5,
            0,
            5,
            11,
            5,
            11,
            Direction.DOWN,
            Direction.UP));
    armModelLocs.put(
        Direction.UP,
        armModel(
            blockModels,
            name,
            "up",
            texture,
            renderType,
            5,
            11,
            5,
            11,
            16,
            11,
            Direction.UP,
            Direction.DOWN));
    armModelLocs.put(
        Direction.NORTH,
        armModel(
            blockModels,
            name,
            "north",
            texture,
            renderType,
            5,
            5,
            0,
            11,
            11,
            5,
            Direction.NORTH,
            Direction.SOUTH));
    armModelLocs.put(
        Direction.SOUTH,
        armModel(
            blockModels,
            name,
            "south",
            texture,
            renderType,
            5,
            5,
            11,
            11,
            11,
            16,
            Direction.SOUTH,
            Direction.NORTH));
    armModelLocs.put(
        Direction.WEST,
        armModel(
            blockModels,
            name,
            "west",
            texture,
            renderType,
            0,
            5,
            5,
            5,
            11,
            11,
            Direction.WEST,
            Direction.EAST));
    armModelLocs.put(
        Direction.EAST,
        armModel(
            blockModels,
            name,
            "east",
            texture,
            renderType,
            11,
            5,
            5,
            16,
            11,
            11,
            Direction.EAST,
            Direction.WEST));

    MultiPartGenerator multipart =
        MultiPartGenerator.multiPart(block).with(BlockModelGenerators.plainVariant(centerModelLoc));

    for (Direction dir : Direction.values()) {
      multipart =
          multipart.with(
              BlockModelGenerators.condition(DuctBlock.PROPERTY_BY_DIRECTION.get(dir), true),
              BlockModelGenerators.plainVariant(armModelLocs.get(dir)));
    }

    blockModels.blockStateOutput.accept(multipart);

    // Register block item model - references the center block model
    Item item = block.asItem();
    Identifier itemModelLoc = ModelLocationUtils.getModelLocation(item);
    itemModels.modelOutput.accept(
        itemModelLoc,
        () -> {
          JsonObject json = new JsonObject();
          json.addProperty("parent", centerModelLoc.toString());
          return json;
        });
    itemModels.itemModelOutput.accept(
        item,
        ItemModelUtils.specialModel(
            centerModelLoc,
            new com.mervyn.thermaducts.client.renderer.DuctBlockItemRenderer.Unbaked()));
  }

  private Identifier armModel(
      BlockModelGenerators blockModels,
      String ductName,
      String dirName,
      Identifier texture,
      String renderType,
      int x1,
      int y1,
      int z1,
      int x2,
      int y2,
      int z2,
      Direction outward,
      Direction inward) {
    Identifier loc =
        Identifier.fromNamespaceAndPath(ThermaDucts.MODID, "block/" + ductName + "_arm_" + dirName);
    blockModels.modelOutput.accept(
        loc,
        () -> {
          JsonObject json = new JsonObject();
          json.addProperty("parent", "block/block");
          json.addProperty("render_type", renderType);
          JsonObject textures = new JsonObject();
          textures.addProperty("duct", texture.toString());
          json.add("textures", textures);

          JsonArray elements = new JsonArray();
          JsonObject element = new JsonObject();
          JsonArray from = new JsonArray();
          from.add(x1);
          from.add(y1);
          from.add(z1);
          element.add("from", from);
          JsonArray to = new JsonArray();
          to.add(x2);
          to.add(y2);
          to.add(z2);
          element.add("to", to);

          JsonObject faces = new JsonObject();
          for (Direction face : Direction.values()) {
            if (face == outward || face == inward) continue;
            JsonObject faceJson = new JsonObject();
            float[] uv = faceUVs(face, x1, y1, z1, x2, y2, z2);
            JsonArray uvJson = new JsonArray();
            uvJson.add(uv[0]);
            uvJson.add(uv[1]);
            uvJson.add(uv[2]);
            uvJson.add(uv[3]);
            faceJson.add("uv", uvJson);
            faceJson.addProperty("texture", "#duct");
            faces.add(face.getSerializedName(), faceJson);
          }
          element.add("faces", faces);
          elements.add(element);
          json.add("elements", elements);

          return json;
        });
    return loc;
  }

  private static float[] faceUVs(Direction face, int x1, int y1, int z1, int x2, int y2, int z2) {
    return switch (face) {
      case DOWN, UP -> new float[] {x1, z1, x2, z2};
      case NORTH, SOUTH -> new float[] {x1, 16 - y2, x2, 16 - y1};
      case WEST, EAST -> new float[] {z1, 16 - y2, z2, 16 - y1};
    };
  }

  private void simpleItem(
      ItemModelGenerators itemModels, DeferredItem<?> item, String textureName) {
    Item realItem = item.get();
    Identifier texture = Identifier.fromNamespaceAndPath(ThermaDucts.MODID, "item/" + textureName);
    Identifier modelLoc =
        ModelTemplates.FLAT_ITEM.create(
            ModelLocationUtils.getModelLocation(realItem),
            TextureMapping.layer0(new Material(texture)),
            itemModels.modelOutput);
    itemModels.itemModelOutput.accept(realItem, ItemModelUtils.plainModel(modelLoc));
  }
}
