package infinitystorage.gui.sidebutton;

import net.minecraft.util.text.TextFormatting;
import infinitystorage.gui.GuiBase;
import infinitystorage.tile.grid.IGrid;
import infinitystorage.tile.grid.TileGrid;

public class SideButtonGridSortingDirection extends SideButton {
    private IGrid grid;

    public SideButtonGridSortingDirection(IGrid grid) {
        this.grid = grid;
    }

    @Override
    public String getTooltip(GuiBase gui) {
        return TextFormatting.YELLOW + gui.t("sidebutton.infinitystorage:grid.sorting.direction") + TextFormatting.RESET + "\n" + gui.t("sidebutton.infinitystorage:grid.sorting.direction." + grid.getSortingDirection());
    }

    @Override
    public void draw(GuiBase gui, int x, int y) {
        gui.bindTexture("icons.png");
        gui.drawTexture(x, y + 2 - 1, grid.getSortingDirection() * 16, 16, 16, 16);
    }

    @Override
    public void actionPerformed() {
        int dir = grid.getSortingDirection();

        if (dir == TileGrid.SORTING_DIRECTION_ASCENDING) {
            dir = TileGrid.SORTING_DIRECTION_DESCENDING;
        } else if (dir == TileGrid.SORTING_DIRECTION_DESCENDING) {
            dir = TileGrid.SORTING_DIRECTION_ASCENDING;
        }

        grid.onSortingDirectionChanged(dir);
    }
}
