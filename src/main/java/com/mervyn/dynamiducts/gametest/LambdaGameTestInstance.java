package com.mervyn.dynamiducts.gametest;

import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

class LambdaGameTestInstance extends GameTestInstance {
    private final Consumer<GameTestHelper> function;

    LambdaGameTestInstance(TestData<Holder<TestEnvironmentDefinition<?>>> testData, Consumer<GameTestHelper> function) {
        super(testData);
        this.function = function;
    }

    @Override
    public void run(GameTestHelper helper) {
        function.accept(helper);
    }

    @Override
    public MapCodec<? extends GameTestInstance> codec() {
        return FunctionGameTestInstance.CODEC;
    }

    @Override
    protected MutableComponent typeDescription() {
        return Component.translatable("test_instance.type.function");
    }
}
