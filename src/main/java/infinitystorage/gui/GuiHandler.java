package infinitystorage.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import infinitystorage.InfinityStorageGui;
import infinitystorage.container.*;
import infinitystorage.gui.grid.GuiGrid;
import infinitystorage.tile.*;
import infinitystorage.tile.externalstorage.TileExternalStorage;
import infinitystorage.tile.grid.TileGrid;
import infinitystorage.tile.grid.WirelessGrid;

public class GuiHandler implements IGuiHandler {
    private Container getContainer(int ID, EntityPlayer player, TileEntity tile) {
        switch (ID) {
            case InfinityStorageGui.CONTROLLER:
                return new ContainerController((TileController) tile, player);
            case InfinityStorageGui.GRID:
                return new ContainerGrid((TileGrid) tile, player);
            case InfinityStorageGui.DISK_DRIVE:
                return new ContainerDiskDrive((TileDiskDrive) tile, player);
            case InfinityStorageGui.IMPORTER:
                return new ContainerImporter((TileImporter) tile, player);
            case InfinityStorageGui.EXPORTER:
                return new ContainerExporter((TileExporter) tile, player);
            case InfinityStorageGui.DETECTOR:
                return new ContainerDetector((TileDetector) tile, player);
            case InfinityStorageGui.SOLDERER:
                return new ContainerSolderer((TileSolderer) tile, player);
            case InfinityStorageGui.DESTRUCTOR:
                return new ContainerDestructor((TileDestructor) tile, player);
            case InfinityStorageGui.CONSTRUCTOR:
                return new ContainerConstructor((TileConstructor) tile, player);
            case InfinityStorageGui.STORAGE:
                return new ContainerStorage((TileStorage) tile, player);
            case InfinityStorageGui.EXTERNAL_STORAGE:
                return new ContainerExternalStorage((TileExternalStorage) tile, player);
            case InfinityStorageGui.RELAY:
                return new ContainerRelay((TileRelay) tile, player);
            case InfinityStorageGui.INTERFACE:
                return new ContainerInterface((TileInterface) tile, player);
            case InfinityStorageGui.CRAFTING_MONITOR:
                return new ContainerCraftingMonitor((TileCraftingMonitor) tile, player);
            case InfinityStorageGui.WIRELESS_TRANSMITTER:
                return new ContainerWirelessTransmitter((TileWirelessTransmitter) tile, player);
            case InfinityStorageGui.CRAFTER:
                return new ContainerCrafter((TileCrafter) tile, player);
            case InfinityStorageGui.PROCESSING_PATTERN_ENCODER:
                return new ContainerProcessingPatternEncoder((TileProcessingPatternEncoder) tile, player);
            case InfinityStorageGui.NETWORK_TRANSMITTER:
                return new ContainerNetworkTransmitter((TileNetworkTransmitter) tile, player);
            case InfinityStorageGui.FLUID_INTERFACE:
                return new ContainerFluidInterface((TileFluidInterface) tile, player);
            case InfinityStorageGui.FLUID_STORAGE:
                return new ContainerFluidStorage((TileFluidStorage) tile, player);
            case InfinityStorageGui.DISK_MANIPULATOR:
                return new ContainerDiskManipulator((TileDiskManipulator) tile, player);
            default:
                return null;
        }
    }

    private WirelessGrid getWirelessGrid(EntityPlayer player, int hand, int controllerDimension) {
        return new WirelessGrid(controllerDimension, player.getHeldItem(EnumHand.values()[hand]));
    }

    private ContainerGrid getWirelessGridContainer(EntityPlayer player, int hand, int controllerDimension) {
        return new ContainerGrid(getWirelessGrid(player, hand, controllerDimension), player);
    }

    private ContainerGridFilter getGridFilterContainer(EntityPlayer player, int hand) {
        return new ContainerGridFilter(player, player.getHeldItem(EnumHand.values()[hand]));
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == InfinityStorageGui.WIRELESS_GRID) {
            return getWirelessGridContainer(player, x, y);
        } else if (ID == InfinityStorageGui.GRID_FILTER) {
            return getGridFilterContainer(player, x);
        }

