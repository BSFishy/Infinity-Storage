package infinitystorage.apiimpl.network;

import infinitystorage.InfinityConfig;
import infinitystorage.InfinityStorage;
import infinitystorage.api.network.IChannelReloadThread;
import infinitystorage.api.network.INetworkMaster;
import infinitystorage.tile.TileCable;
import infinitystorage.tile.TileNode;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

import java.util.ArrayList;
import java.util.List;

public class ChannelReloadThread extends Thread implements IChannelReloadThread {

    private AChannelData channelData;
    private List<TileCable> cables;
    private List<TileEntity> ignore;
    public List<TileNode> connected = new ArrayList<>();
    private World worldObj;
    private int channelsUsed;
    private EntityPlayer player;
    public boolean running = false;
    private INetworkMaster network;

    public ChannelReloadThread(AChannelData channelData) {
        this.channelData = channelData;
        this.worldObj = channelData.worldObj;
    }

    @Override
    public void run() {
        running = true;

        recursiveRun(cables);

        running = false;
        TextComponentString c = new TextComponentString(TextFormatting.GREEN + I18n.format("misc.infinitystorage:network_tool.success"));
        player.addChatComponentMessage(c);
    }

    private void recursiveRun(List<TileCable> oldCables) {
        List<TileCable> newCables = new ArrayList<>();
        for (TileCable cable : oldCables) {
            BlockPos pos = cable.getPos();
            List<TileEntity> adjacentBlocks = new ArrayList<>();

            adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())));
            adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())));
            adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())));
            adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())));
            adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)));
            adjacentBlocks.add(worldObj.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1)));

            adjacentBlocks.stream().filter(tile -> !ignore.contains(tile)).forEach(tile -> {
                if (tile instanceof TileCable) {
                    ((TileCable) tile).setCableNumber(cable.cableNumber);
                    newCables.add((TileCable) tile);
                    ignore.add(tile);
                } else if (tile instanceof TileNode) {
                    ignore.add(tile);
                    if (channelsUsed <= InfinityStorage.maxChannels) {
                        channelsUsed++;
                        connected.add((TileNode) tile);
                        ((TileNode) tile).onConnected(network);
                    }else{
                        if(connected.contains(tile))
                            connected.remove(tile);
                        ((TileNode) tile).onDisconnected(network);
                    }
                }
            });
        }
        channelData.channelsUsed = channelsUsed;
        long sleepTime = 1000 / InfinityStorage.channelTimeUpdate;
        try {
            sleep(sleepTime);
        } catch (InterruptedException e) {
            FMLLog.warning("The reload thread was interrupted.");
            return;
        }

        if(newCables.size() > 0) {
            FMLLog.info("recursiveRun being called");
            recursiveRun(newCables);
        }else{
            FMLLog.info("returning");
            return;
        }
    }

    @Override
    public void setup(List<TileCable> cables, List<TileEntity> ignore, EntityPlayer player, INetworkMaster network) {
        this.cables = cables;
        this.ignore = ignore;
        this.player = player;
        this.network = network;
    }

    @Override
    public int getChannelsUsed() {
        return channelsUsed;
    }
}
