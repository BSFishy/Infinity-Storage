package infinitystorage.apiimpl.network;

import infinitystorage.InfinityConfig;
import infinitystorage.InfinityStorage;
import infinitystorage.api.autocrafting.ICraftingPatternContainer;
import infinitystorage.api.storage.fluid.IFluidStorageProvider;
import infinitystorage.api.storage.item.IItemStorageProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import infinitystorage.api.network.INetworkNode;
import infinitystorage.api.network.INetworkNodeGraph;
import infinitystorage.tile.TileController;
import infinitystorage.tile.TileNetworkTransmitter;
import net.minecraftforge.fml.common.FMLLog;

import java.util.*;

public class NetworkNodeGraph implements INetworkNodeGraph {
    private TileController controller;

    private List<INetworkNode> nodes = new ArrayList<INetworkNode>();

    public NetworkNodeGraph(TileController controller) {
        this.controller = controller;
    }

    @Override
    public void rebuild(BlockPos start, boolean notify) {
        if (!controller.canRun()) {
            if (!nodes.isEmpty()) {
                disconnectAll();
            }

            return;
        }

        World world = getWorld();

        List<INetworkNode> newNodes = new ArrayList<INetworkNode>();

        Set<BlockPos> checked = new HashSet<BlockPos>();
        Queue<BlockPos> toCheck = new ArrayDeque<BlockPos>();

        checked.add(start);
        toCheck.add(start);

        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos pos = start.offset(facing);

            checked.add(pos);
            toCheck.add(pos);
        }

        BlockPos currentPos;
        while ((currentPos = toCheck.poll()) != null) {
            TileEntity tile = world.getTileEntity(currentPos);

            if (tile instanceof TileController && !controller.getPos().equals(currentPos)) {
                world.createExplosion(null, currentPos.getX(), currentPos.getY(), currentPos.getZ(), 1.5f, true);
            }

            if (!(tile instanceof INetworkNode)) {
                continue;
            }

            INetworkNode node = (INetworkNode) tile;

            newNodes.add(node);

            if (tile instanceof TileNetworkTransmitter) {
                final TileNetworkTransmitter transmitter = (TileNetworkTransmitter) tile;

                if (transmitter.canTransmit()) {
                    if (!transmitter.isSameDimension()) {
                        final World dimensionWorld = DimensionManager.getWorld(transmitter.getReceiverDimension());

                        if (dimensionWorld != null) {
                            NetworkNodeGraph dimensionGraph = new NetworkNodeGraph(controller) {
                                @Override
                                public World getWorld() {
                                    return dimensionWorld;
                                }
                            };

                            dimensionGraph.rebuild(transmitter.getReceiver(), false);

                            newNodes.addAll(dimensionGraph.all());
                        }
                    } else {
                        BlockPos receiver = transmitter.getReceiver();

                        if (checked.add(receiver)) {
                            toCheck.add(receiver);
                        }
                    }
                }
            }

            for (EnumFacing facing : EnumFacing.VALUES) {
                if (node.canConduct(facing)) {
                    BlockPos pos = currentPos.offset(facing);

                    if (checked.add(pos)) {
                        toCheck.add(pos);
                    }
                }
            }
        }

        List<INetworkNode> oldNodes = new ArrayList<>(nodes);

        this.nodes = newNodes;

        boolean changed = false;

        if (notify && !InfinityStorage.channelsEnabled) {
            for (INetworkNode node : nodes) {
                if (!oldNodes.contains(node)) {
                    node.onConnected(controller);

                    changed = true;
                }
            }

            for (INetworkNode oldNode : oldNodes) {
                if (!nodes.contains(oldNode)) {
                    oldNode.onDisconnected(controller);

                    changed = true;
                }
            }
        } else if (InfinityStorage.requireNetworkToolToReload) {
            ChannelReloadThread crt = new ChannelReloadThread(world, false);
            crt.setupAtPosition(controller.getPos(), controller);
            crt.start();
        }

        if (changed) {
            controller.getDataManager().sendParameterToWatchers(TileController.NODES);
        }
    }

    @Override
    public List<INetworkNode> all() {
        return nodes;
    }

    @Override
    public void replace(INetworkNode node) {
        nodes.remove(node);
        nodes.add(node);

        if(node instanceof ICraftingPatternContainer){
            controller.rebuildPatterns();
        }

        if(node instanceof IItemStorageProvider){
            controller.getItemStorage().rebuild();
        }

        if(node instanceof IFluidStorageProvider){
            controller.getFluidStorage().rebuild();
        }

        controller.getDataManager().sendParameterToWatchers(TileController.NODES);
    }

    @Override
    public void disconnectAll() {
        List<INetworkNode> oldNodes = new ArrayList<>(nodes);

        nodes.clear();

        for(INetworkNode node : oldNodes){
            if(node.isConnected()) {
                node.onDisconnected(controller);
            }
        }

        controller.getDataManager().sendParameterToWatchers(TileController.NODES);
    }

    public World getWorld() {
        return controller.getWorld();
    }
}
