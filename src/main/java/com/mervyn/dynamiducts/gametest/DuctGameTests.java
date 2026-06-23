package com.mervyn.dynamiducts.gametest;

import com.mervyn.dynamiducts.DynamiDucts;
import com.mervyn.dynamiducts.blockentity.EnergyDuctBlockEntity;
import com.mervyn.dynamiducts.blockentity.FluidDuctBlockEntity;
import com.mervyn.dynamiducts.blockentity.ItemDuctBlockEntity;
import com.mervyn.dynamiducts.blockentity.StructuralDuctBlockEntity;
import com.mervyn.dynamiducts.blockentity.TransportDuctBlockEntity;
import com.mervyn.dynamiducts.init.DDBlocks;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

@EventBusSubscriber(modid = DynamiDucts.MODID)
public class DuctGameTests {

    private static final Identifier STRUCTURE = Identifier.fromNamespaceAndPath(DynamiDucts.MODID, "platform");
    private static final Holder<TestEnvironmentDefinition<?>> ENV =
        Holder.direct(new TestEnvironmentDefinition.AllOf());

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        // Energy Duct
        test(event, "energy_duct_basic", helper -> {
            helper.setBlock(1, 1, 1, DDBlocks.ENERGY_DUCT_BASIC.get());
            helper.assertBlockPresent(DDBlocks.ENERGY_DUCT_BASIC.get(), 1, 1, 1);
            helper.assertTrue(
                helper.getBlockEntity(BlockPos.ZERO.offset(1, 1, 1), EnergyDuctBlockEntity.class) != null,
                "Expected EnergyDuctBlockEntity");
            helper.succeed();
        });

        // Hardened Energy Duct
        test(event, "energy_duct_hardened", helper -> {
            helper.setBlock(1, 1, 1, DDBlocks.ENERGY_DUCT_HARDENED.get());
            helper.assertBlockPresent(DDBlocks.ENERGY_DUCT_HARDENED.get(), 1, 1, 1);
            helper.succeed();
        });

        // Superconductor Energy Duct
        test(event, "energy_duct_superconductor", helper -> {
            helper.setBlock(1, 1, 1, DDBlocks.ENERGY_DUCT_SUPERCONDUCTOR.get());
            helper.assertBlockPresent(DDBlocks.ENERGY_DUCT_SUPERCONDUCTOR.get(), 1, 1, 1);
            helper.succeed();
        });

        // Fluid Duct
        test(event, "fluid_duct_basic", helper -> {
            helper.setBlock(1, 1, 1, DDBlocks.FLUID_DUCT_BASIC.get());
            helper.assertBlockPresent(DDBlocks.FLUID_DUCT_BASIC.get(), 1, 1, 1);
            helper.assertTrue(
                helper.getBlockEntity(BlockPos.ZERO.offset(1, 1, 1), FluidDuctBlockEntity.class) != null,
                "Expected FluidDuctBlockEntity");
            helper.succeed();
        });

        // Fluid Extraction Duct
        test(event, "fluid_duct_energy", helper -> {
            helper.setBlock(1, 1, 1, DDBlocks.FLUID_DUCT_ENERGY.get());
            helper.assertBlockPresent(DDBlocks.FLUID_DUCT_ENERGY.get(), 1, 1, 1);
            helper.succeed();
        });

        // Fluid Extraction Opaque
        test(event, "fluid_duct_energy_opaque", helper -> {
            helper.setBlock(1, 1, 1, DDBlocks.FLUID_DUCT_ENERGY_OPAQUE.get());
            helper.assertBlockPresent(DDBlocks.FLUID_DUCT_ENERGY_OPAQUE.get(), 1, 1, 1);
            helper.succeed();
        });

        // Temperate Fluid Duct
        test(event, "fluid_duct_hardened", helper -> {
            helper.setBlock(1, 1, 1, DDBlocks.FLUID_DUCT_HARDENED.get());
            helper.assertBlockPresent(DDBlocks.FLUID_DUCT_HARDENED.get(), 1, 1, 1);
            helper.succeed();
        });

        // Item Duct
        test(event, "item_duct_basic", helper -> {
            helper.setBlock(1, 1, 1, DDBlocks.ITEM_DUCT_BASIC.get());
            helper.assertBlockPresent(DDBlocks.ITEM_DUCT_BASIC.get(), 1, 1, 1);
            helper.assertTrue(
                helper.getBlockEntity(BlockPos.ZERO.offset(1, 1, 1), ItemDuctBlockEntity.class) != null,
                "Expected ItemDuctBlockEntity");
            helper.succeed();
        });

        // Dense Item Duct
        test(event, "item_duct_dense", helper -> {
            helper.setBlock(1, 1, 1, DDBlocks.ITEM_DUCT_DENSE.get());
            helper.assertBlockPresent(DDBlocks.ITEM_DUCT_DENSE.get(), 1, 1, 1);
            helper.succeed();
        });

        // Extraction Item Duct
        test(event, "item_duct_energy", helper -> {
            helper.setBlock(1, 1, 1, DDBlocks.ITEM_DUCT_ENERGY.get());
            helper.assertBlockPresent(DDBlocks.ITEM_DUCT_ENERGY.get(), 1, 1, 1);
            helper.succeed();
        });

        // Transport Duct
        test(event, "transport_duct_basic", helper -> {
            helper.setBlock(1, 1, 1, DDBlocks.TRANSPORT_DUCT_BASIC.get());
            helper.assertBlockPresent(DDBlocks.TRANSPORT_DUCT_BASIC.get(), 1, 1, 1);
            helper.assertTrue(
                helper.getBlockEntity(BlockPos.ZERO.offset(1, 1, 1), TransportDuctBlockEntity.class) != null,
                "Expected TransportDuctBlockEntity");
            helper.succeed();
        });

        // Linking Transport Duct
        test(event, "transport_duct_linking", helper -> {
            helper.setBlock(1, 1, 1, DDBlocks.TRANSPORT_DUCT_LINKING.get());
            helper.assertBlockPresent(DDBlocks.TRANSPORT_DUCT_LINKING.get(), 1, 1, 1);
            helper.succeed();
        });

        // Structural Duct
        test(event, "structural_duct", helper -> {
            helper.setBlock(1, 1, 1, DDBlocks.STRUCTURAL_DUCT.get());
            helper.assertBlockPresent(DDBlocks.STRUCTURAL_DUCT.get(), 1, 1, 1);
            helper.assertTrue(
                helper.getBlockEntity(BlockPos.ZERO.offset(1, 1, 1), StructuralDuctBlockEntity.class) != null,
                "Expected StructuralDuctBlockEntity");
            helper.succeed();
        });
    }

    private static void test(RegisterGameTestsEvent event, String name, Consumer<GameTestHelper> fn) {
        var testData = new TestData<>(ENV, STRUCTURE, 100, 0, true);
        event.registerTest(
            Identifier.fromNamespaceAndPath(DynamiDucts.MODID, name),
            new LambdaGameTestInstance(testData, fn));
    }
}
