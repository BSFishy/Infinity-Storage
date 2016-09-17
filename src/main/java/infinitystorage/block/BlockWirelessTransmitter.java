package infinitystorage.block;

import infinitystorage.apiimpl.network.ChannelReloadThread;
import infinitystorage.tile.TileNetworkTransmitter;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import infinitystorage.InfinityStorage;
import infinitystorage.InfinityStorageGui;
import infinitystorage.tile.TileCable;
import infinitystorage.tile.TileWirelessTransmitter;

public class BlockWirelessTransmitter extends BlockNode {
    // From BlockTorch
    private static final AxisAlignedBB WIRELESS_TRANSMITTER_AABB = new AxisAlignedBB(0.4000000059604645D, 0.0D, 0.4000000059604645D, 0.6000000238418579D, 0.6000000238418579D, 0.6000000238418579D);

    public BlockWirelessTransmitter() {
        super("wireless_transmitter");
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileWirelessTransmitter();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(InfinityStorage.INSTANCE, InfinityStorageGui.WIRELESS_TRANSMITTER, world, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {
        if (!canPlaceBlockAt(world, pos) && world.getBlockState(pos).getBlock() == this) {
            dropBlockAsItem(world, pos, state, 0);

            world.setBlockToAir(pos);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return WIRELESS_TRANSMITTER_AABB;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return world.getTileEntity(pos.offset(EnumFacing.DOWN)) instanceof TileCable;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }

    @Override
    public EnumPlacementType getPlacementType() {
        return null;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileNetworkTransmitter thisTile = (TileNetworkTransmitter) world.getTileEntity(pos);
        ChannelReloadThread crt = new ChannelReloadThread(world, false);
        crt.setupAtPosition(thisTile.getReceiver(), thisTile.getNetwork());
        crt.start();
        super.breakBlock(world, pos, state);
    }
}
