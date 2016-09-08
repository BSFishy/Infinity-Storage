package infinitystorage.apiimpl.network;

import infinitystorage.api.network.IChannelData;
import infinitystorage.api.network.INetworkMaster;
import infinitystorage.tile.TileCable;
import infinitystorage.tile.TileNode;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public abstract class AChannelData implements IChannelData {

    /**
     * A list full of the cables connect to the network
     */
    public Map<Integer, TileCable> cables = new HashMap<>();
    /**
     * The network variable
     */
    public INetworkMaster network;

    public World worldObj;
    public int channelsUsed = 0;

    public AChannelData(INetworkMaster network){
        this.network = network;
        this.worldObj = network.getNetworkWorld();
    }

    @Override
    public void changeChannelNumber(int channels, boolean aod){
        if(aod){
            channelsUsed += channels;
        }else{
            channelsUsed -= channels;
        }
    }

    /**
     * Updates all of the channels, pipes, and machines
     * @param pipe The cable number to start at
     */
    public abstract void updateChannels(int pipe);

    public void updateChannels(){
        updateChannels(1);
    }

    @Override
    public int channelsUsed(){
        return channelsUsed;
    }
}
