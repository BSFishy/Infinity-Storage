package infinitystorage.container;

import net.minecraft.entity.player.EntityPlayer;
import infinitystorage.tile.TileController;

public class ContainerController extends ContainerBase {
    public ContainerController(TileController controller, EntityPlayer player) {
        super(controller, player);

        addPlayerInventory(8, 99);
    }
}
