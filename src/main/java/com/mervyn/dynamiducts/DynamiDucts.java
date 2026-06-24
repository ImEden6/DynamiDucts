package com.mervyn.dynamiducts;

import com.mervyn.dynamiducts.block.DuctBlock;
import com.mervyn.dynamiducts.block.DuctHitHelper;
import com.mervyn.dynamiducts.gametest.DDGameTestFunctions;
import com.mervyn.dynamiducts.blockentity.DuctBlockEntity;
import com.mervyn.dynamiducts.client.renderer.DuctBlockEntityRenderer;
import com.mervyn.dynamiducts.client.renderer.ItemDuctRenderer;
import com.mervyn.dynamiducts.init.DDAttachments;
import com.mervyn.dynamiducts.init.DDBlockEntities;
import com.mervyn.dynamiducts.init.DDBlocks;
import com.mervyn.dynamiducts.init.DDCreativeTab;
import com.mervyn.dynamiducts.init.DDDataAttachments;
import com.mervyn.dynamiducts.init.DDDataComponents;
import com.mervyn.dynamiducts.init.DDEntityTypes;
import com.mervyn.dynamiducts.init.DDItems;
import com.mervyn.dynamiducts.init.DDMenuTypes;
import com.mervyn.dynamiducts.init.DDRecipeSerializers;
import com.mervyn.dynamiducts.network.payload.AttachmentConfigPayload;
import com.mervyn.dynamiducts.network.payload.ItemTravelSyncPayload;
import com.mervyn.dynamiducts.network.payload.RelayConfigPayload;
import com.mervyn.dynamiducts.network.payload.TransportRenamePayload;
import com.mervyn.dynamiducts.network.payload.TransportRequestPayload;
import com.mervyn.dynamiducts.screen.AttachmentScreen;
import com.mervyn.dynamiducts.screen.RelayScreen;
import com.mervyn.dynamiducts.screen.TransportConfigScreen;
import com.mervyn.dynamiducts.screen.TransportScreen;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.slf4j.Logger;

@Mod(DynamiDucts.MODID)
public class DynamiDucts {

  public static final String MODID = "dynamiducts";
  public static final Logger LOG = LogUtils.getLogger();

  public DynamiDucts(IEventBus modEventBus, ModContainer modContainer) {
    DDAttachments.bootstrap();

    DDBlocks.BLOCKS.register(modEventBus);
    DDDataComponents.DATA_COMPONENTS.register(modEventBus);
    DDItems.ITEMS.register(modEventBus);
    DDBlockEntities.BLOCK_ENTITIES.register(modEventBus);
    DDMenuTypes.MENU_TYPES.register(modEventBus);
    DDEntityTypes.ENTITY_TYPES.register(modEventBus);
    DDDataAttachments.ATTACHMENT_TYPES.register(modEventBus);
    DDCreativeTab.CREATIVE_TABS.register(modEventBus);
    DDRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
    DDGameTestFunctions.TEST_FUNCTIONS.register(modEventBus);

    modEventBus.addListener(this::registerPayloads);

    modEventBus.addListener(com.mervyn.dynamiducts.datagen.DDDataGenerators::gatherDataClient);
    modEventBus.addListener(com.mervyn.dynamiducts.datagen.DDDataGenerators::gatherDataServer);

    modContainer.registerConfig(ModConfig.Type.COMMON, MNConfig.SPEC);
  }

  private void registerPayloads(RegisterPayloadHandlersEvent event) {
    PayloadRegistrar registrar = event.registrar("1");
    registrar.playToClient(
        ItemTravelSyncPayload.TYPE,
        ItemTravelSyncPayload.STREAM_CODEC,
        ItemTravelSyncPayload::handle);
    registrar.playToServer(
        AttachmentConfigPayload.TYPE,
        AttachmentConfigPayload.STREAM_CODEC,
        AttachmentConfigPayload::handle);
    registrar.playToServer(
        RelayConfigPayload.TYPE, RelayConfigPayload.STREAM_CODEC, RelayConfigPayload::handle);
    registrar.playToServer(
        TransportRenamePayload.TYPE,
        TransportRenamePayload.STREAM_CODEC,
        TransportRenamePayload::handle);
    registrar.playToServer(
        TransportRequestPayload.TYPE,
        TransportRequestPayload.STREAM_CODEC,
        TransportRequestPayload::handle);
  }

