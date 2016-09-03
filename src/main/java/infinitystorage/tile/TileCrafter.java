package infinitystorage.tile;

import infinitystorage.InfinityStorageBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

public class TileCrafter extends TileNode implements ICraftingPatternContainer {

    public TileCrafter(){
        super();
        setupClientNode(new ItemStack(InfinityStorageBlocks.CRAFTER), InfinityStorage.INSTANCE.crafterUsage);
    }

    private ItemHandlerBasic patterns = new ItemHandlerBasic(9, this, stack -> stack.getItem() instanceof ICraftingPatternProvider) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (network != null) {
                network.rebuildPatterns();
            }
        }
    };

    private ItemHandlerUpgrade upgrades = new ItemHandlerUpgrade(4, this, ItemUpgrade.TYPE_SPEED);

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
    public void updateNode() {
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

        readItems(patterns, 0, tag);
        readItems(upgrades, 1, tag);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

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
    public IItemHandler getPatterns() {
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
