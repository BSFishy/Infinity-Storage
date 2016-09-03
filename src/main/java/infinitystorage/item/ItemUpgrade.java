package infinitystorage.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import infinitystorage.InfinityStorage;

import java.util.List;

public class ItemUpgrade extends ItemBase {
    public static final int TYPE_RANGE = 1;
    public static final int TYPE_SPEED = 2;
    public static final int TYPE_CRAFTING = 3;
    public static final int TYPE_STACK = 4;
    public static final int TYPE_INTERDIMENSIONAL = 5;

    public ItemUpgrade() {
        super("upgrade");

        setHasSubtypes(true);
        setMaxDamage(0);
        setMaxStackSize(1);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i <= 5; ++i) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    public static int getEnergyUsage(int type) {
        switch (type) {
            case TYPE_RANGE:
                return InfinityStorage.INSTANCE.rangeUpgradeUsage;
            case TYPE_SPEED:
                return InfinityStorage.INSTANCE.speedUpgradeUsage;
            case TYPE_CRAFTING:
                return InfinityStorage.INSTANCE.craftingUpgradeUsage;
            case TYPE_STACK:
                return InfinityStorage.INSTANCE.stackUpgradeUsage;
            case TYPE_INTERDIMENSIONAL:
                return InfinityStorage.INSTANCE.interdimensionalUpgradeUsage;
            default:
                return 0;
        }
    }

    public static ItemStack getRequirement(int type) {
        switch (type) {
            case ItemUpgrade.TYPE_RANGE:
                return new ItemStack(Items.ENDER_PEARL);
            case ItemUpgrade.TYPE_SPEED:
                return new ItemStack(Items.SUGAR);
            case ItemUpgrade.TYPE_CRAFTING:
                return new ItemStack(Blocks.CRAFTING_TABLE);
            case ItemUpgrade.TYPE_INTERDIMENSIONAL:
                return new ItemStack(Items.NETHER_STAR);
            default:
                return null;
        }
    }
}
