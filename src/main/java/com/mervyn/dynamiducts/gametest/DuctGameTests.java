package com.mervyn.dynamiducts.gametest;

import com.mervyn.dynamiducts.DynamiDucts;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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
        test(event, "energy_duct_basic");
        test(event, "energy_duct_hardened");
        test(event, "energy_duct_superconductor");
        test(event, "fluid_duct_basic");
        test(event, "fluid_duct_energy");
        test(event, "fluid_duct_energy_opaque");
        test(event, "fluid_duct_hardened");
        test(event, "item_duct_basic");
        test(event, "item_duct_dense");
        test(event, "item_duct_energy");
        test(event, "transport_duct_basic");
        test(event, "transport_duct_linking");
        test(event, "structural_duct");
    }

    private static void test(RegisterGameTestsEvent event, String name) {
        var key = ResourceKey.create(Registries.TEST_FUNCTION, Identifier.fromNamespaceAndPath(DynamiDucts.MODID, name));
        var testData = new TestData<>(ENV, STRUCTURE, 100, 0, true);
        event.registerTest(
            Identifier.fromNamespaceAndPath(DynamiDucts.MODID, name),
            new FunctionGameTestInstance(key, testData));
    }
}
