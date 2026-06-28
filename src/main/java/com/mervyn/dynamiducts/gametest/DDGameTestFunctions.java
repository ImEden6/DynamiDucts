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
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DDGameTestFunctions {
  public static final DeferredRegister<Consumer<GameTestHelper>> TEST_FUNCTIONS =
      DeferredRegister.create(Registries.TEST_FUNCTION, DynamiDucts.MODID);

  private static Consumer<GameTestHelper> ductTest(Block block) {
    return helper -> {
      helper.setBlock(1, 1, 1, block);
      helper.assertBlockPresent(block, 1, 1, 1);
      helper.succeed();
    };
  }

  private static <T extends BlockEntity> Consumer<GameTestHelper> ductTestWithEntity(
      Block block, Class<T> entityClass, String failMsg) {
    return helper -> {
      helper.setBlock(1, 1, 1, block);
      helper.assertBlockPresent(block, 1, 1, 1);
      helper.assertTrue(helper.getBlockEntity(new BlockPos(1, 1, 1), entityClass) != null, failMsg);
      helper.succeed();
    };
  }

  static {
    TEST_FUNCTIONS.register(
        "energy_duct_basic",
        () ->
            ductTestWithEntity(
                DDBlocks.ENERGY_DUCT_BASIC.get(),
                EnergyDuctBlockEntity.class,
                "Expected EnergyDuctBlockEntity"));
    TEST_FUNCTIONS.register(
        "energy_duct_hardened", () -> ductTest(DDBlocks.ENERGY_DUCT_HARDENED.get()));
    TEST_FUNCTIONS.register(
        "energy_duct_superconductor", () -> ductTest(DDBlocks.ENERGY_DUCT_SUPERCONDUCTOR.get()));
    TEST_FUNCTIONS.register(
        "fluid_duct_basic",
        () ->
            ductTestWithEntity(
                DDBlocks.FLUID_DUCT_BASIC.get(),
                FluidDuctBlockEntity.class,
                "Expected FluidDuctBlockEntity"));
    TEST_FUNCTIONS.register("fluid_duct_energy", () -> ductTest(DDBlocks.FLUID_DUCT_ENERGY.get()));
    TEST_FUNCTIONS.register(
        "fluid_duct_energy_opaque", () -> ductTest(DDBlocks.FLUID_DUCT_ENERGY_OPAQUE.get()));
    TEST_FUNCTIONS.register(
        "fluid_duct_hardened", () -> ductTest(DDBlocks.FLUID_DUCT_HARDENED.get()));
    TEST_FUNCTIONS.register(
        "item_duct_basic",
        () ->
            ductTestWithEntity(
                DDBlocks.ITEM_DUCT_BASIC.get(),
                ItemDuctBlockEntity.class,
                "Expected ItemDuctBlockEntity"));
    TEST_FUNCTIONS.register("item_duct_dense", () -> ductTest(DDBlocks.ITEM_DUCT_DENSE.get()));
    TEST_FUNCTIONS.register("item_duct_energy", () -> ductTest(DDBlocks.ITEM_DUCT_ENERGY.get()));
    TEST_FUNCTIONS.register(
        "transport_duct_basic",
        () ->
            ductTestWithEntity(
                DDBlocks.TRANSPORT_DUCT_BASIC.get(),
                TransportDuctBlockEntity.class,
                "Expected TransportDuctBlockEntity"));
    TEST_FUNCTIONS.register(
        "transport_duct_linking", () -> ductTest(DDBlocks.TRANSPORT_DUCT_LINKING.get()));
    TEST_FUNCTIONS.register(
        "structural_duct",
        () ->
            ductTestWithEntity(
                DDBlocks.STRUCTURAL_DUCT.get(),
                StructuralDuctBlockEntity.class,
                "Expected StructuralDuctBlockEntity"));
  }
}
