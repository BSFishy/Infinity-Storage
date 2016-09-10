package infinitystorage.item;

import infinitystorage.InfinityStorage;
import infinitystorage.InfinityStorageBlocks;
import infinitystorage.tile.TileCable;
import infinitystorage.tile.TileController;
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
import net.minecraftforge.fml.common.FMLLog;

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

        if(block == InfinityStorageBlocks.CONTROLLER) {
            TileController tileCable = (TileController) world.getTileEntity(pos);
            if (tileCable != null) {

                //assert tileCable != null;
                tileCable.reloadCables(player);

                return EnumActionResult.SUCCESS;
            }else{
                TextComponentString e = new TextComponentString(TextFormatting.RED + I18n.format("misc.infinitystorage:network_tool.error"));
                player.addChatComponentMessage(e);
            }
        }

        return EnumActionResult.PASS;
    }
}
