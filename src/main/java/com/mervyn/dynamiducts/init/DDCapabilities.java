package com.mervyn.dynamiducts.init;

import com.mervyn.dynamiducts.DynamiDucts;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

@EventBusSubscriber(modid = DynamiDucts.MODID)
@SuppressWarnings("removal")
public class DDCapabilities {

  @SubscribeEvent
  public static void registerCapabilities(RegisterCapabilitiesEvent event) {
    registerEnergyCaps(event);
    registerFluidCaps(event);
    registerItemCaps(event);
  }

  private static void registerEnergyCaps(RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(
        Capabilities.Energy.BLOCK,
        DDBlockEntities.ENERGY_DUCT_BASIC.get(),
        (be, dir) -> wrapEnergy(be.getEnergyCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Energy.BLOCK,
        DDBlockEntities.ENERGY_DUCT_HARDENED.get(),
        (be, dir) -> wrapEnergy(be.getEnergyCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Energy.BLOCK,
        DDBlockEntities.ENERGY_DUCT_REINFORCED.get(),
        (be, dir) -> wrapEnergy(be.getEnergyCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Energy.BLOCK,
        DDBlockEntities.ENERGY_DUCT_SIGNALUM.get(),
        (be, dir) -> wrapEnergy(be.getEnergyCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Energy.BLOCK,
        DDBlockEntities.ENERGY_DUCT_RESONANT.get(),
        (be, dir) -> wrapEnergy(be.getEnergyCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Energy.BLOCK,
        DDBlockEntities.ENERGY_DUCT_SUPERCONDUCTOR.get(),
        (be, dir) -> wrapEnergy(be.getEnergyCapability(dir)));
  }

  private static void registerFluidCaps(RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(
        Capabilities.Fluid.BLOCK,
        DDBlockEntities.FLUID_DUCT_BASIC.get(),
        (be, dir) -> wrapFluid(be.getFluidCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Fluid.BLOCK,
        DDBlockEntities.FLUID_DUCT_BASIC_OPAQUE.get(),
        (be, dir) -> wrapFluid(be.getFluidCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Fluid.BLOCK,
        DDBlockEntities.FLUID_DUCT_HARDENED.get(),
        (be, dir) -> wrapFluid(be.getFluidCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Fluid.BLOCK,
        DDBlockEntities.FLUID_DUCT_HARDENED_OPAQUE.get(),
        (be, dir) -> wrapFluid(be.getFluidCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Fluid.BLOCK,
        DDBlockEntities.FLUID_DUCT_ENERGY.get(),
        (be, dir) -> wrapFluid(be.getFluidCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Fluid.BLOCK,
        DDBlockEntities.FLUID_DUCT_ENERGY_OPAQUE.get(),
        (be, dir) -> wrapFluid(be.getFluidCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Fluid.BLOCK,
        DDBlockEntities.FLUID_DUCT_SUPER.get(),
        (be, dir) -> wrapFluid(be.getFluidCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Fluid.BLOCK,
        DDBlockEntities.FLUID_DUCT_SUPER_OPAQUE.get(),
        (be, dir) -> wrapFluid(be.getFluidCapability(dir)));

    event.registerBlockEntity(
        Capabilities.Energy.BLOCK,
        DDBlockEntities.FLUID_DUCT_ENERGY.get(),
        (be, dir) -> wrapEnergy(be.getEnergyCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Energy.BLOCK,
        DDBlockEntities.FLUID_DUCT_ENERGY_OPAQUE.get(),
        (be, dir) -> wrapEnergy(be.getEnergyCapability(dir)));
  }

  private static void registerItemCaps(RegisterCapabilitiesEvent event) {
    event.registerBlockEntity(
        Capabilities.Item.BLOCK,
        DDBlockEntities.ITEM_DUCT_BASIC.get(),
        (be, dir) -> wrapItem(be.getItemCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Item.BLOCK,
        DDBlockEntities.ITEM_DUCT_BASIC_OPAQUE.get(),
        (be, dir) -> wrapItem(be.getItemCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Item.BLOCK,
        DDBlockEntities.ITEM_DUCT_DENSE.get(),
        (be, dir) -> wrapItem(be.getItemCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Item.BLOCK,
        DDBlockEntities.ITEM_DUCT_DENSE_OPAQUE.get(),
        (be, dir) -> wrapItem(be.getItemCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Item.BLOCK,
        DDBlockEntities.ITEM_DUCT_VACUUM.get(),
        (be, dir) -> wrapItem(be.getItemCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Item.BLOCK,
        DDBlockEntities.ITEM_DUCT_VACUUM_OPAQUE.get(),
        (be, dir) -> wrapItem(be.getItemCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Item.BLOCK,
        DDBlockEntities.ITEM_DUCT_FAST.get(),
        (be, dir) -> wrapItem(be.getItemCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Item.BLOCK,
        DDBlockEntities.ITEM_DUCT_FAST_OPAQUE.get(),
        (be, dir) -> wrapItem(be.getItemCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Item.BLOCK,
        DDBlockEntities.ITEM_DUCT_ENERGY.get(),
        (be, dir) -> wrapItem(be.getItemCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Item.BLOCK,
        DDBlockEntities.ITEM_DUCT_ENERGY_OPAQUE.get(),
        (be, dir) -> wrapItem(be.getItemCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Item.BLOCK,
        DDBlockEntities.ITEM_DUCT_ENERGY_FAST.get(),
        (be, dir) -> wrapItem(be.getItemCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Item.BLOCK,
        DDBlockEntities.ITEM_DUCT_ENERGY_FAST_OPAQUE.get(),
        (be, dir) -> wrapItem(be.getItemCapability(dir)));

    event.registerBlockEntity(
        Capabilities.Energy.BLOCK,
        DDBlockEntities.ITEM_DUCT_ENERGY.get(),
        (be, dir) -> wrapEnergy(be.getEnergyCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Energy.BLOCK,
        DDBlockEntities.ITEM_DUCT_ENERGY_OPAQUE.get(),
        (be, dir) -> wrapEnergy(be.getEnergyCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Energy.BLOCK,
        DDBlockEntities.ITEM_DUCT_ENERGY_FAST.get(),
        (be, dir) -> wrapEnergy(be.getEnergyCapability(dir)));
    event.registerBlockEntity(
        Capabilities.Energy.BLOCK,
        DDBlockEntities.ITEM_DUCT_ENERGY_FAST_OPAQUE.get(),
        (be, dir) -> wrapEnergy(be.getEnergyCapability(dir)));
  }

  private static EnergyHandler wrapEnergy(IEnergyStorage energy) {
    if (energy == null) return null;
    return new EnergyHandler() {
      @Override
      public int insert(int amount, TransactionContext transaction) {
        int accepted = energy.receiveEnergy(amount, true);
        if (accepted > 0) {
          EnergyInsertJournal journal = new EnergyInsertJournal(energy, accepted);
          journal.updateSnapshots(transaction);
          energy.receiveEnergy(accepted, false);
          return accepted;
        }
        return 0;
      }

      @Override
      public int extract(int amount, TransactionContext transaction) {
        int extracted = energy.extractEnergy(amount, true);
        if (extracted > 0) {
          EnergyExtractJournal journal = new EnergyExtractJournal(energy, extracted);
          journal.updateSnapshots(transaction);
          energy.extractEnergy(extracted, false);
          return extracted;
        }
        return 0;
      }

      @Override
      public long getAmountAsLong() {
        return energy.getEnergyStored();
      }

      @Override
      public long getCapacityAsLong() {
        return energy.getMaxEnergyStored();
      }
    };
  }

  private static ResourceHandler<FluidResource> wrapFluid(IFluidHandler fluid) {
    if (fluid == null) return null;
    return new ResourceHandler<>() {
      @Override
      public int size() {
        return fluid.getTanks();
      }

      @Override
      public FluidResource getResource(int index) {
        return FluidResource.of(fluid.getFluidInTank(index));
      }

      @Override
      public long getAmountAsLong(int index) {
        return fluid.getFluidInTank(index).getAmount();
      }

      @Override
      public long getCapacityAsLong(int index, FluidResource resource) {
        return fluid.getTankCapacity(index);
      }

      @Override
      public boolean isValid(int index, FluidResource resource) {
        return true;
      }

      @Override
      public int insert(
          int index, FluidResource resource, int amount, TransactionContext transaction) {
        if (resource.isEmpty()) return 0;
        FluidStack stack = resource.toStack(amount);
        int accepted = fluid.fill(stack, IFluidHandler.FluidAction.SIMULATE);
        if (accepted > 0) {
          FluidInsertJournal journal = new FluidInsertJournal(fluid, resource.toStack(accepted));
          journal.updateSnapshots(transaction);
          fluid.fill(resource.toStack(accepted), IFluidHandler.FluidAction.EXECUTE);
          return accepted;
        }
        return 0;
      }

      @Override
      public int extract(
          int index, FluidResource resource, int amount, TransactionContext transaction) {
        FluidStack current = fluid.getFluidInTank(index);
        if (current.isEmpty() || !resource.matches(current)) return 0;
        FluidStack drainedSim =
            fluid.drain(resource.toStack(amount), IFluidHandler.FluidAction.SIMULATE);
        int drained = drainedSim.getAmount();
        if (drained > 0) {
          FluidExtractJournal journal = new FluidExtractJournal(fluid, resource.toStack(drained));
          journal.updateSnapshots(transaction);
          fluid.drain(resource.toStack(drained), IFluidHandler.FluidAction.EXECUTE);
          return drained;
        }
        return 0;
      }
    };
  }

  private static ResourceHandler<ItemResource> wrapItem(IItemHandler item) {
    if (item == null) return null;
    return new ResourceHandler<>() {
      @Override
      public int size() {
        return item.getSlots();
      }

      @Override
      public ItemResource getResource(int index) {
        return ItemResource.of(item.getStackInSlot(index));
      }

      @Override
      public long getAmountAsLong(int index) {
        return item.getStackInSlot(index).getCount();
      }

      @Override
      public long getCapacityAsLong(int index, ItemResource resource) {
        return item.getSlotLimit(index);
      }

      @Override
      public boolean isValid(int index, ItemResource resource) {
        return true;
      }

      @Override
      public int insert(
          int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (resource.isEmpty()) return 0;
        net.minecraft.world.item.ItemStack stack = resource.toStack(amount);
        net.minecraft.world.item.ItemStack remainder = item.insertItem(index, stack, true);
        int accepted = amount - remainder.getCount();
        if (accepted > 0) {
          ItemInsertJournal journal = new ItemInsertJournal(item, index, resource, accepted);
          journal.updateSnapshots(transaction);
          item.insertItem(index, resource.toStack(accepted), false);
          return accepted;
        }
        return 0;
      }

      @Override
      public int extract(
          int index, ItemResource resource, int amount, TransactionContext transaction) {
        net.minecraft.world.item.ItemStack current = item.getStackInSlot(index);
        if (current.isEmpty() || !resource.matches(current)) return 0;
        net.minecraft.world.item.ItemStack simulated = item.extractItem(index, amount, true);
        int extracted = simulated.getCount();
        if (extracted > 0) {
          ItemExtractJournal journal = new ItemExtractJournal(item, index, resource, extracted);
          journal.updateSnapshots(transaction);
          item.extractItem(index, extracted, false);
          return extracted;
        }
        return 0;
      }
    };
  }

  private static class EnergyInsertJournal extends SnapshotJournal<Boolean> {
    private final IEnergyStorage energy;
    private final int amount;
    private boolean active = true;

    public EnergyInsertJournal(IEnergyStorage energy, int amount) {
      this.energy = energy;
      this.amount = amount;
    }

    @Override
    protected Boolean createSnapshot() {
      return active;
    }

    @Override
    protected void revertToSnapshot(Boolean snapshot) {
      if (snapshot) {
        energy.extractEnergy(amount, false);
      }
    }
  }

  private static class EnergyExtractJournal extends SnapshotJournal<Boolean> {
    private final IEnergyStorage energy;
    private final int amount;
    private boolean active = true;

    public EnergyExtractJournal(IEnergyStorage energy, int amount) {
      this.energy = energy;
      this.amount = amount;
    }

    @Override
    protected Boolean createSnapshot() {
      return active;
    }

    @Override
    protected void revertToSnapshot(Boolean snapshot) {
      if (snapshot) {
        energy.receiveEnergy(amount, false);
      }
    }
  }

  private static class FluidInsertJournal extends SnapshotJournal<Boolean> {
    private final IFluidHandler fluid;
    private final FluidStack stack;
    private boolean active = true;

    public FluidInsertJournal(IFluidHandler fluid, FluidStack stack) {
      this.fluid = fluid;
      this.stack = stack;
    }

    @Override
    protected Boolean createSnapshot() {
      return active;
    }

    @Override
    protected void revertToSnapshot(Boolean snapshot) {
      if (snapshot) {
        fluid.drain(stack, IFluidHandler.FluidAction.EXECUTE);
      }
    }
  }

  private static class FluidExtractJournal extends SnapshotJournal<Boolean> {
    private final IFluidHandler fluid;
    private final FluidStack stack;
    private boolean active = true;

    public FluidExtractJournal(IFluidHandler fluid, FluidStack stack) {
      this.fluid = fluid;
      this.stack = stack;
    }

    @Override
    protected Boolean createSnapshot() {
      return active;
    }

    @Override
    protected void revertToSnapshot(Boolean snapshot) {
      if (snapshot) {
        fluid.fill(stack, IFluidHandler.FluidAction.EXECUTE);
      }
    }
  }

  private static class ItemInsertJournal extends SnapshotJournal<Boolean> {
    private final IItemHandler item;
    private final int index;
    private final int amount;
    private boolean active = true;

    public ItemInsertJournal(IItemHandler item, int index, ItemResource resource, int amount) {
      this.item = item;
      this.index = index;
      this.amount = amount;
    }

    @Override
    protected Boolean createSnapshot() {
      return active;
    }

    @Override
    protected void revertToSnapshot(Boolean snapshot) {
      if (snapshot) {
        item.extractItem(index, amount, false);
      }
    }
  }

  private static class ItemExtractJournal extends SnapshotJournal<Boolean> {
    private final IItemHandler item;
    private final int index;
    private final ItemResource resource;
    private final int amount;
    private boolean active = true;

    public ItemExtractJournal(IItemHandler item, int index, ItemResource resource, int amount) {
      this.item = item;
      this.index = index;
      this.resource = resource;
      this.amount = amount;
    }

    @Override
    protected Boolean createSnapshot() {
      return active;
    }

    @Override
    protected void revertToSnapshot(Boolean snapshot) {
      if (snapshot) {
        item.insertItem(index, resource.toStack(amount), false);
      }
    }
  }
}
