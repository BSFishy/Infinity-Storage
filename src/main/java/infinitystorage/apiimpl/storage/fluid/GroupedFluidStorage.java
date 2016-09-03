package infinitystorage.apiimpl.storage.fluid;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import infinitystorage.api.network.INetworkMaster;
import infinitystorage.api.network.NetworkUtils;
import infinitystorage.api.storage.CompareUtils;
import infinitystorage.api.storage.fluid.IFluidStorage;
import infinitystorage.api.storage.fluid.IFluidStorageProvider;
import infinitystorage.api.storage.fluid.IGroupedFluidStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GroupedFluidStorage implements IGroupedFluidStorage {
    private INetworkMaster network;
    private List<IFluidStorage> storages = new ArrayList<IFluidStorage>();
    private Multimap<Fluid, FluidStack> stacks = ArrayListMultimap.create();

    public GroupedFluidStorage(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public void rebuild() {
        storages.clear();

        network.getNodeGraph().all().stream()
            .filter(node -> node.canUpdate() && node instanceof IFluidStorageProvider)
            .forEach(node -> ((IFluidStorageProvider) node).addFluidStorages(storages));

        stacks.clear();

        for (IFluidStorage storage : storages) {
            for (FluidStack stack : storage.getStacks()) {
                add(stack, true);
            }
        }

        network.sendFluidStorageToClient();
    }

    @Override
    public void add(@Nonnull FluidStack stack, boolean rebuilding) {
        for (FluidStack otherStack : stacks.get(stack.getFluid())) {
            if (otherStack.isFluidEqual(stack)) {
                otherStack.amount += stack.amount;

                if (!rebuilding) {
                    network.sendFluidStorageDeltaToClient(stack, stack.amount);
                }

                return;
            }
        }

        stacks.put(stack.getFluid(), stack.copy());

        if (!rebuilding) {
            network.sendFluidStorageDeltaToClient(stack, stack.amount);
        }
    }

    @Override
    public void remove(@Nonnull FluidStack stack) {
        for (FluidStack otherStack : stacks.get(stack.getFluid())) {
            if (otherStack.isFluidEqual(stack)) {
                otherStack.amount -= stack.amount;

                if (otherStack.amount == 0) {
                    stacks.remove(otherStack.getFluid(), otherStack);
                }

                network.sendFluidStorageDeltaToClient(stack, -stack.amount);

                return;
            }
        }
    }

    @Override
    @Nullable
    public FluidStack get(@Nonnull FluidStack stack, int flags) {
        for (FluidStack otherStack : stacks.get(stack.getFluid())) {
            if (CompareUtils.compareStack(otherStack, stack, flags)) {
                return otherStack;
            }
        }

        return null;
    }

    @Override
    @Nullable
    public FluidStack get(int hash) {
        for (FluidStack stack : this.stacks.values()) {
            if (NetworkUtils.getFluidStackHashCode(stack) == hash) {
                return stack;
            }
        }

        return null;
    }

    @Override
    public Collection<FluidStack> getStacks() {
        return stacks.values();
    }

    @Override
    public List<IFluidStorage> getStorages() {
        return storages;
    }
}
