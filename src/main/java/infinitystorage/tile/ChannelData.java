package infinitystorage.tile;

import infinitystorage.InfinityConfig;
import infinitystorage.InfinityStorage;
import infinitystorage.api.network.INetworkMaster;
import infinitystorage.apiimpl.network.AChannelData;
import infinitystorage.apiimpl.network.ChannelReloadThread;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLLog;

import java.util.ArrayList;
import java.util.List;

public class ChannelData extends AChannelData {

    private ChannelReloadThread crt;

    public ChannelData(INetworkMaster network) {
        super(network);
    }

    @Override
    public void reloadCables(EntityPlayer player) {
        try {
            crt = new ChannelReloadThread(worldObj);

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

            channelsUsed = crt.getChannelsUsed();
        } catch (IllegalThreadStateException e) {
            FMLLog.warning("There was an error running the thread.");
            e.printStackTrace();
            TextComponentString c = new TextComponentString(TextFormatting.RED + I18n.format("misc.infinitystorage:network_tool.error"));
            player.addChatComponentMessage(c);
        }
    }
}
