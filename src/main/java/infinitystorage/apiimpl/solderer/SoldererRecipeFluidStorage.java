package infinitystorage.apiimpl.solderer;

import net.minecraft.item.ItemStack;
import infinitystorage.InfinityStorageBlocks;
import infinitystorage.InfinityStorageItems;
import infinitystorage.api.solderer.ISoldererRecipe;
import infinitystorage.block.EnumFluidStorageType;
import infinitystorage.item.ItemBlockFluidStorage;
import infinitystorage.item.ItemProcessor;

public class SoldererRecipeFluidStorage implements ISoldererRecipe {
    private EnumFluidStorageType type;
    private ItemStack[] rows;

    public SoldererRecipeFluidStorage(EnumFluidStorageType type, int storagePart) {
        this.type = type;
        this.rows = new ItemStack[]{
            new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            new ItemStack(InfinityStorageBlocks.MACHINE_CASING),
            new ItemStack(InfinityStorageItems.FLUID_STORAGE_PART, 1, storagePart)
        };
    }

    @Override
    public ItemStack getRow(int row) {
        return rows[row];
    }

    @Override
    public ItemStack getResult() {
        return ItemBlockFluidStorage.initNBT(new ItemStack(InfinityStorageBlocks.FLUID_STORAGE, 1, type.getId()));
    }

    @Override
    public int getDuration() {
        return 200;
    }
}
