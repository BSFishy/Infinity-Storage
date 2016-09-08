package infinitystorage.tile;

import infinitystorage.InfinityConfig;
import infinitystorage.api.network.INetworkMaster;
import infinitystorage.apiimpl.network.AChannelData;
import infinitystorage.apiimpl.network.ChannelReloadThread;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class ChannelData extends AChannelData {

    private ChannelReloadThread crt = new ChannelReloadThread(this);

    public ChannelData(INetworkMaster network) {
        super(network);
    }

    @Override
    public boolean shouldConnect(int pipe) {
        int channelsUsedLocal = 0;
        for(int pipenum = 0; pipenum < cables.size(); pipenum++){
            TileCable cable = cables.get(pipenum);
            if(pipenum == pipe){
                return channelsUsedLocal < InfinityConfig.maxChannels;
            }
            channelsUsedLocal += ((channelsUsedLocal < InfinityConfig.maxChannels) ? ((channelsUsedLocal + cable.getNodesInArea().size() >= InfinityConfig.maxChannels) ? InfinityConfig.maxChannels - channelsUsedLocal : cable.getNodesInArea().size()) : 0);
        }
        return false;
    }

    @Override
    public void reloadCables() {
        channelsUsed = 5;

        BlockPos pos = network.getPosition();
        List<TileEntity> adjacentBlocks = new ArrayList<>();
        List<TileCable> outputCables = new ArrayList<>();
        adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())));
        adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())));
        adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())));
        adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())));
        adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)));
        adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1)));
        for(TileEntity tile : adjacentBlocks){
            if(tile instanceof TileCable){
                ((TileCable) tile).setCableNumber(1);
                outputCables.add((TileCable) tile);
            }else if(tile instanceof TileNode){
                channelsUsed++;
            }
        }

        crt.setup(outputCables, adjacentBlocks);
    }

    @Override
    public void updateChannels(int pipe) {
        for(int pipenum = pipe; pipenum < cables.size(); pipenum++){
            List<TileNode> machinesAround = cables.get(pipenum).getNodesInArea();
            if(machinesAround != null && channelsUsed() < InfinityConfig.maxChannels) {
                machinesAround.stream().filter(node -> channelsUsed() < InfinityConfig.maxChannels).forEach(node -> changeChannelNumber(1, true));
            }
        }
    }
}
