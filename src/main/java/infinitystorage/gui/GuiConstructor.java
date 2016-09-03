package infinitystorage.gui;

import infinitystorage.api.storage.CompareUtils;
import infinitystorage.container.ContainerConstructor;
import infinitystorage.gui.sidebutton.SideButtonCompare;
import infinitystorage.gui.sidebutton.SideButtonRedstoneMode;
import infinitystorage.gui.sidebutton.SideButtonType;
import infinitystorage.tile.TileConstructor;

public class GuiConstructor extends GuiBase {
    public GuiConstructor(ContainerConstructor container) {
        super(container, 211, 137);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(TileConstructor.REDSTONE_MODE));

        addSideButton(new SideButtonType(TileConstructor.TYPE));

        addSideButton(new SideButtonCompare(TileConstructor.COMPARE, CompareUtils.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(TileConstructor.COMPARE, CompareUtils.COMPARE_NBT));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/constructor.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.infinitystorage:constructor"));
        drawString(7, 43, t("container.inventory"));
    }
}
