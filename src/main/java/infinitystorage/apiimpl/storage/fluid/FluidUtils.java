package infinitystorage.apiimpl.storage.fluid;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import infinitystorage.api.autocrafting.ICraftingPattern;
import infinitystorage.api.network.INetworkMaster;
import infinitystorage.api.network.NetworkUtils;

public final class FluidUtils {
    public static final ItemStack EMPTY_BUCKET = new ItemStack(Items.BUCKET);

    public static FluidStack copyStackWithSize(FluidStack stack, int size) {
        FluidStack copy = stack.copy();
        copy.amount = size;
        return copy;
    }

    public static FluidStack copy(FluidStack stack) {
        return stack == null ? null : stack.copy();
    }

    @SuppressWarnings("deprecation")
    public static FluidStack getFluidFromStack(ItemStack stack, boolean simulate) {
        if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
            return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).drain(Fluid.BUCKET_VOLUME, !simulate);
        } else if (stack.getItem() instanceof IFluidContainerItem) {
            return ((IFluidContainerItem) stack.getItem()).drain(stack, Fluid.BUCKET_VOLUME, !simulate);
        }

        return null;
    }

    public static boolean hasFluidBucket(FluidStack stack) {
        return stack.getFluid() == FluidRegistry.WATER || stack.getFluid() == FluidRegistry.LAVA || FluidRegistry.getBucketFluids().contains(stack.getFluid());
    }

    public static ItemStack extractItemOrIfBucketLookInFluids(INetworkMaster network, ItemStack stack, int size) {
        ItemStack result = NetworkUtils.extractItem(network, stack, size);

        if (result == null && stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
            FluidStack fluidStack = getFluidFromStack(stack, true);

            if (fluidStack != null && hasFluidBucket(fluidStack)) {
                result = extractBucket(network);

                if (result != null) {
                    result.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).fill(NetworkUtils.extractFluid(network, fluidStack, Fluid.BUCKET_VOLUME), true);
                } else {
                    ICraftingPattern pattern = NetworkUtils.getPattern(network, EMPTY_BUCKET);

                    if (pattern != null) {
                        network.addCraftingTask(NetworkUtils.createCraftingTask(network, pattern));
                    }
                }
            }
        }

        return result;
    }

    public static ItemStack extractBucket(INetworkMaster network) {
        return NetworkUtils.extractItem(network, EMPTY_BUCKET, 1);
    }
}
