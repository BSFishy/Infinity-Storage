package infinitystorage.inventory;

import infinitystorage.InfinityStorageItems;
import infinitystorage.apiimpl.storage.fluid.FluidStorageNBT;
import infinitystorage.apiimpl.storage.item.ItemStorageNBT;
import net.minecraft.item.ItemStack;

public interface IItemValidator {
    IItemValidator ITEM_STORAGE_DISK = new ItemValidatorBasic(InfinityStorageItems.STORAGE_DISK) {
        @Override
        public boolean isValid(ItemStack disk) {
            return super.isValid(disk) && ItemStorageNBT.isValid(disk);
        }
    };
    IItemValidator FLUID_STORAGE_DISK = new ItemValidatorBasic(InfinityStorageItems.FLUID_STORAGE_DISK) {
        @Override
        public boolean isValid(ItemStack disk) {
            return super.isValid(disk) && FluidStorageNBT.isValid(disk);
        }
    };
    IItemValidator STORAGE_DISK = new IItemValidator() {
        @Override
        public boolean isValid(ItemStack stack) {
            return ITEM_STORAGE_DISK.isValid(stack) || FLUID_STORAGE_DISK.isValid(stack);
        }
    };

    boolean isValid(ItemStack stack);
}
