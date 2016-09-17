package infinitystorage.api.network;

import infinitystorage.tile.TileCable;
import infinitystorage.tile.TileNode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
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
     * @param network The network to run all the commands on
     * @param player The player to send the message to
     */
    void setup(List<TileCable> cables, List<TileEntity> ignore, EntityPlayer player, INetworkMaster network);

    /**
     * Sets up all of the settings for the loop. This will not send the player a message, even if it is set to true
     * @param cables The cables to loop through
     * @param ignore The cables/nodes to ignore
     * @param network The network to run all the commands on
     */
    void setup(List<TileCable> cables, List<TileEntity> ignore, INetworkMaster network);

    /**
     * Gets the number of channels used
     * @return The number of channels used
     */
    int getChannelsUsed();

    /**
     * Reloads all of the channels at the specific location
     * @param pos The position to start at
     */
    void reloadAtPosition(BlockPos pos);

    /**
     * Gets all of the information needed, and runs the {@link #setup} command
     * @param pos The pos to setup at
     * @param network The network to setup
     */
    void setupAtPosition(BlockPos pos, INetworkMaster network);

    /**
     * Takes a node, and connects it or disconnects it, depending on the channels remaining
     * @param tile The node to process
     * @param add If the method should add the node to different lists
     */
    @Nullable
    TileNode processNode(TileEntity tile, boolean add);
}
