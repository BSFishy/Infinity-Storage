package infinitystorage.gui;

import infinitystorage.api.storage.CompareUtils;
import infinitystorage.container.ContainerImporter;
import infinitystorage.gui.sidebutton.SideButtonCompare;
import infinitystorage.gui.sidebutton.SideButtonMode;
import infinitystorage.gui.sidebutton.SideButtonRedstoneMode;
import infinitystorage.gui.sidebutton.SideButtonType;
import infinitystorage.tile.TileImporter;

public class GuiImporter extends GuiBase {
    public GuiImporter(ContainerImporter container) {
        super(container, 211, 137);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(TileImporter.REDSTONE_MODE));

        addSideButton(new SideButtonType(TileImporter.TYPE));

        addSideButton(new SideButtonMode(TileImporter.MODE));

        addSideButton(new SideButtonCompare(TileImporter.COMPARE, CompareUtils.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(TileImporter.COMPARE, CompareUtils.COMPARE_NBT));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/importer.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.infinitystorage:importer"));
        drawString(7, 43, t("container.inventory"));
    }
}
