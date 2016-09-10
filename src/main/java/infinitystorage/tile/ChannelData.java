package infinitystorage.tile;

import infinitystorage.InfinityConfig;
import infinitystorage.InfinityStorage;
import infinitystorage.api.network.INetworkMaster;
import infinitystorage.apiimpl.network.AChannelData;
import infinitystorage.apiimpl.network.ChannelReloadThread;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLLog;

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
                return channelsUsedLocal < InfinityStorage.maxChannels;
            }
            channelsUsedLocal += ((channelsUsedLocal < InfinityStorage.maxChannels) ? ((channelsUsedLocal + cable.getNodesInArea().size() >= InfinityStorage.maxChannels) ? InfinityStorage.maxChannels - channelsUsedLocal : cable.getNodesInArea().size()) : 0);
        }
        return false;
    }

    @Override
    public void reloadCables(EntityPlayer player) {
        if(!crt.running) {
            channelsUsed = 0;

            BlockPos pos = network.getPosition();
            List<TileEntity> adjacentBlocks = new ArrayList<>();
            List<TileCable> outputCables = new ArrayList<>();
            List<TileEntity> ignore = new ArrayList<>();
            adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())));
            adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())));
            adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())));
            adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())));
            adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)));
            adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1)));
            for (TileEntity tile : adjacentBlocks) {
                if (tile instanceof TileCable) {
                    ((TileCable) tile).setCableNumber(1);
                    outputCables.add((TileCable) tile);
                } else if (tile instanceof TileNode) {
                    channelsUsed++;
                    ignore.add(tile);
                }
            }

            ignore.addAll(adjacentBlocks);
            crt.setup(outputCables, ignore, player, network);
            crt.start();
        }
    }

    @Override
    public void updateChannels(int pipe) {
        for(int pipenum = pipe; pipenum < cables.size(); pipenum++){
            List<TileNode> machinesAround = cables.get(pipenum).getNodesInArea();
            if(machinesAround != null && channelsUsed() < InfinityStorage.maxChannels) {
                machinesAround.stream().filter(node -> channelsUsed() < InfinityStorage.maxChannels).forEach(node -> changeChannelNumber(1, true));
            }
        }
    }
}