        return getContainer(ID, player, world.getTileEntity(new BlockPos(x, y, z)));
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

        switch (ID) {
            case InfinityStorageGui.CONTROLLER:
                return new GuiController((ContainerController) getContainer(ID, player, tile), (TileController) tile);
            case InfinityStorageGui.GRID:
                return new GuiGrid((ContainerGrid) getContainer(ID, player, tile), (TileGrid) tile);
            case InfinityStorageGui.WIRELESS_GRID:
                return getWirelessGridGui(player, x, y);
            case InfinityStorageGui.DISK_DRIVE:
                return new GuiStorage((ContainerDiskDrive) getContainer(ID, player, tile), (IStorageGui) tile, "gui/disk_drive.png");
            case InfinityStorageGui.IMPORTER:
                return new GuiImporter((ContainerImporter) getContainer(ID, player, tile));
            case InfinityStorageGui.EXPORTER:
                return new GuiExporter((ContainerExporter) getContainer(ID, player, tile));
            case InfinityStorageGui.DETECTOR:
                return new GuiDetector((ContainerDetector) getContainer(ID, player, tile));
            case InfinityStorageGui.SOLDERER:
                return new GuiSolderer((ContainerSolderer) getContainer(ID, player, tile), (TileSolderer) tile);
            case InfinityStorageGui.DESTRUCTOR:
                return new GuiDestructor((ContainerDestructor) getContainer(ID, player, tile));
            case InfinityStorageGui.CONSTRUCTOR:
                return new GuiConstructor((ContainerConstructor) getContainer(ID, player, tile));
            case InfinityStorageGui.STORAGE:
                return new GuiStorage((ContainerStorage) getContainer(ID, player, tile), (TileStorage) tile);
            case InfinityStorageGui.EXTERNAL_STORAGE:
                return new GuiStorage((ContainerExternalStorage) getContainer(ID, player, tile), (TileExternalStorage) tile);
            case InfinityStorageGui.RELAY:
                return new GuiRelay((ContainerRelay) getContainer(ID, player, tile));
            case InfinityStorageGui.INTERFACE:
                return new GuiInterface((ContainerInterface) getContainer(ID, player, tile));
            case InfinityStorageGui.CRAFTING_MONITOR:
                return new GuiCraftingMonitor((ContainerCraftingMonitor) getContainer(ID, player, tile), (TileCraftingMonitor) tile);
            case InfinityStorageGui.WIRELESS_TRANSMITTER:
                return new GuiWirelessTransmitter((ContainerWirelessTransmitter) getContainer(ID, player, tile));
            case InfinityStorageGui.CRAFTER:
                return new GuiCrafter((ContainerCrafter) getContainer(ID, player, tile));
            case InfinityStorageGui.PROCESSING_PATTERN_ENCODER:
                return new GuiProcessingPatternEncoder((ContainerProcessingPatternEncoder) getContainer(ID, player, tile), (TileProcessingPatternEncoder) tile);
            case InfinityStorageGui.GRID_FILTER:
                return new GuiGridFilter(getGridFilterContainer(player, x));
            case InfinityStorageGui.NETWORK_TRANSMITTER:
                return new GuiNetworkTransmitter((ContainerNetworkTransmitter) getContainer(ID, player, tile), (TileNetworkTransmitter) tile);
            case InfinityStorageGui.FLUID_INTERFACE:
                return new GuiFluidInterface((ContainerFluidInterface) getContainer(ID, player, tile));
            case InfinityStorageGui.FLUID_STORAGE:
                return new GuiStorage((ContainerFluidStorage) getContainer(ID, player, tile), (TileFluidStorage) tile);
            default:
                return null;
        }
    }

    private GuiGrid getWirelessGridGui(EntityPlayer player, int hand, int controllerDimension) {
        WirelessGrid grid = getWirelessGrid(player, hand, controllerDimension);

        return new GuiGrid(new ContainerGrid(grid, player), grid);
    }
}
