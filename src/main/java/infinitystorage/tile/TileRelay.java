package infinitystorage.tile;

import infinitystorage.InfinityStorageBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import infinitystorage.InfinityStorage;
import infinitystorage.tile.config.RedstoneMode;

public class TileRelay extends TileNode {
    public TileRelay() {
        setRedstoneMode(RedstoneMode.LOW);

        rebuildOnUpdateChange = true;
        setupClientNode(new ItemStack(InfinityStorageBlocks.RELAY), InfinityStorage.INSTANCE.relayUsage);
    }

    @Override
    public int getEnergyUsage() {
        return getRedstoneMode() == RedstoneMode.IGNORE ? 0 : InfinityStorage.INSTANCE.relayUsage;
    }

    @Override
    public void updateNode() {
    }

    @Override
    public boolean canConduct(EnumFacing direction) {
        return canUpdate();
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}
