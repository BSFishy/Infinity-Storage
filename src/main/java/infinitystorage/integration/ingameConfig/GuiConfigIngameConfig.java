package infinitystorage.integration.ingameConfig;

import infinitystorage.InfinityStorage;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class GuiConfigIngameConfig extends GuiConfig {

    private static List<IConfigElement> elements = new ArrayList<>();

    static{
        addElement(InfinityStorage.INSTANCE.config.getCategory("channels"));
        addElement(InfinityStorage.INSTANCE.config.getCategory("controller"));
        addElement(InfinityStorage.INSTANCE.config.getCategory("energy"));
        addElement(InfinityStorage.INSTANCE.config.getCategory("misc"));
        addElement(InfinityStorage.INSTANCE.config.getCategory("upgrades"));
        addElement(InfinityStorage.INSTANCE.config.getCategory("wirelessgrid"));
        addElement(InfinityStorage.INSTANCE.config.getCategory("wirelesstransmitter"));
    }

    public GuiConfigIngameConfig(GuiScreen parent) {
        super(parent, elements,
                InfinityStorage.ID,
                false,
                false,
                "Hey, guess what? You can edit the config ingame! :D");
        titleLine2 = InfinityStorage.INSTANCE.config.getConfigFile().getAbsolutePath();
    }

    private static void addElement(ConfigCategory c){
        elements.add(new ConfigElement(c));
    }

    @Override
    public void initGui()
    {
        // You can add buttons and initialize fields here
        super.initGui();
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        // You can do things like create animations, draw additional elements, etc. here
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        // You can process any additional buttons you may have added here
        super.actionPerformed(button);
        InfinityStorage.INSTANCE.reloadConfig();
    }
}
