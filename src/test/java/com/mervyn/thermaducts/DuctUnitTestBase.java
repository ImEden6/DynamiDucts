package com.mervyn.thermaducts;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mervyn.thermaducts.blockentity.DuctBlockEntity;
import com.mervyn.thermaducts.core.network.ConnectionType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.junit.jupiter.api.BeforeEach;

public abstract class DuctUnitTestBase {

  protected DuctBlockEntity parent;
  protected ServerLevel level;
  protected HolderLookup.Provider provider;
  protected BlockPos pos = BlockPos.ZERO;

  @BeforeEach
  void setUpBase() {
    parent = mock(DuctBlockEntity.class);
    level = mock(ServerLevel.class);
    provider = mock(HolderLookup.Provider.class);

    when(parent.getLevel()).thenReturn(level);
    when(parent.getBlockPos()).thenReturn(pos);
    for (Direction dir : Direction.values()) {
      when(parent.getConnectionType(dir)).thenReturn(ConnectionType.NORMAL);
    }
  }

  protected CompoundTag saveAndReload(Runnable save, Runnable load) {
    CompoundTag tag = new CompoundTag();
    save.run();
    load.run();
    return tag;
  }
}
