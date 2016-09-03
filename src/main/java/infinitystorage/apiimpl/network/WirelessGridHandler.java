package infinitystorage.apiimpl.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import infinitystorage.InfinityStorage;
import infinitystorage.InfinityStorageGui;
import infinitystorage.InfinityStorageItems;
import infinitystorage.api.network.*;
import infinitystorage.item.ItemWirelessGrid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WirelessGridHandler implements IWirelessGridHandler {
    private INetworkMaster network;

    private List<IWirelessGridConsumer> consumers = new ArrayList<IWirelessGridConsumer>();
    private List<IWirelessGridConsumer> consumersToRemove = new ArrayList<IWirelessGridConsumer>();

    public WirelessGridHandler(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public void update() {
        consumers.removeAll(consumersToRemove);
        consumersToRemove.clear();
    }

    @Override
    public boolean onOpen(EntityPlayer player, World controllerWorld, EnumHand hand) {
        boolean inRange = false;

        for (INetworkNode node : network.getNodeGraph().all()) {
            if (node instanceof IWirelessTransmitter && node.getNodeWorld().provider.getDimension() == player.dimension) {
                IWirelessTransmitter transmitter = (IWirelessTransmitter) node;

                double distance = Math.sqrt(Math.pow(transmitter.getOrigin().getX() - player.posX, 2) + Math.pow(transmitter.getOrigin().getY() - player.posY, 2) + Math.pow(transmitter.getOrigin().getZ() - player.posZ, 2));

                if (distance < transmitter.getRange()) {
                    inRange = true;

                    break;
                }
            }
        }

        if (!inRange) {
            return false;
        }

        ItemStack stack = player.getHeldItem(hand);

        if (InfinityStorage.INSTANCE.wirelessGridUsesEnergy && stack.getItemDamage() != ItemWirelessGrid.TYPE_CREATIVE && InfinityStorageItems.WIRELESS_GRID.getEnergyStored(stack) <= InfinityStorage.INSTANCE.wirelessGridOpenUsage) {
            return true;
        }

        consumers.add(new WirelessGridConsumer(player, stack));

        player.openGui(InfinityStorage.INSTANCE, InfinityStorageGui.WIRELESS_GRID, player.worldObj, hand.ordinal(), controllerWorld.provider.getDimension(), 0);

        network.sendItemStorageToClient((EntityPlayerMP) player);

        drainEnergy(player, InfinityStorage.INSTANCE.wirelessGridOpenUsage);

        return true;
    }

    @Override
    public void onClose(EntityPlayer player) {
        IWirelessGridConsumer consumer = getConsumer(player);

        if (consumer != null) {
            consumersToRemove.add(consumer);
        }
    }

    @Override
    public void drainEnergy(EntityPlayer player, int energy) {
        IWirelessGridConsumer consumer = getConsumer(player);

        if (consumer != null && InfinityStorage.INSTANCE.wirelessGridUsesEnergy) {
            ItemWirelessGrid item = InfinityStorageItems.WIRELESS_GRID;

            if (consumer.getStack().getItemDamage() != ItemWirelessGrid.TYPE_CREATIVE) {
                item.extractEnergy(consumer.getStack(), energy, false);

                if (item.getEnergyStored(consumer.getStack()) <= 0) {
                    onClose(player);

                    consumer.getPlayer().closeScreen();
                }
            }
        }
    }

    @Override
    public IWirelessGridConsumer getConsumer(EntityPlayer player) {
        Iterator<IWirelessGridConsumer> it = consumers.iterator();

        while (it.hasNext()) {
            IWirelessGridConsumer consumer = it.next();

            if (consumer.getPlayer() == player) {
                return consumer;
            }
        }

        return null;
    }
}
