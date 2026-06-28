package com.mervyn.thermaducts.client.renderer;

import com.mervyn.thermaducts.ThermaDucts;
import com.mervyn.thermaducts.block.DuctBlock;
import com.mervyn.thermaducts.block.TransportDuctBlock;
import com.mervyn.thermaducts.blockentity.DuctBlockEntity;
import com.mervyn.thermaducts.blockentity.TransportDuctBlockEntity;
import com.mervyn.thermaducts.ccl.render.CCRenderState;
import com.mervyn.thermaducts.ccl.vec.Translation;
import com.mervyn.thermaducts.ccl.vec.uv.IconTransformation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;

@EventBusSubscriber(modid = ThermaDucts.MODID, value = Dist.CLIENT)
public class TransportSectionRenderer {

  private static final Map<String, TextureAtlasSprite> SPRITE_CACHE = new ConcurrentHashMap<>();

  private static final Map<TransportDuctBlockEntity.Tier, TubeTextures> TIER_TEXTURES =
      Map.of(
          TransportDuctBlockEntity.Tier.BASIC,
              new TubeTextures("copper_trans", "copper_band", "green_glass", 96),
          TransportDuctBlockEntity.Tier.LONG_RANGE,
              new TubeTextures("lead_trans", "lead_band", "green_glass", 80),
          TransportDuctBlockEntity.Tier.LINKING,
              new TubeTextures("enderium_trans", "enderium_band", "green_glass", 128),
          TransportDuctBlockEntity.Tier.FRAME,
              new TubeTextures("copper_trans", "copper_band", null, 0));

  @SubscribeEvent
  public static void onAddSectionGeometry(AddSectionGeometryEvent event) {
    Level level = event.getLevel();
    BlockPos origin = event.getSectionOrigin();

    List<TransportRenderData> renderData = new ArrayList<>();

    for (int x = 0; x < 16; x++) {
      for (int y = 0; y < 16; y++) {
        for (int z = 0; z < 16; z++) {
          BlockPos pos = origin.offset(x, y, z);
          if (!(level.getBlockEntity(pos) instanceof TransportDuctBlockEntity tbe)) continue;
          if (!(tbe.getBlockState().getBlock() instanceof TransportDuctBlock)) continue;

          TransportDuctBlockEntity.Tier tier = tbe.getTier();
          if (tier == TransportDuctBlockEntity.Tier.FRAME) continue;

          TubeTextures tex = TIER_TEXTURES.get(tier);
          if (tex == null) continue;

          int connectionMask = getConnectionMask(tbe, tbe.getBlockState());
          boolean[] externalSides = new boolean[6];
          for (Direction dir : Direction.values()) {
            externalSides[dir.ordinal()] = isExternalConnection(tbe, tbe.getBlockState(), dir);
          }

          renderData.add(new TransportRenderData(pos, connectionMask, externalSides, tex));
        }
      }
    }

    if (renderData.isEmpty()) return;

    event.addRenderer(
        context -> {
          DuctModels.init();
          CCRenderState ccrs = CCRenderState.instance();

          for (TransportRenderData data : renderData) {
            renderTransportDuct(ccrs, context, data);
          }
        });
  }

