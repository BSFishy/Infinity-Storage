package infinitystorage.apiimpl.network;

import infinitystorage.InfinityConfig;
import infinitystorage.InfinityStorage;
import infinitystorage.api.network.IChannelReloadThread;
import infinitystorage.api.network.INetworkMaster;
import infinitystorage.tile.TileCable;
import infinitystorage.tile.TileNetworkTransmitter;
import infinitystorage.tile.TileNode;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

import java.nio.channels.Channel;
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
    private boolean output;
    ChannelReloadThread crt;

    public ChannelReloadThread(AChannelData channelData, boolean output){
        this(channelData);
        this.output = output;
    }

    public ChannelReloadThread(AChannelData channelData) {
        this.channelData = channelData;
        this.worldObj = channelData.worldObj;
        this.output = true;
    }

    @Override
    public void run() {
        running = true;

        recursiveRun(cables);

        running = false;
        if(output) {
            TextComponentString c = new TextComponentString(TextFormatting.GREEN + I18n.format("misc.infinitystorage:network_tool.success"));
            player.addChatComponentMessage(c);
        }
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
                }else if (tile instanceof TileNetworkTransmitter) {
                    ignore.add(tile);
                    if (channelsUsed <= InfinityStorage.maxChannels) {
                        channelsUsed++;
                        connected.add((TileNode) tile);
                        ((TileNode) tile).onConnected(network);
                        if(((TileNetworkTransmitter) tile).canTransmit())
                            connectWirelessReciever(((TileNetworkTransmitter) tile).getReceiver());
                    }else{
                        if(connected.contains(tile))
                            connected.remove(tile);
                        ((TileNode) tile).onDisconnected(network);
                    }
                }else if (tile instanceof TileNode) {
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
            TextComponentString c = new TextComponentString(TextFormatting.RED + I18n.format("misc.infinitystorage:network_tool.error"));
            player.addChatComponentMessage(c);
            return;
        }

        if(newCables.size() > 0) {
            recursiveRun(newCables);
        }else{
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

    private void connectWirelessReciever(BlockPos pos) {
        if(crt == null){
             crt = new ChannelReloadThread(channelData, false);
        }

        if (crt.isAlive()) {
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
            try {
                crt.join();
            } catch (InterruptedException e) {
                FMLLog.warning("The reload thread was interrupted.");
                TextComponentString c = new TextComponentString(TextFormatting.RED + I18n.format("misc.infinitystorage:network_tool.error"));
                player.addChatComponentMessage(c);
                return;
            }
            ignore.addAll(crt.ignore);
        }
    }
}
