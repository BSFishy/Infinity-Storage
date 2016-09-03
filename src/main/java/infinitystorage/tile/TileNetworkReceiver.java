package infinitystorage.tile;

import infinitystorage.InfinityStorage;
import infinitystorage.tile.config.RedstoneMode;

public class TileNetworkReceiver extends TileNode {
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
