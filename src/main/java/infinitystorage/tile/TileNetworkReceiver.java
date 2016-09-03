package infinitystorage.tile;

import infinitystorage.InfinityStorage;
import infinitystorage.InfinityStorageBlocks;
import infinitystorage.tile.config.RedstoneMode;
import net.minecraft.item.ItemStack;

public class TileNetworkReceiver extends TileNode {

    public TileNetworkReceiver(){
        super();
        setupClientNode(new ItemStack(InfinityStorageBlocks.NETWORK_RECEIVER), InfinityStorage.INSTANCE.networkReceiverUsage);
    }

    @Override
    public void updateNode() {
    }

    @Override
    public int getEnergyUsage() {
        return InfinityStorage.INSTANCE.networkReceiverUsage;
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        // NO OP
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}
