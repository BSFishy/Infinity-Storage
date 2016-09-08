package infinitystorage.api.network;

import infinitystorage.tile.TileCable;
import infinitystorage.tile.TileNode;
import net.minecraft.tileentity.TileEntity;

import java.util.List;

/**
 * Represents the channel reloading thread
 */
public interface IChannelReloadThread {

    /**
     * Begins the loop of checking the channels
     */
    void start();

    /**
     * Sets up all of the settings for the loop
     * @param cables The cables to loop through
     * @param ignore The cables/nodes to ignore
     */
    void setup(List<TileCable> cables, List<TileEntity> ignore);

    /**
     * Gets the number of channels used
     * @return The number of channels used
     */
    int getChannelsUsed();
}
