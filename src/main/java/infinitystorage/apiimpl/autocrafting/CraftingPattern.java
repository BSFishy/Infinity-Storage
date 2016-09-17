package infinitystorage.apiimpl.autocrafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import infinitystorage.api.autocrafting.ICraftingPattern;
import infinitystorage.api.autocrafting.ICraftingPatternContainer;
import infinitystorage.api.storage.CompareUtils;
import infinitystorage.apiimpl.autocrafting.registry.CraftingTaskFactoryNormal;
import infinitystorage.apiimpl.autocrafting.registry.CraftingTaskFactoryProcessing;
import infinitystorage.item.ItemPattern;

import java.util.ArrayList;
import java.util.List;

public class CraftingPattern implements ICraftingPattern {
    private ICraftingPatternContainer container;
    private ItemStack stack;
    private List<ItemStack> inputs = new ArrayList<ItemStack>();
    private List<ItemStack> outputs = new ArrayList<ItemStack>();

    public CraftingPattern(World world, ICraftingPatternContainer container, ItemStack stack) {
        this.container = container;
        this.stack = stack;

        InventoryCrafting dummyInventory = new InventoryCrafting(new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer player) {
                return false;
            }
        }, 3, 3);

        for (int i = 0; i < 9; ++i) {
            ItemStack slot = ItemPattern.getSlot(stack, i);

            if (slot != null) {
                for (int j = 0; j < slot.stackSize; ++j) {
                    inputs.add(ItemHandlerHelper.copyStackWithSize(slot, 1));
                }

                dummyInventory.setInventorySlotContents(i, slot);
            }
        }

        if (!ItemPattern.isProcessing(stack)) {
            ItemStack output = CraftingManager.getInstance().findMatchingRecipe(dummyInventory, world);

            if (output != null) {
                outputs.add(output.copy());

                for (ItemStack remaining : CraftingManager.getInstance().getRemainingItems(dummyInventory, world)) {
                    if (remaining != null) {
                        outputs.add(remaining.copy());
                    }
                }
            }
        } else {
            outputs = ItemPattern.getOutputs(stack);
        }
    }

    @Override
    public ICraftingPatternContainer getContainer() {
        return container;
    }

    @Override
    public ItemStack getStack() {
        return stack;
    }

    @Override
    public boolean isValid() {
        return !inputs.isEmpty() && !outputs.isEmpty();
    }

    @Override
    public List<ItemStack> getInputs() {
        return inputs;
    }

    @Override
    public boolean isOredicted(){
        return ItemPattern.isOredicted(stack);
    }

    @Override
    public List<ItemStack> getOutputs() {
        return outputs;
    }

    @Override
    public String getId() {
        return ItemPattern.isProcessing(stack) ? CraftingTaskFactoryProcessing.ID : CraftingTaskFactoryNormal.ID;
    }

    @Override
    public int getQuantityPerRequest(ItemStack requested) {
        int quantity = 0;

        for (ItemStack output : outputs) {
            if (CompareUtils.compareStackNoQuantity(requested, output)) {
                quantity += output.stackSize;

                if (!ItemPattern.isProcessing(stack)) {
                    break;
                }
            }
        }

        return quantity;
    }
}
