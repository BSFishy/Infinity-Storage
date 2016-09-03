package infinitystorage.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import infinitystorage.InfinityStorage;
import infinitystorage.InfinityStorageItems;
import infinitystorage.api.network.INetworkMaster;
import infinitystorage.api.storage.fluid.IFluidStorage;
import infinitystorage.api.storage.fluid.IFluidStorageProvider;
import infinitystorage.api.storage.item.IItemStorage;
import infinitystorage.api.storage.item.IItemStorageProvider;
import infinitystorage.apiimpl.storage.fluid.FluidStorageNBT;
import infinitystorage.apiimpl.storage.fluid.FluidUtils;
import infinitystorage.apiimpl.storage.item.ItemStorageNBT;
import infinitystorage.block.EnumFluidStorageType;
import infinitystorage.block.EnumItemStorageType;
import infinitystorage.inventory.ItemHandlerBasic;
import infinitystorage.inventory.ItemHandlerFluid;
import infinitystorage.inventory.ItemValidatorBasic;
import infinitystorage.tile.config.IComparable;
import infinitystorage.tile.config.IFilterable;
import infinitystorage.tile.config.IPrioritizable;
import infinitystorage.tile.config.IType;
import infinitystorage.tile.data.TileDataParameter;

import java.util.List;

public class TileDiskDrive extends TileNode implements IItemStorageProvider, IFluidStorageProvider, IStorageGui, IComparable, IFilterable, IPrioritizable, IType {
    public static final TileDataParameter<Integer> PRIORITY = IPrioritizable.createParameter();
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> MODE = IFilterable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();

    public class ItemStorage extends ItemStorageNBT {
        public ItemStorage(ItemStack disk) {
            super(disk.getTagCompound(), EnumItemStorageType.getById(disk.getItemDamage()).getCapacity(), TileDiskDrive.this);
        }

        @Override
        public int getPriority() {
            return priority;
        }

        @Override
        public ItemStack insertItem(ItemStack stack, int size, boolean simulate) {
            if (!IFilterable.canTake(itemFilters, mode, getCompare(), stack)) {
                return ItemHandlerHelper.copyStackWithSize(stack, size);
            }

            return super.insertItem(stack, size, simulate);
        }
    }

    public class FluidStorage extends FluidStorageNBT {
        public FluidStorage(ItemStack disk) {
            super(disk.getTagCompound(), EnumFluidStorageType.getById(disk.getItemDamage()).getCapacity(), TileDiskDrive.this);
        }

        @Override
        public int getPriority() {
            return priority;
        }

        @Override
        public FluidStack insertFluid(FluidStack stack, int size, boolean simulate) {
            if (!IFilterable.canTakeFluids(fluidFilters, mode, getCompare(), stack)) {
                return FluidUtils.copyStackWithSize(stack, size);
            }

            return super.insertFluid(stack, size, simulate);
        }
    }

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_STORED = "Stored";
    private static final String NBT_TYPE = "Type";

    private ItemHandlerBasic disks = new ItemHandlerBasic(8, this, new ItemValidatorBasic(InfinityStorageItems.STORAGE_DISK) {
        @Override
        public boolean isValid(ItemStack disk) {
            return super.isValid(disk) && ItemStorageNBT.isValid(disk);
        }
    }, new ItemValidatorBasic(InfinityStorageItems.FLUID_STORAGE_DISK) {
        @Override
        public boolean isValid(ItemStack disk) {
            return super.isValid(disk) && FluidStorageNBT.isValid(disk);
        }
    }) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                ItemStack disk = getStackInSlot(slot);

                if (disk == null) {
                    itemStorages[slot] = null;
                    fluidStorages[slot] = null;
                } else {
                    if (disk.getItem() == InfinityStorageItems.STORAGE_DISK) {
                        itemStorages[slot] = new ItemStorage(disk);
                    } else if (disk.getItem() == InfinityStorageItems.FLUID_STORAGE_DISK) {
                        fluidStorages[slot] = new FluidStorage(disk);
                    }
                }

