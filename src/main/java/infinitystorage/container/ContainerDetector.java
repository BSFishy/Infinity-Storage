package infinitystorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import infinitystorage.container.slot.SlotSpecimenType;
import infinitystorage.tile.TileDetector;

public class ContainerDetector extends ContainerBase {
    public ContainerDetector(TileDetector detector, EntityPlayer player) {
        super(detector, player);

        addSlotToContainer(new SlotSpecimenType(detector, 0, 107, 20));

        addPlayerInventory(8, 55);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        Slot slot = getSlot(index);

        if (slot != null && slot.getHasStack() && index > 0) {
            return mergeItemStackToSpecimen(slot.getStack(), 0, 1);
        }

        return null;
    }
}
