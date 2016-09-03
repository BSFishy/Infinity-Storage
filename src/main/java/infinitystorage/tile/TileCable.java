package infinitystorage.tile;

import infinitystorage.InfinityStorage;

public class TileCable extends TileMultipartNode {
    @Override
    public int getEnergyUsage() {
        return InfinityStorage.INSTANCE.cableUsage;
    }

    @Override
    public void updateNode() {
        // NO OP
    }
}