                if (network != null) {
                    network.getItemStorage().rebuild();
                    network.getFluidStorage().rebuild();
                }
            }
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (itemStorages[slot] != null) {
                itemStorages[slot].writeToNBT();
            }

            if (fluidStorages[slot] != null) {
                fluidStorages[slot].writeToNBT();
            }


            return super.extractItem(slot, amount, simulate);
        }
    };

    private ItemHandlerBasic itemFilters = new ItemHandlerBasic(9, this);
    private ItemHandlerFluid fluidFilters = new ItemHandlerFluid(9, this);

    private ItemStorage itemStorages[] = new ItemStorage[8];
    private FluidStorage fluidStorages[] = new FluidStorage[8];

    private int priority = 0;
    private int compare = 0;
    private int mode = IFilterable.WHITELIST;
    private int type = IType.ITEMS;

    private int stored = 0;

    public TileDiskDrive() {
        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(TYPE);
    }

    @Override
    public void update() {
        if (!worldObj.isRemote) {
            if (stored != getStoredForDisplay()) {
                stored = getStoredForDisplay();

                updateBlock();
            }
        }

        super.update();
    }

    @Override
    public int getEnergyUsage() {
        int usage = InfinityStorage.INSTANCE.diskDriveUsage;

        for (int i = 0; i < disks.getSlots(); ++i) {
            if (disks.getStackInSlot(i) != null) {
                usage += InfinityStorage.INSTANCE.diskDrivePerDiskUsage;
            }
        }

        return usage;
    }

    @Override
    public void updateNode() {
    }

    public void onBreak() {
        for (ItemStorage storage : this.itemStorages) {
            if (storage != null) {
                storage.writeToNBT();
            }
        }

        for (FluidStorage storage : this.fluidStorages) {
            if (storage != null) {
                storage.writeToNBT();
            }
        }
    }

    @Override
    public void onConnectionChange(INetworkMaster network, boolean state) {
        super.onConnectionChange(network, state);

        network.getItemStorage().rebuild();
        network.getFluidStorage().rebuild();
    }

    @Override
    public void addItemStorages(List<IItemStorage> storages) {
        for (IItemStorage storage : this.itemStorages) {
            if (storage != null) {
                storages.add(storage);
            }
        }
    }

    @Override
    public void addFluidStorages(List<IFluidStorage> storages) {
        for (IFluidStorage storage : this.fluidStorages) {
            if (storage != null) {
                storages.add(storage);
            }
        }
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        readItems(disks, 0, tag);
        readItems(itemFilters, 1, tag);
        readItems(fluidFilters, 2, tag);

        if (tag.hasKey(NBT_PRIORITY)) {
            priority = tag.getInteger(NBT_PRIORITY);
        }

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        if (tag.hasKey(NBT_MODE)) {
            mode = tag.getInteger(NBT_MODE);
        }

        if (tag.hasKey(NBT_TYPE)) {
            type = tag.getInteger(NBT_TYPE);
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        for (int i = 0; i < disks.getSlots(); ++i) {
            if (itemStorages[i] != null) {
                itemStorages[i].writeToNBT();
            }

            if (fluidStorages[i] != null) {
                fluidStorages[i].writeToNBT();
            }
        }

        writeItems(disks, 0, tag);
        writeItems(itemFilters, 1, tag);
        writeItems(fluidFilters, 2, tag);

        tag.setInteger(NBT_PRIORITY, priority);
        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);
        tag.setInteger(NBT_TYPE, type);

        return tag;
    }

    @Override
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        tag.setInteger(NBT_STORED, stored);

        return tag;
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        stored = tag.getInteger(NBT_STORED);

        super.readUpdate(tag);
    }

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public void setCompare(int compare) {
        this.compare = compare;

        markDirty();
    }

    @Override
    public int getMode() {
        return mode;
    }

    @Override
    public void setMode(int mode) {
        this.mode = mode;

        markDirty();
    }

    public int getStoredForDisplay() {
        if (!worldObj.isRemote) {
            float stored = 0;
            float capacity = 0;

            for (int i = 0; i < disks.getSlots(); ++i) {
                ItemStack disk = disks.getStackInSlot(i);

                if (disk != null) {
                    int diskCapacity = disk.getItem() == InfinityStorageItems.STORAGE_DISK ? EnumItemStorageType.getById(disk.getItemDamage()).getCapacity() : EnumFluidStorageType.getById(disk.getItemDamage()).getCapacity();

                    if (diskCapacity == -1) {
                        return 0;
                    }

                    stored += disk.getItem() == InfinityStorageItems.STORAGE_DISK ? ItemStorageNBT.getStoredFromNBT(disk.getTagCompound()) : FluidStorageNBT.getStoredFromNBT(disk.getTagCompound());
                    capacity += diskCapacity;
                }
            }

            if (capacity == 0) {
                return 0;
            }

            return (int) Math.floor((stored / capacity) * 7F);
        }

        return stored;
    }

    @Override
    public String getGuiTitle() {
        return "block.infinitystorage:disk_drive.name";
    }

    @Override
    public TileDataParameter<Integer> getTypeParameter() {
        return TYPE;
    }

    @Override
    public TileDataParameter<Integer> getRedstoneModeParameter() {
        return REDSTONE_MODE;
    }

    @Override
    public TileDataParameter<Integer> getCompareParameter() {
        return COMPARE;
    }

    @Override
    public TileDataParameter<Integer> getFilterParameter() {
        return MODE;
    }

    @Override
    public TileDataParameter<Integer> getPriorityParameter() {
        return PRIORITY;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;

        markDirty();
    }

    @Override
    public int getStored() {
        int stored = 0;

        for (int i = 0; i < disks.getSlots(); ++i) {
            ItemStack disk = disks.getStackInSlot(i);

            if (disk != null) {
                stored += disk.getItem() == InfinityStorageItems.STORAGE_DISK ? ItemStorageNBT.getStoredFromNBT(disk.getTagCompound()) : FluidStorageNBT.getStoredFromNBT(disk.getTagCompound());
            }
        }

        return stored;
    }

    @Override
    public int getCapacity() {
        int capacity = 0;

        for (int i = 0; i < disks.getSlots(); ++i) {
            ItemStack disk = disks.getStackInSlot(i);

            if (disk != null) {
                int diskCapacity = disk.getItem() == InfinityStorageItems.STORAGE_DISK ? EnumItemStorageType.getById(disk.getItemDamage()).getCapacity() : EnumFluidStorageType.getById(disk.getItemDamage()).getCapacity();

                if (diskCapacity == -1) {
                    return -1;
                }

                capacity += diskCapacity;
            }
        }

        return capacity;
    }

    public IItemHandler getDisks() {
        return disks;
    }

    @Override
    public int getType() {
        return worldObj.isRemote ? TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;

        markDirty();
    }

    @Override
    public IItemHandler getFilterInventory() {
        return getType() == IType.ITEMS ? itemFilters : fluidFilters;
    }

    @Override
    public IItemHandler getDrops() {
        return disks;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) disks;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}
