package infinitystorage.gui;

import infinitystorage.api.storage.CompareUtils;
import infinitystorage.container.ContainerExporter;
import infinitystorage.gui.sidebutton.SideButtonCompare;
import infinitystorage.gui.sidebutton.SideButtonRedstoneMode;
import infinitystorage.gui.sidebutton.SideButtonType;
import infinitystorage.tile.TileExporter;

public class GuiExporter extends GuiBase {
    public GuiExporter(ContainerExporter container) {
        super(container, 211, 137);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(TileExporter.REDSTONE_MODE));

        addSideButton(new SideButtonType(TileExporter.TYPE));

        addSideButton(new SideButtonCompare(TileExporter.COMPARE, CompareUtils.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(TileExporter.COMPARE, CompareUtils.COMPARE_NBT));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/exporter.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.infinitystorage:exporter"));
        drawString(7, 43, t("container.inventory"));
    }
}
