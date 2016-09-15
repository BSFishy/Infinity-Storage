package infinitystorage.gui;

import infinitystorage.container.ContainerCrafter;
import infinitystorage.gui.sidebutton.SideButtonCrafterAutocraftSignal;
import infinitystorage.gui.sidebutton.SideButtonRedstoneMode;
import infinitystorage.tile.TileCrafter;

public class GuiCrafter extends GuiBase {
    public GuiCrafter(ContainerCrafter container) {
        super(container, 211, 137);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(TileCrafter.REDSTONE_MODE));
        addSideButton(new SideButtonCrafterAutocraftSignal());
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/crafter.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.infinitystorage:crafter"));
        drawString(7, 43, t("container.inventory"));
    }
}
