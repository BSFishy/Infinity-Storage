package infinitystorage.gui;

import infinitystorage.api.storage.CompareUtils;
import infinitystorage.container.ContainerInterface;
import infinitystorage.gui.sidebutton.SideButtonCompare;
import infinitystorage.gui.sidebutton.SideButtonRedstoneMode;
import infinitystorage.tile.TileInterface;

public class GuiInterface extends GuiBase {
    public GuiInterface(ContainerInterface container) {
        super(container, 211, 217);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(TileInterface.REDSTONE_MODE));

        addSideButton(new SideButtonCompare(TileInterface.COMPARE, CompareUtils.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(TileInterface.COMPARE, CompareUtils.COMPARE_NBT));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/interface.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.infinitystorage:interface.import"));
        drawString(7, 42, t("gui.infinitystorage:interface.export"));
        drawString(7, 122, t("container.inventory"));
    }
}
