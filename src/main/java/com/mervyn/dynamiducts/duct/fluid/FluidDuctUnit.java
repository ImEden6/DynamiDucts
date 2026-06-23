package com.mervyn.dynamiducts.duct.fluid;

import com.mervyn.dynamiducts.blockentity.DuctBlockEntity;
import com.mervyn.dynamiducts.core.duct.DuctToken;
import com.mervyn.dynamiducts.core.duct.DuctUnit;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class FluidDuctUnit extends DuctUnit<FluidDuctUnit, FluidGrid, ResourceHandler<FluidResource>> {

  private final int capacityPerDuct;
  private final int throughputPerDuct;
  private final boolean transparent;
  private FluidStack fluidForGrid = FluidStack.EMPTY;
  private FluidStack renderFluid = FluidStack.EMPTY;

  public FluidDuctUnit(
      DuctBlockEntity parent, int capacityPerDuct, int throughputPerDuct, boolean transparent) {
    super(parent);
    this.capacityPerDuct = capacityPerDuct;
    this.throughputPerDuct = throughputPerDuct;
    this.transparent = transparent;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected ResourceHandler<FluidResource>[] createTileCacheArray() {
    return new ResourceHandler[6];
  }

  @Override
  protected FluidDuctUnit[] createDuctCacheArray() {
    return new FluidDuctUnit[6];
  }

  @Override
  public DuctToken getToken() {
    return DuctToken.FLUID;
  }

  @Override
  public FluidGrid createGrid(ServerLevel level) {
    return new FluidGrid(level, capacityPerDuct, throughputPerDuct);
  }

  @Override
  public int getPathWeight() {
    return 1;
  }

  @Override
  public ResourceHandler<FluidResource> cacheTile(Direction side) {
    if (parent.getLevel() == null) return null;
    return parent.getLevel().getCapability(
        Capabilities.Fluid.BLOCK, parent.getBlockPos().relative(side), side.getOpposite());
  }

  public ResourceHandler<FluidResource> createCapability(Direction side) {
    return new ResourceHandler<>() {
      @Override
      public int size() {
        return grid != null ? grid.getTank().size() : 0;
      }

      @Override
      public FluidResource getResource(int index) {
        return grid != null ? grid.getTank().getResource(index) : FluidResource.EMPTY;
      }

      @Override
      public long getAmountAsLong(int index) {
        return grid != null ? grid.getTank().getAmountAsLong(index) : 0;
      }

      @Override
      public long getCapacityAsLong(int index, FluidResource resource) {
        return grid != null ? grid.getTank().getCapacityAsLong(index, resource) : 0;
      }

      @Override
      public boolean isValid(int index, FluidResource resource) {
        return true;
      }

      @Override
      public int insert(int index, FluidResource resource, int amount, TransactionContext ctx) {
        if (grid == null) return 0;
        int result = grid.getTank().insert(index, resource, amount, ctx);
        if (result > 0) {
          new SnapshotJournal<Void>() {
            @Override
            protected Void createSnapshot() { return null; }
            @Override
            protected void revertToSnapshot(Void snapshot) {}
            @Override
            protected void onRootCommit(Void snapshot) { grid.syncVisualIfChanged(); }
          }.updateSnapshots(ctx);
        }
        return result;
      }

      @Override
      public int extract(int index, FluidResource resource, int amount, TransactionContext ctx) {
        if (grid == null) return 0;
        int result = grid.getTank().extract(index, resource, amount, ctx);
        if (result > 0) {
          new SnapshotJournal<Void>() {
            @Override
            protected Void createSnapshot() { return null; }
            @Override
            protected void revertToSnapshot(Void snapshot) {}
            @Override
            protected void onRootCommit(Void snapshot) { grid.syncVisualIfChanged(); }
          }.updateSnapshots(ctx);
        }
        return result;
      }
    };
  }

  private boolean beingDestroyed;

  public boolean isTransparent() {
    return transparent;
  }

  public void markBeingDestroyed() {
    beingDestroyed = true;
  }

  public boolean isBeingDestroyed() {
    return beingDestroyed;
  }

  public FluidStack getFluidForGrid() {
    return fluidForGrid;
  }

  public void setFluidForGrid(FluidStack fluid) {
    this.fluidForGrid = fluid != null ? fluid : FluidStack.EMPTY;
  }

  public FluidStack getVisualFluid() {
    return renderFluid;
  }

  public int getVisualFluidLevel() {
    return renderFluid.isEmpty() ? 0 : Mth.clamp(renderFluid.getAmount(), 1, 6);
  }

  public void setRenderFluid(FluidStack fluid) {
    renderFluid = fluid == null || fluid.isEmpty() ? FluidStack.EMPTY : fluid.copy();
  }

  @Override
  public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
    FluidStack toSave = FluidStack.EMPTY;
    if (grid != null && !grid.getTank().isEmpty()) {
      toSave = grid.getNodeShare(this);
    } else if (!fluidForGrid.isEmpty()) {
      toSave = fluidForGrid;
    }
    if (!toSave.isEmpty()) {
      var ops = RegistryOps.create(NbtOps.INSTANCE, provider);
      FluidStack.CODEC.encodeStart(ops, toSave).result().ifPresent(t -> tag.put("Fluid", t));
    }
    if (!renderFluid.isEmpty()) {
      var ops = RegistryOps.create(NbtOps.INSTANCE, provider);
      FluidStack.CODEC
          .encodeStart(ops, renderFluid)
          .result()
          .ifPresent(t -> tag.put("RenderFluid", t));
    }
  }

  @Override
  public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
    var ops = RegistryOps.create(NbtOps.INSTANCE, provider);
    if (tag.contains("Fluid")) {
      fluidForGrid =
          FluidStack.CODEC
              .parse(ops, tag.get("Fluid"))
              .resultOrPartial(e -> {})
              .orElse(FluidStack.EMPTY);
    } else {
      fluidForGrid = FluidStack.EMPTY;
    }
    if (tag.contains("RenderFluid")) {
      renderFluid =
          FluidStack.CODEC
              .parse(ops, tag.get("RenderFluid"))
              .resultOrPartial(e -> {})
              .orElse(FluidStack.EMPTY);
    } else {
      renderFluid = FluidStack.EMPTY;
    }
  }
}
