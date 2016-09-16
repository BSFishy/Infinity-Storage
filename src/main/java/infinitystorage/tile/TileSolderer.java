package infinitystorage.tile;

import infinitystorage.InfinityStorageBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import infinitystorage.InfinityStorage;
import infinitystorage.api.InfinityStorageAPI;
import infinitystorage.api.network.INetworkMaster;
import infinitystorage.api.solderer.ISoldererRecipe;
import infinitystorage.api.storage.CompareUtils;
import infinitystorage.inventory.ItemHandlerBasic;
import infinitystorage.inventory.ItemHandlerUpgrade;
import infinitystorage.item.ItemUpgrade;
import infinitystorage.tile.data.ITileDataProducer;
import infinitystorage.tile.data.TileDataParameter;

import java.util.HashSet;
import java.util.Set;

public class TileSolderer extends TileNode {
    public static final TileDataParameter<Integer> DURATION = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileSolderer>() {
        @Override
        public Integer getValue(TileSolderer tile) {
            return tile.recipe != null ? tile.recipe.getDuration() : 0;
        }
    });

    public static final TileDataParameter<Integer> PROGRESS = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileSolderer>() {
        @Override
        public Integer getValue(TileSolderer tile) {
            return tile.progress;
        }
    });

    private static final String NBT_WORKING = "Working";
    private static final String NBT_PROGRESS = "Progress";

    private ItemHandlerBasic items = new ItemHandlerBasic(3, this){
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
            Set<Integer> possibleSlots = new HashSet<>();

            for(ISoldererRecipe recipe : InfinityStorageAPI.instance().getSoldererRegistry().getRecipes()){
                for(int i = 0; i < 3; ++i){
                    if(CompareUtils.compareStackNoQuantity(recipe.getRow(i), stack) || CompareUtils.compareStackOreDict(recipe.getRow(i), stack)){
                        possibleSlots.add(i);
                    }
                }
            }

            return possibleSlots.contains(slot) ? super.insertItem(slot, stack, simulate) : stack;
        }
    };
    private ItemHandlerBasic result = new ItemHandlerBasic(1, this){
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
            return stack;
        }
    };
    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_SPEED);

    private ISoldererRecipe recipe;

    private boolean working = false;
    private int progress = 0;

    public TileSolderer() {
        dataManager.addWatchedParameter(DURATION);
        dataManager.addWatchedParameter(PROGRESS);
        setupClientNode(new ItemStack(InfinityStorageBlocks.SOLDERER), InfinityStorage.INSTANCE.soldererUsage);
    }

    @Override
    public int getEnergyUsage() {
        return InfinityStorage.INSTANCE.soldererUsage + upgrades.getEnergyUsage();
    }

    @Override
    public void updateNode() {
        boolean wasWorking = working;

        if (items.getStackInSlot(1) == null && items.getStackInSlot(2) == null && result.getStackInSlot(0) == null) {
            stop();
        } else {
            ISoldererRecipe newRecipe = InfinityStorageAPI.instance().getSoldererRegistry().getRecipe(items);

            if (newRecipe == null) {
                stop();
            } else if (newRecipe != recipe) {
                boolean sameItem = result.getStackInSlot(0) != null ? CompareUtils.compareStackNoQuantity(result.getStackInSlot(0), newRecipe.getResult()) : false;

                if (result.getStackInSlot(0) == null || (sameItem && ((result.getStackInSlot(0).stackSize + newRecipe.getResult().stackSize) <= result.getStackInSlot(0).getMaxStackSize()))) {
                    recipe = newRecipe;
                    progress = 0;
                    working = true;

                    markDirty();
                }
            } else if (working) {
                progress += 1 + upgrades.getUpgradeCount(ItemUpgrade.TYPE_SPEED);

                if (progress >= recipe.getDuration()) {
                    if (result.getStackInSlot(0) != null) {
                        result.getStackInSlot(0).stackSize += recipe.getResult().stackSize;
                    } else {
                        result.setStackInSlot(0, recipe.getResult().copy());
                    }

                    for (int i = 0; i < 3; ++i) {
                        if (recipe.getRow(i) != null) {
                            items.extractItem(i, recipe.getRow(i).stackSize, false);
                        }
                    }

                    recipe = null;
                    progress = 0;
                    // Don't set working to false yet, wait till the next update because we may have another stack waiting.

                    markDirty();
                }
            }
        }

        if (wasWorking != working) {
            updateBlock();
        }
    }

    @Override
    public void onConnectionChange(INetworkMaster network, boolean state) {
        super.onConnectionChange(network, state);

        if (!state) {
            stop();
        }
    }

    public void stop() {
        progress = 0;
        working = false;
        recipe = null;

        markDirty();
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        readItems(items, 0, tag);
        readItems(upgrades, 1, tag);
        readItems(result, 2, tag);

        recipe = InfinityStorageAPI.instance().getSoldererRegistry().getRecipe(items);

        if (tag.hasKey(NBT_WORKING)) {
            working = tag.getBoolean(NBT_WORKING);
        }

        if (tag.hasKey(NBT_PROGRESS)) {
            progress = tag.getInteger(NBT_PROGRESS);
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        writeItems(items, 0, tag);
        writeItems(upgrades, 1, tag);
        writeItems(result, 2, tag);

        tag.setBoolean(NBT_WORKING, working);
        tag.setInteger(NBT_PROGRESS, progress);

        return tag;
    }

    @Override
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        tag.setBoolean(NBT_WORKING, working);

        return tag;
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        working = tag.getBoolean(NBT_WORKING);

        super.readUpdate(tag);
    }

    public boolean isWorking() {
        return working;
    }

    public ItemHandlerBasic getItems() {
        return items;
    }

    public ItemHandlerBasic getResult() {
        return result;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(items, upgrades);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return facing == EnumFacing.DOWN ? (T) result : (T) items;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}
