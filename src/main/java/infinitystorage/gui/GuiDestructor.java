package infinitystorage.gui;

import infinitystorage.api.storage.CompareUtils;
import infinitystorage.container.ContainerDestructor;
import infinitystorage.gui.sidebutton.SideButtonCompare;
import infinitystorage.gui.sidebutton.SideButtonMode;
import infinitystorage.gui.sidebutton.SideButtonRedstoneMode;
import infinitystorage.gui.sidebutton.SideButtonType;
import infinitystorage.tile.TileDestructor;

public class GuiDestructor extends GuiBase {
    public GuiDestructor(ContainerDestructor container) {
        super(container, 211, 137);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(TileDestructor.REDSTONE_MODE));

        addSideButton(new SideButtonType(TileDestructor.TYPE));

        addSideButton(new SideButtonMode(TileDestructor.MODE));

        addSideButton(new SideButtonCompare(TileDestructor.COMPARE, CompareUtils.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(TileDestructor.COMPARE, CompareUtils.COMPARE_NBT));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/destructor.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.infinitystorage:destructor"));
        drawString(7, 43, t("container.inventory"));
    }
}
