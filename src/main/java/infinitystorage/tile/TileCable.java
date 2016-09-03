package infinitystorage.tile;

import infinitystorage.InfinityStorage;
import infinitystorage.InfinityStorageBlocks;
import infinitystorage.InfinityStorageItems;
import net.minecraft.item.ItemStack;

public class TileCable extends TileMultipartNode {

    public TileCable(){
        super();
        setupClientNode(new ItemStack(InfinityStorageBlocks.CABLE), InfinityStorage.INSTANCE.cableUsage);
    }

    @Override
    public int getEnergyUsage() {
        return InfinityStorage.INSTANCE.cableUsage;
    }

    @Override
    public void updateNode() {
        // NO OP
    }
}
