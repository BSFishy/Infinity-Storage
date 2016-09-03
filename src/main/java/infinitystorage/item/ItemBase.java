package infinitystorage.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import infinitystorage.InfinityStorage;

public abstract class ItemBase extends Item {
    private String name;

    public ItemBase(String name) {
        this.name = name;

        setRegistryName(InfinityStorage.ID, name);
        setCreativeTab(InfinityStorage.INSTANCE.tab);
    }

    @Override
    public String getUnlocalizedName() {
        return "item." + InfinityStorage.ID + ":" + name;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (getHasSubtypes()) {
            return getUnlocalizedName() + "." + stack.getItemDamage();
        }

        return getUnlocalizedName();
    }
}
