package infinitystorage.gui;

import infinitystorage.container.ContainerWirelessTransmitter;
import infinitystorage.gui.sidebutton.SideButtonRedstoneMode;
import infinitystorage.tile.TileWirelessTransmitter;

public class GuiWirelessTransmitter extends GuiBase {
    public GuiWirelessTransmitter(ContainerWirelessTransmitter container) {
        super(container, 211, 137);
    }

    @Override
    public void init(int x, int y) {
        addSideButton(new SideButtonRedstoneMode(TileWirelessTransmitter.REDSTONE_MODE));
    }

    @Override
    public void update(int x, int y) {
    }

    @Override
    public void drawBackground(int x, int y, int mouseX, int mouseY) {
        bindTexture("gui/wireless_transmitter.png");

        drawTexture(x, y, 0, 0, width, height);
    }

    @Override
    public void drawForeground(int mouseX, int mouseY) {
        drawString(7, 7, t("gui.infinitystorage:wireless_transmitter"));
        drawString(28, 25, t("gui.infinitystorage:wireless_transmitter.distance", TileWirelessTransmitter.RANGE.getValue()));
        drawString(7, 43, t("container.inventory"));
    }
}
