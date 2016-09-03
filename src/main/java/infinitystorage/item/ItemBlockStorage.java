package infinitystorage.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import infinitystorage.InfinityStorageBlocks;
import infinitystorage.InfinityStorageItems;
import infinitystorage.apiimpl.storage.item.ItemStorageNBT;
import infinitystorage.block.EnumItemStorageType;
import infinitystorage.tile.TileStorage;

import java.util.List;

public class ItemBlockStorage extends ItemBlockBase {
    public ItemBlockStorage() {
        super(InfinityStorageBlocks.STORAGE, InfinityStorageBlocks.STORAGE.getPlacementType(), true);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        EnumItemStorageType type = EnumItemStorageType.getById(stack.getMetadata());

        if (type != null && isValid(stack)) {
            NBTTagCompound tag = stack.getTagCompound().getCompoundTag(TileStorage.NBT_STORAGE);

            if (type == EnumItemStorageType.TYPE_CREATIVE) {
                tooltip.add(I18n.format("misc.infinitystorage:storage.stored", ItemStorageNBT.getStoredFromNBT(tag)));
            } else {
                tooltip.add(I18n.format("misc.infinitystorage:storage.stored_capacity", ItemStorageNBT.getStoredFromNBT(tag), type.getCapacity()));
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        EnumItemStorageType type = EnumItemStorageType.getById(stack.getMetadata());

        if (type != null && stack.stackSize == 1 && isValid(stack) && ItemStorageNBT.getStoredFromNBT(stack.getTagCompound().getCompoundTag(TileStorage.NBT_STORAGE)) == 0 && stack.getMetadata() != ItemStorageDisk.TYPE_CREATIVE && !world.isRemote && player.isSneaking()) {
            ItemStack storagePart = new ItemStack(InfinityStorageItems.STORAGE_PART, 1, stack.getMetadata());

            if (!player.inventory.addItemStackToInventory(storagePart.copy())) {
                InventoryHelper.spawnItemStack(world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), storagePart);
            }

            ItemStack processor = new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC);

            if (!player.inventory.addItemStackToInventory(processor.copy())) {
                InventoryHelper.spawnItemStack(world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), processor);
            }

            return new ActionResult<>(EnumActionResult.SUCCESS, new ItemStack(InfinityStorageBlocks.MACHINE_CASING));
        }

        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    private static boolean isValid(ItemStack stack) {
        return stack.getTagCompound() != null && stack.getTagCompound().hasKey(TileStorage.NBT_STORAGE);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.onUpdate(stack, world, entity, slot, selected);

        if (!stack.hasTagCompound()) {
            initNBT(stack);
        }
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);

        initNBT(stack);
    }

    public static ItemStack initNBT(ItemStack stack) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag(TileStorage.NBT_STORAGE, ItemStorageNBT.createNBT());
        stack.setTagCompound(tag);
        return stack;
    }
}
