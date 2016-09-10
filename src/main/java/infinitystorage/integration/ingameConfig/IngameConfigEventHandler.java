package infinitystorage.integration.ingameConfig;

import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IngameConfigEventHandler {

    @SideOnly(Side.CLIENT)
    public void onEvent(GuiOpenEvent e){
        if(e.getGui() instanceof GuiConfigIngameConfig){
            e.setGui(new GuiConfigIngameConfig(null));
        }
    }
}
