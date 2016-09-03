package infinitystorage.tile;

import infinitystorage.InfinityStorageBlocks;
import mcmultipart.microblock.IMicroblock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import infinitystorage.InfinityStorage;
import infinitystorage.api.network.NetworkUtils;
import infinitystorage.inventory.ItemHandlerBasic;
import infinitystorage.inventory.ItemHandlerFluid;
import infinitystorage.inventory.ItemHandlerUpgrade;
import infinitystorage.item.ItemUpgrade;
import infinitystorage.tile.config.IComparable;
import infinitystorage.tile.config.IType;
import infinitystorage.tile.data.TileDataParameter;

public class TileExporter extends TileMultipartNode implements IComparable, IType {
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_TYPE = "Type";

    private ItemHandlerBasic itemFilters = new ItemHandlerBasic(9, this);
    private ItemHandlerFluid fluidFilters = new ItemHandlerFluid(9, this);

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_SPEED, ItemUpgrade.TYPE_CRAFTING, ItemUpgrade.TYPE_STACK);

    private int compare = 0;
    private int type = IType.ITEMS;

    public TileExporter() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
        setupClientNode(new ItemStack(InfinityStorageBlocks.EXPORTER), InfinityStorage.INSTANCE.exporterUsage);
    }

    @Override
    public boolean canAddMicroblock(IMicroblock microblock) {
        return !isBlockingMicroblock(microblock, getDirection());
    }

    @Override
    public int getEnergyUsage() {
        return InfinityStorage.INSTANCE.exporterUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void updateNode() {
        if (ticks % upgrades.getSpeed() == 0) {
            if (type == IType.ITEMS) {
                IItemHandler handler = getItemHandler(getFacingTile(), getDirection().getOpposite());

                int size = upgrades.hasUpgrade(ItemUpgrade.TYPE_STACK) ? 64 : 1;

                if (handler != null) {
                    for (int i = 0; i < itemFilters.getSlots(); ++i) {
                        ItemStack slot = itemFilters.getStackInSlot(i);

                        if (slot != null) {
                            ItemStack took = network.extractItem(slot, size, compare);

                            if (took != null) {
                                ItemStack remainder = ItemHandlerHelper.insertItem(handler, took, false);

                                if (remainder != null) {
                                    network.insertItem(remainder, remainder.stackSize, false);
                                }
                            } else if (upgrades.hasUpgrade(ItemUpgrade.TYPE_CRAFTING)) {
                                NetworkUtils.scheduleCraftingTaskIfUnscheduled(network, slot, 1, compare);
                            }
                        }
                    }
                }
            } else if (type == IType.FLUIDS) {
                IFluidHandler handler = getFluidHandler(getFacingTile(), getDirection().getOpposite());

                if (handler != null) {
                    for (FluidStack stack : fluidFilters.getFluids()) {
                        if (stack != null) {
                            FluidStack took = network.extractFluid(stack, Fluid.BUCKET_VOLUME, compare);

                            if (took != null) {
                                int remainder = Fluid.BUCKET_VOLUME - handler.fill(took, true);

                                if (remainder > 0) {
                                    network.insertFluid(took, remainder, false);
                                }
                            }
                        }
                    }
                }
            }
        }
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
    public void read(NBTTagCompound tag) {
        super.read(tag);

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        if (tag.hasKey(NBT_TYPE)) {
            type = tag.getInteger(NBT_TYPE);
        }

        readItems(itemFilters, 0, tag);
        readItems(upgrades, 1, tag);
        readItems(fluidFilters, 2, tag);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_TYPE, type);

        writeItems(itemFilters, 0, tag);
        writeItems(upgrades, 1, tag);
        writeItems(fluidFilters, 2, tag);

        return tag;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return upgrades;
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
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) upgrades;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}
