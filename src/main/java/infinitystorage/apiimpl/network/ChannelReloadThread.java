package infinitystorage.apiimpl.network;

import infinitystorage.InfinityConfig;
import infinitystorage.api.network.IChannelReloadThread;
import infinitystorage.tile.TileCable;
import infinitystorage.tile.TileNode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ChannelReloadThread extends Thread implements IChannelReloadThread {

    private AChannelData channelData;
    private List<TileCable> cables;
    private List<TileEntity> ignore;
    private World worldObj;
    private int channelsUsed;

    public ChannelReloadThread(AChannelData channelData){
        this.channelData = channelData;
        this.worldObj = channelData.worldObj;
    }

    @Override
    public void run(){
        List<TileCable> newCables = new ArrayList<>();
        List<TileCable> oldCables = cables;
        while(!oldCables.isEmpty()) {
            for (TileCable cable : oldCables) {
                BlockPos pos = cable.getPos();
                List<TileEntity> adjacentBlocks = new ArrayList<>();

                adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())));
                adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())));
                adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())));
                adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())));
                adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)));
                adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1)));

                for (TileEntity tile : adjacentBlocks) {
                    if (!ignore.contains(tile)) {
                        if (tile instanceof TileCable) {
                            ((TileCable) tile).setCableNumber(cable.cableNumber);
                            newCables.add((TileCable) tile);
                            ignore.add(tile);
                        } else if (tile instanceof TileNode) {
                            channelsUsed++;
                            ignore.add(tile);
                        }
                    }
                }
            }
            oldCables = newCables;
            newCables = new ArrayList<>();
            channelData.channelsUsed = channelsUsed;
            long sleepTime = 1000 / InfinityConfig.channelTimeUpdate;
            try {
                sleep(sleepTime);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    @Override
    public void setup(List<TileCable> cables, List<TileEntity> ignore) {
        this.cables = cables;
        this.ignore = ignore;
    }

    @Override
    public int getChannelsUsed() {
        return channelsUsed;
    }
}
