package infinitystorage.gui;

import infinitystorage.container.ContainerSolderer;
import infinitystorage.gui.sidebutton.SideButtonRedstoneMode;
import infinitystorage.tile.TileSolderer;

public class GuiSolderer extends GuiBase {
    private TileSolderer solderer;

    public GuiSolderer(ContainerSolderer container, TileSolderer solderer) {
        super(container, 211, 171);

        this.solderer = solderer;
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(TileSolderer.REDSTONE_MODE));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/solderer.png");

        drawTexture(x, y, 0, 0, width, height);

        if (solderer.isWorking()) {
            drawTexture(x + 83, y + 38 - 1, 212, 0, getProgressScaled(22), 15);
        }
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.infinitystorage:solderer"));
        drawString(7, 77, t("container.inventory"));

        if (inBounds(83, 38, 22, 15, mouseX, mouseY) && solderer.isWorking()) {
            drawTooltip(mouseX, mouseY, getProgressScaled(100) + "%");
        }
    }

    private int getProgressScaled(int scale) {
        float progress = TileSolderer.PROGRESS.getValue();
        float duration = TileSolderer.DURATION.getValue();

        if (progress > duration) {
            return scale;
        }

        return (int) (progress / duration * (float) scale);
    }
}
