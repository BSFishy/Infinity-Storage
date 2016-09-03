package infinitystorage.apiimpl.solderer;

import net.minecraft.item.ItemStack;
import infinitystorage.InfinityStorageBlocks;
import infinitystorage.InfinityStorageItems;
import infinitystorage.api.solderer.ISoldererRecipe;
import infinitystorage.block.EnumItemStorageType;
import infinitystorage.item.ItemBlockStorage;
import infinitystorage.item.ItemProcessor;

public class SoldererRecipeStorage implements ISoldererRecipe {
    private EnumItemStorageType type;
    private ItemStack[] rows;

    public SoldererRecipeStorage(EnumItemStorageType type, int storagePart) {
        this.type = type;
        this.rows = new ItemStack[]{
            new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            new ItemStack(InfinityStorageBlocks.MACHINE_CASING),
            new ItemStack(InfinityStorageItems.STORAGE_PART, 1, storagePart)
        };
    }

    @Override
    public ItemStack getRow(int row) {
        return rows[row];
    }

    @Override
    public ItemStack getResult() {
        return ItemBlockStorage.initNBT(new ItemStack(InfinityStorageBlocks.STORAGE, 1, type.getId()));
    }

    @Override
    public int getDuration() {
        return 200;
    }
}
