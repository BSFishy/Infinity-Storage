package infinitystorage.tile.grid;

import net.minecraft.util.math.BlockPos;
import infinitystorage.api.network.grid.IFluidGridHandler;
import infinitystorage.api.network.grid.IItemGridHandler;
import infinitystorage.block.EnumGridType;
import infinitystorage.gui.grid.GridFilteredItem;
import infinitystorage.inventory.ItemHandlerBasic;
import infinitystorage.tile.data.TileDataParameter;

import java.util.List;

public interface IGrid {
    EnumGridType getType();

    BlockPos getNetworkPosition();

    IItemGridHandler getItemHandler();

    IFluidGridHandler getFluidHandler();

    String getGuiTitle();

    int getViewType();

    int getSortingType();

    int getSortingDirection();

    int getSearchBoxMode();

    void onViewTypeChanged(int type);

    void onSortingTypeChanged(int type);

    void onSortingDirectionChanged(int direction);

    void onSearchBoxModeChanged(int searchBoxMode);

    List<GridFilteredItem> getFilteredItems();

    ItemHandlerBasic getFilter();

    TileDataParameter<Integer> getRedstoneModeConfig();

    boolean isConnected();
}
