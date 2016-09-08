package infinitystorage.tile;

import infinitystorage.InfinityConfig;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import infinitystorage.api.network.INetworkMaster;
import infinitystorage.api.network.INetworkNode;
import infinitystorage.api.network.NetworkUtils;
import infinitystorage.tile.config.IRedstoneConfigurable;
import infinitystorage.tile.config.RedstoneMode;
import infinitystorage.tile.data.TileDataParameter;
import net.minecraftforge.fml.common.FMLLog;

public abstract class TileNode extends TileBase implements INetworkNode, IRedstoneConfigurable {
    public static final TileDataParameter<Integer> REDSTONE_MODE = RedstoneMode.createParameter();

    private static final String NBT_CONNECTED = "Connected";

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    private boolean active;
    private boolean update;

    protected boolean connected;
    protected INetworkMaster network;

    protected boolean rebuildOnUpdateChange;

    public ClientNode clientNode;

    public Block block;

    public TileNode() {
        dataManager.addWatchedParameter(REDSTONE_MODE);
    }

    @Override
    public boolean canUpdate() {
        return redstoneMode.isEnabled(worldObj, pos);
    }

    public boolean isActive() {
        return isConnected() && canUpdate();
    }

    @Override
    public void update() {
        if (!worldObj.isRemote) {
            if (update != canUpdate() && network != null) {
                update = canUpdate();

                onConnectionChange(network, update);

                if (rebuildOnUpdateChange) {
                    NetworkUtils.rebuildGraph(network);
                }
            }

            if (active != isActive() && hasConnectivityState()) {
                updateBlock();

                active = isActive();
            }

            if (isActive()) {
                updateNode();
            }
        }

        super.update();
    }

    @Override
    public void onConnected(INetworkMaster network) {
        /*if(this instanceof TileCable){
            network.reloadCables();
        }else {
            if (InfinityConfig.channelsEnabled) {
            } else {
                this.connected = true;
                this.network = network;

                onConnectionChange(network, true);
            }
        }*/
        // DISABLED FOR NOW
    }

    @Override
    public void onDisconnected(INetworkMaster network) {
        /*if(InfinityConfig.channelsEnabled && !(this instanceof TileCable)){
            if(network.connected(this)){
                changeConnections(network, false);
                onConnectionChange(network, false);
                network.connectQueue();

                this.connected = false;
                this.network = null;
            }
        }else {
            onConnectionChange(network, false);

            this.connected = false;
            this.network = null;
        }*/
        // DISABLED FOR NOW
    }

    @Override
    public void onConnectionChange(INetworkMaster network, boolean state) {
        // NO OP
    }

    private void changeConnections(INetworkMaster network, boolean state) {
        if(state) {
            network.addConnections(1);
            network.addConnection(this);
        }else {
            network.removeConnections(1);
            network.removeConnection(this);
        }
    }

    @Override
    public boolean canConduct(EnumFacing direction) {
        return true;
    }

    @Override
    public INetworkMaster getNetwork() {
        return network;
    }

    @Override
    public World getNodeWorld() {
        return worldObj;
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        this.redstoneMode = mode;

        markDirty();
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        if (tag.hasKey(RedstoneMode.NBT)) {
            redstoneMode = RedstoneMode.getById(tag.getInteger(RedstoneMode.NBT));
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setInteger(RedstoneMode.NBT, redstoneMode.ordinal());

        return tag;
    }

    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        if (hasConnectivityState()) {
            tag.setBoolean(NBT_CONNECTED, isActive());
        }

        return tag;
    }

    public void readUpdate(NBTTagCompound tag) {
        if (hasConnectivityState()) {
            connected = tag.getBoolean(NBT_CONNECTED);
        }

        super.readUpdate(tag);
    }

    public boolean hasConnectivityState() {
        return false;
    }

    public void setupClientNode(ItemStack i, int e){
        clientNode = new ClientNode(i, 1, e);
    }

    public void setupClientNode(ItemStack i, int a, int e){
        clientNode = new ClientNode(i, a, e);
    }

    public Block getBlock(){
        if(block == null)
            block = worldObj.getBlockState(pos).getBlock();
        return block;
    }
}
