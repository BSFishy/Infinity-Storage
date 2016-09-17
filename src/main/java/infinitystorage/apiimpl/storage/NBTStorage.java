package infinitystorage.apiimpl.storage;

import net.minecraft.item.ItemStack;
import infinitystorage.InfinityStorageItems;
import infinitystorage.apiimpl.storage.fluid.FluidStorageNBT;
import infinitystorage.apiimpl.storage.item.ItemStorageNBT;

import java.util.function.Function;

public final class NBTStorage {
    public static void constructFromDrive(ItemStack disk, int slot, ItemStorageNBT[] itemStorages, FluidStorageNBT[] fluidStorages, Function<ItemStack, ItemStorageNBT> itemStorageSupplier, Function<ItemStack, FluidStorageNBT> fluidStorageNBTSupplier) {
        if (disk == null) {
            itemStorages[slot] = null;
            fluidStorages[slot] = null;
        } else {
            if (disk.getItem() == InfinityStorageItems.STORAGE_DISK) {
                itemStorages[slot] = itemStorageSupplier.apply(disk);
            } else if (disk.getItem() == InfinityStorageItems.FLUID_STORAGE_DISK) {
                fluidStorages[slot] = fluidStorageNBTSupplier.apply(disk);
            }
        }
    }
}