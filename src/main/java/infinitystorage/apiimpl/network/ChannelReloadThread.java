package infinitystorage.apiimpl.network;

import infinitystorage.InfinityConfig;
import infinitystorage.InfinityStorage;
import infinitystorage.api.network.IChannelReloadThread;
import infinitystorage.api.network.INetworkMaster;
import infinitystorage.api.network.INetworkNode;
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

    private List<TileCable> cables;
    private List<TileEntity> ignore;
    public List<TileNode> connected = new ArrayList<>();
    private World worldObj;
    public int channelsUsed;
    private EntityPlayer player;
    public boolean running = false;
    private INetworkMaster network;
    private boolean output;
    ChannelReloadThread crt;

    public ChannelReloadThread(World world, boolean output){
        this(world);
        this.output = output;
    }

    public ChannelReloadThread(World world) {
        this.worldObj = world;
        this.output = true;
    }

    @Override
    public void run() {
        running = true;

        recursiveRun(cables);

        running = false;
        if(output && player != null) {
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

            adjacentBlocks.stream().filter(tile -> !ignore.contains(tile)).forEach(tile -> processNode(tile, true));
        }
        long sleepTime = 1000 / InfinityStorage.channelTimeUpdate;
        if(InfinityStorage.channelWaitTimeEnabled) {
            try {
                sleep(sleepTime);
            } catch (InterruptedException e) {
                FMLLog.warning("The reload thread was interrupted.");
                TextComponentString c = new TextComponentString(TextFormatting.RED + I18n.format("misc.infinitystorage:network_tool.error"));
                player.addChatComponentMessage(c);
                return;
            }
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
    public void setup(List<TileCable> cables, List<TileEntity> ignore, INetworkMaster network) {
        this.cables = cables;
        this.ignore = ignore;
        this.network = network;
    }

    @Override
    public int getChannelsUsed() {
        return channelsUsed;
    }

    @Override
    public void reloadAtPosition(BlockPos pos) {
        if(crt == null){
             crt = new ChannelReloadThread(worldObj, false);
        }

        if (!crt.isAlive()) {
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
                TileNode node = processNode(tile);
                if(node instanceof TileCable){
                    outputCables.add((TileCable) node);
                }
            }
            ignore.addAll(adjacentBlocks);
            crt.setup(outputCables, ignore, player, network);
            crt.channelsUsed = channelsUsed;
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

    @Override
    public void setupAtPosition(BlockPos pos, INetworkMaster network) {
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
            TileNode node = processNode(tile);
            if(node instanceof TileCable){
                outputCables.add((TileCable) node);
            }
        }
        ignore.addAll(adjacentBlocks);
        setup(outputCables, ignore, network);
    }

    public TileNode processNode(TileEntity tile){
        return processNode(tile, false);
    }

    @Override
    public TileNode processNode(TileEntity tile, boolean add) {
        if (tile instanceof TileCable) {
            if(add)
                ignore.add(tile);
            //newCables.add((TileCable) tile);
            return (TileCable) tile;
        }else if (tile instanceof TileNetworkTransmitter) {
            ignore.add(tile);
            if (channelsUsed <= InfinityStorage.maxChannels) {
                channelsUsed++;
                if(add)
                    connected.add((TileNode) tile);
                ((TileNode) tile).onConnected(network);
                if(((TileNetworkTransmitter) tile).canTransmit()) {
                    //reloadAtPosition(((TileNetworkTransmitter) tile).getReceiver());
                    crt = new ChannelReloadThread(worldObj, false);
                    crt.setupAtPosition(((TileNetworkTransmitter) tile).getReceiver(), network);
                    crt.start();
                }
            }else{
                if(((TileNetworkTransmitter) tile).canTransmit())
                    reloadAtPosition(((TileNetworkTransmitter) tile).getReceiver());
                if(add) {
                    if (connected.contains(tile))
                        connected.remove(tile);
                }
                ((TileNode) tile).onDisconnected(network);
            }
            return (TileNetworkTransmitter) tile;
        }else if (tile instanceof TileNode) {
            if(add)
                ignore.add(tile);
            if (channelsUsed <= InfinityStorage.maxChannels) {
                channelsUsed++;
                if(add)
                    connected.add((TileNode) tile);
                ((TileNode) tile).onConnected(network);
            } else {
                if(add) {
                    if (connected.contains(tile))
                        connected.remove(tile);
                }
                ((TileNode) tile).onDisconnected(network);
            }
            return (TileNode) tile;
        }
        return null;
    }
}
