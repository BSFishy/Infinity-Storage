package infinitystorage.apiimpl.solderer;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import infinitystorage.InfinityStorageItems;
import infinitystorage.api.solderer.ISoldererRecipe;
import infinitystorage.item.ItemUpgrade;

public class SoldererRecipeUpgrade implements ISoldererRecipe {
    private ItemStack[] rows;
    private ItemStack result;

    public SoldererRecipeUpgrade(int type) {
        this.result = new ItemStack(InfinityStorageItems.UPGRADE, 1, type);
        this.rows = new ItemStack[]{
            ItemUpgrade.getRequirement(type),
            new ItemStack(InfinityStorageItems.UPGRADE, 1, 0),
            new ItemStack(Items.REDSTONE)
        };
    }

    @Override
    public ItemStack getRow(int row) {
        return rows[row];
    }

    @Override
    public ItemStack getResult() {
        return result;
    }

    @Override
    public int getDuration() {
        return 250;
    }
}
