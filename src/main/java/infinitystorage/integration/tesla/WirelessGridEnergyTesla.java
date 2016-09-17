package infinitystorage.integration.tesla;

import infinitystorage.InfinityStorageItems;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.minecraft.item.ItemStack;
import infinitystorage.item.ItemWirelessGrid;

public class WirelessGridEnergyTesla implements ITeslaHolder, ITeslaConsumer {
    private ItemStack stack;

    public WirelessGridEnergyTesla(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public long getStoredPower() {
        return InfinityStorageItems.WIRELESS_GRID.getEnergyStored(stack);
    }

    @Override
    public long getCapacity() {
        return InfinityStorageItems.WIRELESS_GRID.getMaxEnergyStored(stack);
    }

    @Override
    public long givePower(long power, boolean simulated) {
        return InfinityStorageItems.WIRELESS_GRID.receiveEnergy(stack, (int) power, simulated);
    }
}