  private static void renderTransportDuct(
      CCRenderState ccrs,
      AddSectionGeometryEvent.SectionRenderingContext context,
      TransportRenderData data) {
    int lx = data.pos.getX() & 15;
    int ly = data.pos.getY() & 15;
    int lz = data.pos.getZ() & 15;
    Translation trans = new Translation(lx + 0.5, ly + 0.5, lz + 0.5);
    int light = LevelRenderer.getLightCoords(context.getRegion(), data.pos);

    VertexConsumer translucentConsumer =
        context.getOrCreateChunkBuffer(
            net.minecraft.client.renderer.chunk.ChunkSectionLayer.TRANSLUCENT);
    VertexConsumer cutoutConsumer =
        context.getOrCreateChunkBuffer(
            net.minecraft.client.renderer.chunk.ChunkSectionLayer.CUTOUT);

    TextureAtlasSprite frameSprite = getSprite("block/duct/base/" + data.tex.frame);
    TextureAtlasSprite bandSprite =
        data.tex.band != null ? getSprite("block/duct/base/" + data.tex.band) : null;
    IconTransformation frameIcon = new IconTransformation(frameSprite);
    IconTransformation bandIcon = bandSprite != null ? new IconTransformation(bandSprite) : null;

    for (Direction dir : Direction.values()) {
      if (data.externalSides[dir.ordinal()] && bandIcon != null) {
        ccrs.reset();
        ccrs.computeLighting = false;
        ccrs.bind(cutoutConsumer, net.minecraft.client.renderer.Sheets.cutoutBlockSheet().format());
        ccrs.brightness = light;
        ccrs.baseColour = 0xFFFFFFFF;
        DuctModels.modelTransportConnection[64 + dir.ordinal()].render(ccrs, trans, bandIcon);
      }
    }

    if (DuctModels.modelTransportConnection[data.connectionMask].verts.length != 0) {
      ccrs.reset();
      ccrs.computeLighting = false;
      ccrs.bind(
          translucentConsumer,
          net.minecraft.client.renderer.Sheets.translucentBlockSheet().format());
      ccrs.brightness = light;
      ccrs.baseColour = 0xFFFFFFFF;
      DuctModels.modelTransportConnection[data.connectionMask].render(ccrs, trans, frameIcon);
    }

    if (data.tex.fill != null && data.tex.fillAlpha > 0) {
      TextureAtlasSprite fillSprite = getSprite("block/duct/base/" + data.tex.fill);
      IconTransformation fillIcon = new IconTransformation(fillSprite);
      ccrs.reset();
      ccrs.computeLighting = false;
      ccrs.bind(
          translucentConsumer,
          net.minecraft.client.renderer.Sheets.translucentBlockSheet().format());
      ccrs.brightness = light;
      ccrs.baseColour = rgba(255, 255, 255, data.tex.fillAlpha);
      if (DuctModels.modelTransport[data.connectionMask].verts.length != 0) {
        DuctModels.modelTransport[data.connectionMask].render(ccrs, trans, fillIcon);
      }
      ccrs.baseColour = 0xFFFFFFFF;
    }
  }

  private static int getConnectionMask(DuctBlockEntity be, BlockState state) {
    int mask = 0;
    for (Direction dir : Direction.values()) {
      if (state.getValue(DuctBlock.PROPERTY_BY_DIRECTION.get(dir))
          || be.getAttachment(dir) != null) {
        mask |= (1 << dir.ordinal());
      }
    }
    return mask;
  }

  private static boolean isExternalConnection(DuctBlockEntity be, BlockState state, Direction dir) {
    boolean connected = state.getValue(DuctBlock.PROPERTY_BY_DIRECTION.get(dir));
    if (!connected && be.getAttachment(dir) == null) return false;
    if (be.getLevel() == null) return true;
    return !(be.getLevel().getBlockState(be.getBlockPos().relative(dir)).getBlock()
        instanceof DuctBlock);
  }

  private static TextureAtlasSprite getSprite(String path) {
    return SPRITE_CACHE.computeIfAbsent(
        path,
        p ->
            Minecraft.getInstance()
                .getAtlasManager()
                .getAtlasOrThrow(net.minecraft.data.AtlasIds.BLOCKS)
                .getSprite(Identifier.fromNamespaceAndPath(ThermaDucts.MODID, p)));
  }

  public static void clearSpriteCache() {
    SPRITE_CACHE.clear();
  }

  private static int rgba(int r, int g, int b, int a) {
    return (r & 0xFF) << 24 | (g & 0xFF) << 16 | (b & 0xFF) << 8 | a & 0xFF;
  }

  private record TubeTextures(String frame, String band, String fill, int fillAlpha) {}

  private record TransportRenderData(
      BlockPos pos, int connectionMask, boolean[] externalSides, TubeTextures tex) {}
}
