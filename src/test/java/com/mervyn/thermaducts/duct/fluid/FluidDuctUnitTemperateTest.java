package com.mervyn.thermaducts.duct.fluid;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.mervyn.thermaducts.DuctUnitTestBase;
import com.mervyn.thermaducts.core.duct.DuctToken;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FluidDuctUnitTemperateTest extends DuctUnitTestBase {

  private static final int TEST_CAPACITY = 1000;
  private static final int TEST_THROUGHPUT = 100;

  private FluidDuctUnitTemperate unit;
  private FluidGrid grid;

  private static FluidStack mockFluid(int amount) {
    FluidStack stack = mock(FluidStack.class);
    when(stack.getAmount()).thenReturn(amount);
    when(stack.isEmpty()).thenReturn(false);
    when(stack.isComponentsPatchEmpty()).thenReturn(true);
    when(stack.getComponentsPatch()).thenReturn(DataComponentPatch.EMPTY);
    when(stack.getFluid()).thenReturn(Fluids.WATER);
    return stack;
  }

  @BeforeEach
  void setUp() {
    when(level.getRandom()).thenReturn(mock(RandomSource.class));
    unit = new FluidDuctUnitTemperate(parent, TEST_CAPACITY, TEST_THROUGHPUT, true);
    grid = new FluidGrid(level, TEST_CAPACITY, TEST_THROUGHPUT);
    unit.setGrid(grid);
  }

  @Test
  void constructor_createsUnit() {
    assertNotNull(unit);
    assertSame(parent, unit.getParent());
  }

  @Test
  void getToken_returnsFluid() {
    assertEquals(DuctToken.FLUID, unit.getToken());
  }

  @Test
  void getPathWeight_returnsOne() {
    assertEquals(1, unit.getPathWeight());
  }

  @Test
  void initialTemperature_isRoomTemperature() {
    assertFalse(unit.isOverheating());
    assertFalse(unit.isFreezing());
  }

  @Test
  void isOverheating_returnsFalseByDefault() {
    assertFalse(unit.isOverheating());
  }

  @Test
  void isFreezing_returnsFalseByDefault() {
    assertFalse(unit.isFreezing());
  }

  @Test
  void tickTemperature_clientSide_doesNothing() {
    when(parent.getLevel()).thenReturn(mock(Level.class));
    when(parent.getLevel().isClientSide()).thenReturn(true);
    unit.tickTemperature();
    assertFalse(unit.isOverheating());
  }

  @Test
  void tickTemperature_nullLevel_doesNothing() {
    when(parent.getLevel()).thenReturn(null);
    unit.tickTemperature();
    assertFalse(unit.isOverheating());
  }

  @Test
  void tickTemperature_noGrid_doesNothing() {
    unit.setGrid(null);
    unit.tickTemperature();
    assertFalse(unit.isOverheating());
  }

  @Test
  void tickTemperature_noFluid_lerpsTowardRoomTemp() {
    when(parent.getLevel()).thenReturn(level);
    assertTrue(grid.getTank().isEmpty());

    unit.tickTemperature();

    assertFalse(unit.isOverheating());
  }

  @Test
  void saveAdditional_savesTemperature() {
    CompoundTag tag = new CompoundTag();
    unit.saveAdditional(tag, provider);
    assertTrue(tag.contains("Temperature"));
  }

  @Test
  void loadAdditional_loadsTemperature() {
    CompoundTag tag = new CompoundTag();
    tag.putFloat("Temperature", 900.0f);
    unit.loadAdditional(tag, provider);
    assertTrue(unit.isOverheating());
  }

  @Test
  void loadAdditional_defaultIsRoomTemp() {
    CompoundTag tag = new CompoundTag();
    unit.loadAdditional(tag, provider);
    assertFalse(unit.isOverheating());
    assertFalse(unit.isFreezing());
  }

  @Test
  void saveAdditional_inheritsSuperData() {
    FluidStack fluid = mockFluid(250);
    when(fluid.isEmpty()).thenReturn(true);
    unit.setFluidForGrid(fluid);
    CompoundTag tag = new CompoundTag();
    unit.saveAdditional(tag, provider);
    assertTrue(tag.contains("Temperature"));
  }

  @Test
  void spawnSmokeParticles_notOverheating_doesNothing() {
    when(parent.getLevel()).thenReturn(level);
    when(level.isClientSide()).thenReturn(true);
    ServerLevel serverLevel = mock(ServerLevel.class);
    when(parent.getLevel()).thenReturn(serverLevel);
    when(serverLevel.isClientSide()).thenReturn(true);

    unit.spawnSmokeParticles(serverLevel);

    verify(serverLevel, never()).sendParticles(any(), anyDouble(), anyDouble(), anyDouble(), anyInt(), anyDouble(), anyDouble(), anyDouble(), anyDouble());
  }

  @Test
  void spawnSmokeParticles_overheating_sendsParticles() {
    when(parent.getLevel()).thenReturn(level);
    when(parent.getBlockPos()).thenReturn(BlockPos.ZERO);

    CompoundTag tag = new CompoundTag();
    tag.putFloat("Temperature", 900.0f);
    unit.loadAdditional(tag, provider);

    assertTrue(unit.isOverheating());

    unit.spawnSmokeParticles(level);
    verify(level).sendParticles(
        eq(ParticleTypes.SMOKE),
        anyDouble(),
        anyDouble(),
        anyDouble(),
        eq(1),
        anyDouble(),
        anyDouble(),
        anyDouble(),
        anyDouble());
  }
}
