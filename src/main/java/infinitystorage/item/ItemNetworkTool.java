package infinitystorage.item;

import infinitystorage.InfinityStorage;
import infinitystorage.InfinityStorageBlocks;
import infinitystorage.tile.TileCable;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public class ItemNetworkTool extends ItemBase {
    public ItemNetworkTool() {
        super("network_tool");

        //setRegistryName(InfinityStorage.ID, "network_tool");
        setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("misc.infinitystorage:network_tool.tooltip"));
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        Block block = world.getBlockState(pos).getBlock();

        if(block == InfinityStorageBlocks.CABLE){
            TileCable tileCable = (TileCable) world.getTileEntity(pos);

            tileCable.getNetwork().reloadCables();

            TextComponentString c = new TextComponentString(TextFormatting.GREEN + "Successfully reloaded all of the channels");
            player.addChatComponentMessage(c);

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }
}