  @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
  public static class ClientModEvents {
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
      event.register(DDMenuTypes.ATTACHMENT_MENU.get(), AttachmentScreen::new);
      event.register(DDMenuTypes.RELAY_MENU.get(), RelayScreen::new);
      event.register(DDMenuTypes.TRANSPORT_MENU.get(), TransportScreen::new);
      event.register(DDMenuTypes.TRANSPORT_CONFIG_MENU.get(), TransportConfigScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
      ductRenderer(event, DDBlockEntities.ENERGY_DUCT_BASIC, false);
      ductRenderer(event, DDBlockEntities.ENERGY_DUCT_HARDENED, false);
      ductRenderer(event, DDBlockEntities.ENERGY_DUCT_REINFORCED, false);
      ductRenderer(event, DDBlockEntities.ENERGY_DUCT_SIGNALUM, false);
      ductRenderer(event, DDBlockEntities.ENERGY_DUCT_RESONANT, false);
      ductRenderer(event, DDBlockEntities.ENERGY_DUCT_SUPERCONDUCTOR, false);
      ductRenderer(event, DDBlockEntities.ENERGY_DUCT_REINFORCED_EMPTY, false);
      ductRenderer(event, DDBlockEntities.ENERGY_DUCT_SIGNALUM_EMPTY, false);
      ductRenderer(event, DDBlockEntities.ENERGY_DUCT_RESONANT_EMPTY, false);
      ductRenderer(event, DDBlockEntities.ENERGY_DUCT_SUPERCONDUCTOR_EMPTY, false);

      ductRenderer(event, DDBlockEntities.FLUID_DUCT_BASIC, false);
      ductRenderer(event, DDBlockEntities.FLUID_DUCT_BASIC_OPAQUE, false);
      ductRenderer(event, DDBlockEntities.FLUID_DUCT_HARDENED, false);
      ductRenderer(event, DDBlockEntities.FLUID_DUCT_HARDENED_OPAQUE, false);
      ductRenderer(event, DDBlockEntities.FLUID_DUCT_ENERGY, false);
      ductRenderer(event, DDBlockEntities.FLUID_DUCT_ENERGY_OPAQUE, false);
      ductRenderer(event, DDBlockEntities.FLUID_DUCT_SUPER, false);
      ductRenderer(event, DDBlockEntities.FLUID_DUCT_SUPER_OPAQUE, false);

      ductRenderer(event, DDBlockEntities.ITEM_DUCT_BASIC, true);
      ductRenderer(event, DDBlockEntities.ITEM_DUCT_BASIC_OPAQUE, true);
      ductRenderer(event, DDBlockEntities.ITEM_DUCT_FAST, true);
      ductRenderer(event, DDBlockEntities.ITEM_DUCT_FAST_OPAQUE, true);
      ductRenderer(event, DDBlockEntities.ITEM_DUCT_ENERGY, true);
      ductRenderer(event, DDBlockEntities.ITEM_DUCT_ENERGY_OPAQUE, true);
      ductRenderer(event, DDBlockEntities.ITEM_DUCT_ENERGY_FAST, true);
      ductRenderer(event, DDBlockEntities.ITEM_DUCT_ENERGY_FAST_OPAQUE, true);
      ductRenderer(event, DDBlockEntities.ITEM_DUCT_DENSE, true);
      ductRenderer(event, DDBlockEntities.ITEM_DUCT_DENSE_OPAQUE, true);
      ductRenderer(event, DDBlockEntities.ITEM_DUCT_VACUUM, true);
      ductRenderer(event, DDBlockEntities.ITEM_DUCT_VACUUM_OPAQUE, true);

      ductRenderer(event, DDBlockEntities.TRANSPORT_DUCT_BASIC, false);
      ductRenderer(event, DDBlockEntities.TRANSPORT_DUCT_LONG_RANGE, false);
      ductRenderer(event, DDBlockEntities.TRANSPORT_DUCT_LINKING, false);
      ductRenderer(event, DDBlockEntities.TRANSPORT_DUCT_FRAME, false);

      ductRenderer(event, DDBlockEntities.STRUCTURAL_DUCT, false);
      ductRenderer(event, DDBlockEntities.LUX_DUCT, false);

      event.registerEntityRenderer(
          DDEntityTypes.TRANSPORT.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
    }

    @SubscribeEvent
    public static void registerItemExtensions(RegisterClientExtensionsEvent event) {
      // TODO Item rendering uses the block center model via data gen (DDModelProvider).
      // The generated item model at models/item/<id>.json references the block's _center model,
      // and the item definition at items/<id>.json uses "type": "minecraft:model".
      // Future enhancement: implement a SpecialModelRenderer for more detailed item rendering.
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void ductRenderer(
        EntityRenderersEvent.RegisterRenderers event,
        DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>> holder,
        boolean itemDuct) {
      BlockEntityType type = holder.get();
      if (itemDuct) {
        event.registerBlockEntityRenderer(type, ItemDuctRenderer::new);
      } else {
        event.registerBlockEntityRenderer(type, DuctBlockEntityRenderer::new);
      }
    }
  }

  @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
  public static class ClientEvents {
    @SubscribeEvent
    public static void renderMultipartHighlight(ExtractBlockOutlineRenderStateEvent event) {
      if (event.getLevel() == null) {
        return;
      }

      BlockState state = event.getBlockState();
      if (!(state.getBlock() instanceof DuctBlock)) {
        return;
      }

      var blockEntity = event.getLevel().getBlockEntity(event.getBlockPos());
      var ductBE = blockEntity instanceof DuctBlockEntity duct ? duct : null;
      var hit = DuctHitHelper.resolve(state, ductBE, event.getBlockPos(), event.getHitResult());
      var box = DuctHitHelper.outlineBox(hit, ductBE, state);
      var cameraPos = event.getCamera().position();
      var blockPos = event.getBlockPos();

      event.setCanceled(true);
      event.addCustomRenderer(
          (blockOutlineRenderState, bufferSource, poseStack, isHighContrast, levelRenderState) -> {
            var builder =
                bufferSource.getBuffer(net.minecraft.client.renderer.rendertype.RenderTypes.LINES);
            renderLineBox(
                poseStack,
                builder,
                box.move(
                    blockPos.getX() - cameraPos.x,
                    blockPos.getY() - cameraPos.y,
                    blockPos.getZ() - cameraPos.z),
                0.0F,
                0.0F,
                0.0F,
                0.4F);
            return true;
          });
    }

    private static void renderLineBox(
        com.mojang.blaze3d.vertex.PoseStack poseStack,
        VertexConsumer consumer,
        net.minecraft.world.phys.AABB box,
        float r,
        float g,
        float b,
        float a) {
      double minX = box.minX;
      double minY = box.minY;
      double minZ = box.minZ;
      double maxX = box.maxX;
      double maxY = box.maxY;
      double maxZ = box.maxZ;

      com.mojang.blaze3d.vertex.PoseStack.Pose pose = poseStack.last();
      org.joml.Matrix4f poseMatrix = pose.pose();

      line(consumer, poseMatrix, pose, minX, minY, minZ, maxX, minY, minZ, r, g, b, a);
      line(consumer, poseMatrix, pose, maxX, minY, minZ, maxX, minY, maxZ, r, g, b, a);
      line(consumer, poseMatrix, pose, maxX, minY, maxZ, minX, minY, maxZ, r, g, b, a);
      line(consumer, poseMatrix, pose, minX, minY, maxZ, minX, minY, minZ, r, g, b, a);

      line(consumer, poseMatrix, pose, minX, maxY, minZ, maxX, maxY, minZ, r, g, b, a);
      line(consumer, poseMatrix, pose, maxX, maxY, minZ, maxX, maxY, maxZ, r, g, b, a);
      line(consumer, poseMatrix, pose, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a);
      line(consumer, poseMatrix, pose, minX, maxY, maxZ, minX, maxY, minZ, r, g, b, a);

      line(consumer, poseMatrix, pose, minX, minY, minZ, minX, maxY, minZ, r, g, b, a);
      line(consumer, poseMatrix, pose, maxX, minY, minZ, maxX, maxY, minZ, r, g, b, a);
      line(consumer, poseMatrix, pose, maxX, minY, maxZ, maxX, maxY, maxZ, r, g, b, a);
      line(consumer, poseMatrix, pose, minX, minY, maxZ, minX, maxY, maxZ, r, g, b, a);
    }

    private static void line(
        VertexConsumer consumer,
        org.joml.Matrix4f poseMatrix,
        com.mojang.blaze3d.vertex.PoseStack.Pose pose,
        double x1,
        double y1,
        double z1,
        double x2,
        double y2,
        double z2,
        float r,
        float g,
        float b,
        float a) {
      float dx = (float) (x2 - x1);
      float dy = (float) (y2 - y1);
      float dz = (float) (z2 - z1);
      float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
      if (len > 0) {
        dx /= len;
        dy /= len;
        dz /= len;
      }
      consumer
          .addVertex(poseMatrix, (float) x1, (float) y1, (float) z1)
          .setColor(r, g, b, a)
          .setNormal(pose, dx, dy, dz);
      consumer
          .addVertex(poseMatrix, (float) x2, (float) y2, (float) z2)
          .setColor(r, g, b, a)
          .setNormal(pose, dx, dy, dz);
    }
  }
}
