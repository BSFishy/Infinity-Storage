package infinitystorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import infinitystorage.container.slot.SlotSpecimenType;
import infinitystorage.tile.TileDiskDrive;

public class ContainerDiskDrive extends ContainerBase {
    public ContainerDiskDrive(TileDiskDrive drive, EntityPlayer player) {
        super(drive, player);

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(drive.getDisks(), i, 98 + (i * 18), 78));
        }

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(drive.getDisks(), 4 + i, 98 + (i * 18), 96));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotSpecimenType(drive, i, 8 + (18 * i), 20));
        }

        addPlayerInventory(8, 129);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;

        Slot slot = getSlot(index);

        if (slot != null && slot.getHasStack()) {
            stack = slot.getStack();

            if (index < 8) {
                if (!mergeItemStack(stack, 8 + 9, inventorySlots.size(), false)) {
                    return null;
                }
            } else if (!mergeItemStack(stack, 0, 8, false)) {
                return mergeItemStackToSpecimen(stack, 8, 8 + 9);
            }

            if (stack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }

        return stack;
    }
}
