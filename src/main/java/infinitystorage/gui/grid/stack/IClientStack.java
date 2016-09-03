package infinitystorage.gui.grid.stack;

import infinitystorage.gui.GuiBase;

public interface IClientStack {
    int getHash();

    String getName();

    String getModId();

    String getTooltip();

    int getQuantity();

    void draw(GuiBase gui, int x, int y, boolean isOverWithShift);
}
