package infinitystorage.tile;

import infinitystorage.InfinityStorage;
import infinitystorage.InfinityStorageBlocks;
import infinitystorage.tile.data.ITileDataProducer;
import infinitystorage.tile.data.InfinityStorageSerializers;
import infinitystorage.tile.data.TileDataParameter;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TileCraftingMonitor extends TileNode {
    public static final TileDataParameter<List<ClientCraftingTask>> TASKS = new TileDataParameter<>(InfinityStorageSerializers.CLIENT_CRAFTING_TASK_SERIALIZER, new ArrayList<>(), new ITileDataProducer<List<ClientCraftingTask>, TileCraftingMonitor>() {
        @Override
        public List<ClientCraftingTask> getValue(TileCraftingMonitor tile) {
            if (tile.connected) {
                List<ClientCraftingTask> tasks = tile.network.getCraftingTasks().stream().map(t -> new ClientCraftingTask(
                    t.getStatus(),
                    t.getPattern().getOutputs(),
                    t.getProgress(),
                    t.getChild()
                )).collect(Collectors.toList());

                return tasks;
            } else {
                return Collections.emptyList();
            }
        }
    });

    public TileCraftingMonitor() {
        dataManager.addParameter(TASKS);
        setupClientNode(new ItemStack(InfinityStorageBlocks.CRAFTING_MONITOR), InfinityStorage.INSTANCE.craftingMonitorUsage);
    }

    @Override
    public int getEnergyUsage() {
        return InfinityStorage.INSTANCE.craftingMonitorUsage;
    }

    @Override
    public void updateNode() {
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}
