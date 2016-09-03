package infinitystorage.apiimpl.storage.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import infinitystorage.api.autocrafting.ICraftingPattern;
import infinitystorage.api.network.INetworkMaster;
import infinitystorage.api.network.NetworkUtils;
import infinitystorage.api.storage.CompareUtils;
import infinitystorage.api.storage.item.IGroupedItemStorage;
import infinitystorage.api.storage.item.IItemStorage;
import infinitystorage.api.storage.item.IItemStorageProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GroupedItemStorage implements IGroupedItemStorage {
    private INetworkMaster network;
    private List<IItemStorage> storages = new ArrayList<>();
    private Multimap<Item, ItemStack> stacks = ArrayListMultimap.create();

    public GroupedItemStorage(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public void rebuild() {
        storages.clear();

        network.getNodeGraph().all().stream()
            .filter(node -> node.canUpdate() && node instanceof IItemStorageProvider)
            .forEach(node -> ((IItemStorageProvider) node).addItemStorages(storages));

        stacks.clear();

        for (IItemStorage storage : storages) {
            for (ItemStack stack : storage.getItems()) {
                add(stack, true);
            }
        }

        for (ICraftingPattern pattern : network.getPatterns()) {
            for (ItemStack output : pattern.getOutputs()) {
                ItemStack patternStack = output.copy();
                patternStack.stackSize = 0;
                add(patternStack, true);
            }
        }

        network.sendItemStorageToClient();
    }

    @Override
    public void add(@Nonnull ItemStack stack, boolean rebuilding) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (CompareUtils.compareStackNoQuantity(otherStack, stack)) {
                otherStack.stackSize += stack.stackSize;

                if (!rebuilding) {
                    network.sendItemStorageDeltaToClient(stack, stack.stackSize);
                }

                return;
            }
        }

        stacks.put(stack.getItem(), stack.copy());

        if (!rebuilding) {
            network.sendItemStorageDeltaToClient(stack, stack.stackSize);
        }
    }

    @Override
    public void remove(@Nonnull ItemStack stack) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (CompareUtils.compareStackNoQuantity(otherStack, stack)) {
                otherStack.stackSize -= stack.stackSize;

                if (otherStack.stackSize == 0) {
                    if (!NetworkUtils.hasPattern(network, stack)) {
                        stacks.remove(otherStack.getItem(), otherStack);
                    }
                }

                network.sendItemStorageDeltaToClient(stack, -stack.stackSize);

                return;
            }
        }
    }

    @Override
    @Nullable
    public ItemStack get(@Nonnull ItemStack stack, int flags) {
        for (ItemStack otherStack : stacks.get(stack.getItem())) {
            if (CompareUtils.compareStack(otherStack, stack, flags)) {
                return otherStack;
            }
        }

        return null;
    }

    @Override
    @Nullable
    public ItemStack get(int hash) {
        for (ItemStack stack : this.stacks.values()) {
            if (NetworkUtils.getItemStackHashCode(stack) == hash) {
                return stack;
            }
        }

        return null;
    }

    @Override
    public Collection<ItemStack> getStacks() {
        return stacks.values();
    }

    @Override
    public List<IItemStorage> getStorages() {
        return storages;
    }
}
