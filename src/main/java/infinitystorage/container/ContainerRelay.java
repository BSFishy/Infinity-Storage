package infinitystorage.container;

import net.minecraft.entity.player.EntityPlayer;
import infinitystorage.tile.TileRelay;

public class ContainerRelay extends ContainerBase {
    public ContainerRelay(TileRelay relay, EntityPlayer player) {
        super(relay, player);

        addPlayerInventory(8, 50);
    }
}
