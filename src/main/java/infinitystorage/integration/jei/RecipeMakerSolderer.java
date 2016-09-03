package infinitystorage.integration.jei;

import net.minecraft.item.ItemStack;
import infinitystorage.api.InfinityStorageAPI;
import infinitystorage.api.solderer.ISoldererRecipe;

import java.util.ArrayList;
import java.util.List;

public final class RecipeMakerSolderer {
    public static List<RecipeWrapperSolderer> getRecipes() {
        List<RecipeWrapperSolderer> recipes = new ArrayList<>();

        for (ISoldererRecipe recipe : InfinityStorageAPI.SOLDERER_REGISTRY.getRecipes()) {
            List<ItemStack> inputs = new ArrayList<>();

            inputs.add(recipe.getRow(0));
            inputs.add(recipe.getRow(1));
            inputs.add(recipe.getRow(2));

            ItemStack output = recipe.getResult();

            recipes.add(new RecipeWrapperSolderer(inputs, output));
        }

        return recipes;
    }
}
