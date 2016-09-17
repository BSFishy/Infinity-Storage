package infinitystorage.gui;

import infinitystorage.api.storage.CompareUtils;
import infinitystorage.container.ContainerDiskManipulator;
import infinitystorage.gui.sidebutton.*;
import infinitystorage.tile.TileDiskManipulator;

public class GuiDiskManipulator extends GuiBase {
    public GuiDiskManipulator(ContainerDiskManipulator container) {
        super(container, 176, 211);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(TileDiskManipulator.REDSTONE_MODE));
        addSideButton(new SideButtonIOMode(TileDiskManipulator.IO_MODE));
        addSideButton(new SideButtonType(TileDiskManipulator.TYPE));
        addSideButton(new SideButtonMode(TileDiskManipulator.MODE));
        addSideButton(new SideButtonCompare(TileDiskManipulator.COMPARE, CompareUtils.COMPARE_DAMAGE));
        addSideButton(new SideButtonCompare(TileDiskManipulator.COMPARE, CompareUtils.COMPARE_NBT));
    }

    @Override
    public void update(int x, int y) {

    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/disk_manipulator.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("block.infinitystorage:disk_manipulator.name"));
        drawString(7, 117, t("container.inventory"));
    }
}