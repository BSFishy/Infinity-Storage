package infinitystorage.tile;

import infinitystorage.InfinityStorageBlocks;
import infinitystorage.api.autocrafting.ICraftingPattern;
import infinitystorage.api.network.NetworkUtils;
import infinitystorage.api.storage.CompareUtils;
import infinitystorage.tile.data.ITileDataConsumer;
import infinitystorage.tile.data.ITileDataProducer;
import infinitystorage.tile.data.TileDataParameter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import infinitystorage.InfinityStorage;
import infinitystorage.api.autocrafting.ICraftingPatternContainer;
import infinitystorage.api.autocrafting.ICraftingPatternProvider;
import infinitystorage.api.network.INetworkMaster;
import infinitystorage.inventory.ItemHandlerBasic;
import infinitystorage.inventory.ItemHandlerUpgrade;
import infinitystorage.item.ItemUpgrade;

import javax.swing.plaf.basic.BasicComboBoxUI;
import java.util.ArrayList;
import java.util.List;

public class TileCrafter extends TileNode implements ICraftingPatternContainer {
    public static final TileDataParameter<Boolean> AUTOCRAFT_SIGNAL = new TileDataParameter<Boolean>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileCrafter>() {
        @Override
        public Boolean getValue(TileCrafter tile) {
            return tile.autocraftSignal;
        }
    }, new ITileDataConsumer<Boolean, TileCrafter>() {
        @Override
        public void setValue(TileCrafter tile, Boolean value) {
            tile.autocraftSignal = value;

            tile.markDirty();
        }
    });

    private static final String NBT_AUTOCRAFT_SIGNAL = "AutocraftSignal";

    private ItemHandlerBasic patterns = new ItemHandlerBasic(9, this, stack -> stack.getItem() instanceof ICraftingPatternProvider) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if(worldObj != null){
                rebuildPatterns();
            }

            if (network != null) {
                network.rebuildPatterns();
            }
        }
    };

    private List<ICraftingPattern> actualPatterns = new ArrayList<>();

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_SPEED);

    private boolean autocraftSignal = false;

    public TileCrafter() {
        dataManager.addWatchedParameter(AUTOCRAFT_SIGNAL);
        setupClientNode(new ItemStack(InfinityStorageBlocks.CRAFTER), InfinityStorage.INSTANCE.crafterUsage);
    }

    private void rebuildPatterns() {
        actualPatterns.clear();

        for(int i = 0; i < patterns.getSlots(); ++i){
            ItemStack patternStack = patterns.getStackInSlot(i);

            if(patternStack != null){
                ICraftingPattern pattern = ((ICraftingPatternProvider) patternStack.getItem()).create(worldObj, patternStack, this);

                if(pattern.isValid()){
                    actualPatterns.add(pattern);
                }
            }
        }
    }

    @Override
    public int getEnergyUsage() {
        int usage = InfinityStorage.INSTANCE.crafterUsage + upgrades.getEnergyUsage();

        for (int i = 0; i < patterns.getSlots(); ++i) {
            if (patterns.getStackInSlot(i) != null) {
                usage += InfinityStorage.INSTANCE.crafterPerPatternUsage;
            }
        }

        return usage;
    }

    @Override
    public void update(){
        if(ticks == 0){
            rebuildPatterns();
        }

        super.update();
    }

    @Override
    public void updateNode() {
        if(autocraftSignal && worldObj.isBlockPowered(pos)){
            for(ICraftingPattern pattern : actualPatterns){
                for(ItemStack output : pattern.getOutputs()){
                    NetworkUtils.scheduleCraftingTaskIfUnscheduled(network, output, 1, CompareUtils.COMPARE_DAMAGE | CompareUtils.COMPARE_NBT);
                }
            }
        }
    }

    @Override
    public void onConnectionChange(INetworkMaster network, boolean state) {
        if (!state) {
            network.getCraftingTasks().stream()
                .filter(task -> task.getPattern().getContainer().getPosition().equals(pos))
                .forEach(network::cancelCraftingTask);
        }

        network.rebuildPatterns();
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        if(tag.hasKey(NBT_AUTOCRAFT_SIGNAL)){
            autocraftSignal = tag.getBoolean(NBT_AUTOCRAFT_SIGNAL);
        }

        readItems(patterns, 0, tag);
        readItems(upgrades, 1, tag);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setBoolean(NBT_AUTOCRAFT_SIGNAL, autocraftSignal);

        writeItems(patterns, 0, tag);
        writeItems(upgrades, 1, tag);

        return tag;
    }

    @Override
    public int getSpeed() {
        return 20 - (upgrades.getUpgradeCount(ItemUpgrade.TYPE_SPEED) * 4);
    }

    @Override
    public IItemHandler getConnectedItems() {
        return getItemHandler(getFacingTile(), getDirection().getOpposite());
    }

    @Override
    public List<ICraftingPattern> getPatterns() {
        return actualPatterns;
    }

    public IItemHandler getPatternItems(){
        return patterns;
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return new CombinedInvWrapper(patterns, upgrades);
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != getDirection()) {
            return (T) patterns;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != getDirection()) || super.hasCapability(capability, facing);
    }
}
