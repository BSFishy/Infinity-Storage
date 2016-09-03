package infinitystorage.apiimpl.solderer;

import net.minecraftforge.items.IItemHandler;
import infinitystorage.api.solderer.ISoldererRecipe;
import infinitystorage.api.solderer.ISoldererRegistry;
import infinitystorage.api.storage.CompareUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SoldererRegistry implements ISoldererRegistry {
    private List<ISoldererRecipe> recipes = new ArrayList<ISoldererRecipe>();

    @Override
    public void addRecipe(@Nonnull ISoldererRecipe recipe) {
        recipes.add(recipe);
    }

    @Override
    public List<ISoldererRecipe> getRecipes() {
        return recipes;
    }

    @Override
    @Nullable
    public ISoldererRecipe getRecipe(@Nonnull IItemHandler items) {
        for (ISoldererRecipe recipe : recipes) {
            boolean found = true;

            for (int i = 0; i < 3; ++i) {
                if (!CompareUtils.compareStackNoQuantity(recipe.getRow(i), items.getStackInSlot(i)) && !CompareUtils.compareStackOreDict(recipe.getRow(i), items.getStackInSlot(i))) {
                    found = false;
                }

                if (items.getStackInSlot(i) != null && recipe.getRow(i) != null) {
                    if (items.getStackInSlot(i).stackSize < recipe.getRow(i).stackSize) {
                        found = false;
                    }
                }
            }

            if (found) {
                return recipe;
            }
        }

        return null;
    }
}
