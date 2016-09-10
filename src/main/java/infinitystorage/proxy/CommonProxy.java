package infinitystorage.proxy;

import infinitystorage.InfinityConfig;
import infinitystorage.integration.ingameConfig.IngameConfigEventHandler;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import infinitystorage.InfinityStorage;
import infinitystorage.InfinityStorageBlocks;
import infinitystorage.InfinityStorageItems;
import infinitystorage.api.InfinityStorageAPI;
import infinitystorage.apiimpl.autocrafting.registry.CraftingTaskFactoryNormal;
import infinitystorage.apiimpl.autocrafting.registry.CraftingTaskFactoryProcessing;
import infinitystorage.apiimpl.autocrafting.registry.CraftingTaskRegistry;
import infinitystorage.apiimpl.solderer.*;
import infinitystorage.apiimpl.storage.fluid.FluidStorageNBT;
import infinitystorage.apiimpl.storage.item.ItemStorageNBT;
import infinitystorage.block.*;
import infinitystorage.gui.GuiHandler;
import infinitystorage.integration.craftingtweaks.IntegrationCraftingTweaks;
import infinitystorage.item.*;
import infinitystorage.network.*;
import infinitystorage.tile.*;
import infinitystorage.tile.data.ContainerListener;
import infinitystorage.tile.data.TileDataManager;
import infinitystorage.tile.externalstorage.TileExternalStorage;
import infinitystorage.tile.grid.TileGrid;

import java.util.ArrayList;
import java.util.List;

public class CommonProxy {
    protected List<BlockCable> cableTypes = new ArrayList<>();

    public void preInit(FMLPreInitializationEvent e) {
        if (IntegrationCraftingTweaks.isLoaded()) {
            IntegrationCraftingTweaks.register();
        }

        InfinityStorageAPI.instance().getCraftingTaskRegistry().addFactory(CraftingTaskFactoryNormal.ID, new CraftingTaskFactoryNormal());
        InfinityStorageAPI.instance().getCraftingTaskRegistry().addFactory(CraftingTaskFactoryProcessing.ID, new CraftingTaskFactoryProcessing());

        int id = 0;

        InfinityStorage.INSTANCE.network.registerMessage(MessageTileDataParameter.class, MessageTileDataParameter.class, id++, Side.CLIENT);
        InfinityStorage.INSTANCE.network.registerMessage(MessageTileDataParameterUpdate.class, MessageTileDataParameterUpdate.class, id++, Side.SERVER);
        InfinityStorage.INSTANCE.network.registerMessage(MessageGridItemInsertHeld.class, MessageGridItemInsertHeld.class, id++, Side.SERVER);
        InfinityStorage.INSTANCE.network.registerMessage(MessageGridItemPull.class, MessageGridItemPull.class, id++, Side.SERVER);
        InfinityStorage.INSTANCE.network.registerMessage(MessageGridCraftingClear.class, MessageGridCraftingClear.class, id++, Side.SERVER);
        InfinityStorage.INSTANCE.network.registerMessage(MessageGridCraftingTransfer.class, MessageGridCraftingTransfer.class, id++, Side.SERVER);
        InfinityStorage.INSTANCE.network.registerMessage(MessageWirelessGridSettingsUpdate.class, MessageWirelessGridSettingsUpdate.class, id++, Side.SERVER);
        InfinityStorage.INSTANCE.network.registerMessage(MessageGridCraftingStart.class, MessageGridCraftingStart.class, id++, Side.SERVER);
        InfinityStorage.INSTANCE.network.registerMessage(MessageGridPatternCreate.class, MessageGridPatternCreate.class, id++, Side.SERVER);
        InfinityStorage.INSTANCE.network.registerMessage(MessageCraftingMonitorCancel.class, MessageCraftingMonitorCancel.class, id++, Side.SERVER);
        InfinityStorage.INSTANCE.network.registerMessage(MessageGridItemUpdate.class, MessageGridItemUpdate.class, id++, Side.CLIENT);
        InfinityStorage.INSTANCE.network.registerMessage(MessageGridItemDelta.class, MessageGridItemDelta.class, id++, Side.CLIENT);
        InfinityStorage.INSTANCE.network.registerMessage(MessageGridFluidUpdate.class, MessageGridFluidUpdate.class, id++, Side.CLIENT);
        InfinityStorage.INSTANCE.network.registerMessage(MessageGridFluidDelta.class, MessageGridFluidDelta.class, id++, Side.CLIENT);
        InfinityStorage.INSTANCE.network.registerMessage(MessageGridFluidPull.class, MessageGridFluidPull.class, id++, Side.SERVER);
        InfinityStorage.INSTANCE.network.registerMessage(MessageGridFluidInsertHeld.class, MessageGridFluidInsertHeld.class, id++, Side.SERVER);
        InfinityStorage.INSTANCE.network.registerMessage(MessageProcessingPatternEncoderClear.class, MessageProcessingPatternEncoderClear.class, id++, Side.SERVER);
        InfinityStorage.INSTANCE.network.registerMessage(MessageGridFilterUpdate.class, MessageGridFilterUpdate.class, id++, Side.SERVER);

        NetworkRegistry.INSTANCE.registerGuiHandler(InfinityStorage.INSTANCE, new GuiHandler());

        MinecraftForge.EVENT_BUS.register(new ContainerListener());

        registerTile(TileController.class, "controller");
        registerTile(TileGrid.class, "grid");
        registerTile(TileDiskDrive.class, "disk_drive");
        registerTile(TileExternalStorage.class, "external_storage");
        registerTile(TileImporter.class, "importer");
        registerTile(TileExporter.class, "exporter");
        registerTile(TileDetector.class, "detector");
        registerTile(TileSolderer.class, "solderer");
        registerTile(TileDestructor.class, "destructor");
        registerTile(TileConstructor.class, "constructor");
        registerTile(TileStorage.class, "storage");
        registerTile(TileRelay.class, "relay");
        registerTile(TileInterface.class, "interface");
        registerTile(TileCraftingMonitor.class, "crafting_monitor");
        registerTile(TileWirelessTransmitter.class, "wireless_transmitter");
        registerTile(TileCrafter.class, "crafter");
        registerTile(TileProcessingPatternEncoder.class, "processing_pattern_encoder");
        registerTile(TileCable.class, "cable");
        registerTile(TileNetworkReceiver.class, "network_receiver");
        registerTile(TileNetworkTransmitter.class, "network_transmitter");
        registerTile(TileFluidInterface.class, "fluid_interface");
        registerTile(TileFluidStorage.class, "fluid_storage");

        registerBlock(InfinityStorageBlocks.CONTROLLER);
        registerBlock(InfinityStorageBlocks.GRID);
        registerBlock(InfinityStorageBlocks.CRAFTING_MONITOR);
        registerBlock(InfinityStorageBlocks.CRAFTER);
        registerBlock(InfinityStorageBlocks.PROCESSING_PATTERN_ENCODER);
        registerBlock(InfinityStorageBlocks.DISK_DRIVE);
        registerBlock(InfinityStorageBlocks.STORAGE);
        registerBlock(InfinityStorageBlocks.FLUID_STORAGE);
        registerBlock(InfinityStorageBlocks.SOLDERER);
        registerBlock(InfinityStorageBlocks.CABLE);
        registerBlock(InfinityStorageBlocks.IMPORTER);
        registerBlock(InfinityStorageBlocks.EXPORTER);
        registerBlock(InfinityStorageBlocks.EXTERNAL_STORAGE);
        registerBlock(InfinityStorageBlocks.CONSTRUCTOR);
        registerBlock(InfinityStorageBlocks.DESTRUCTOR);
        registerBlock(InfinityStorageBlocks.DETECTOR);
        registerBlock(InfinityStorageBlocks.RELAY);
        registerBlock(InfinityStorageBlocks.INTERFACE);
        registerBlock(InfinityStorageBlocks.FLUID_INTERFACE);
        registerBlock(InfinityStorageBlocks.WIRELESS_TRANSMITTER);
        registerBlock(InfinityStorageBlocks.MACHINE_CASING);
        registerBlock(InfinityStorageBlocks.NETWORK_TRANSMITTER);
        registerBlock(InfinityStorageBlocks.NETWORK_RECEIVER);

        registerItem(InfinityStorageItems.QUARTZ_ENRICHED_IRON);
        registerItem(InfinityStorageItems.STORAGE_DISK);
        registerItem(InfinityStorageItems.FLUID_STORAGE_DISK);
        registerItem(InfinityStorageItems.STORAGE_HOUSING);
        registerItem(InfinityStorageItems.PATTERN);
        registerItem(InfinityStorageItems.STORAGE_PART);
        registerItem(InfinityStorageItems.FLUID_STORAGE_PART);
        registerItem(InfinityStorageItems.WIRELESS_GRID);
        registerItem(InfinityStorageItems.PROCESSOR);
        registerItem(InfinityStorageItems.CORE);
        registerItem(InfinityStorageItems.SILICON);
        registerItem(InfinityStorageItems.UPGRADE);
        registerItem(InfinityStorageItems.GRID_FILTER);
        registerItem(InfinityStorageItems.NETWORK_CARD);
        if(InfinityStorage.channelsEnabled)
            registerItem(InfinityStorageItems.NETWORK_TOOL);

        OreDictionary.registerOre("itemSilicon", InfinityStorageItems.SILICON);

        // Processors
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_BASIC));
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_IMPROVED));
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_ADVANCED));
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipePrintedProcessor(ItemProcessor.TYPE_PRINTED_SILICON));

        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipeProcessor(ItemProcessor.TYPE_BASIC));
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipeProcessor(ItemProcessor.TYPE_IMPROVED));
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipeProcessor(ItemProcessor.TYPE_ADVANCED));

        // Silicon
        GameRegistry.addSmelting(Items.QUARTZ, new ItemStack(InfinityStorageItems.SILICON), 0.5f);

        // Quartz Enriched Iron
        GameRegistry.addRecipe(new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON, 4),
            "II",
            "IQ",
            'I', new ItemStack(Items.IRON_INGOT),
            'Q', new ItemStack(Items.QUARTZ)
        );

        // Machine Casing
        GameRegistry.addRecipe(new ItemStack(InfinityStorageBlocks.MACHINE_CASING),
            "EEE",
            "E E",
            "EEE",
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON)
        );

        // Construction Core
        GameRegistry.addShapelessRecipe(new ItemStack(InfinityStorageItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            new ItemStack(Items.GLOWSTONE_DUST)
        );

        // Destruction Core
        GameRegistry.addShapelessRecipe(new ItemStack(InfinityStorageItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            new ItemStack(Items.QUARTZ)
        );

        // Relay
        GameRegistry.addShapelessRecipe(new ItemStack(InfinityStorageBlocks.RELAY),
            new ItemStack(InfinityStorageBlocks.MACHINE_CASING),
            new ItemStack(InfinityStorageBlocks.CABLE),
            new ItemStack(Blocks.REDSTONE_TORCH)
        );

        // Controller
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(InfinityStorageBlocks.CONTROLLER, 1, EnumControllerType.NORMAL.getId()),
            "EDE",
            "SMS",
            "ESE",
            'D', new ItemStack(Items.DIAMOND),
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'M', new ItemStack(InfinityStorageBlocks.MACHINE_CASING),
            'S', "itemSilicon"
        ));

        // Solderer
        GameRegistry.addRecipe(new ItemStack(InfinityStorageBlocks.SOLDERER),
            "ESE",
            "E E",
            "ESE",
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'S', new ItemStack(Blocks.STICKY_PISTON)
        );

        // Disk Drive
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipeBasic(
            new ItemStack(InfinityStorageBlocks.DISK_DRIVE),
            500,
            new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            new ItemStack(InfinityStorageBlocks.MACHINE_CASING),
            new ItemStack(Blocks.CHEST)
        ));

        // Cable
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(InfinityStorageBlocks.CABLE, 12),
            "EEE",
            "GRG",
            "EEE",
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'G', "blockGlass",
            'R', new ItemStack(Items.REDSTONE)
        ));

        // Wireless Transmitter
        GameRegistry.addRecipe(new ItemStack(InfinityStorageBlocks.WIRELESS_TRANSMITTER),
            "EPE",
            "EME",
            "EAE",
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'A', new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            'P', new ItemStack(Items.ENDER_PEARL),
            'M', new ItemStack(InfinityStorageBlocks.MACHINE_CASING)
        );

        // Grid
        GameRegistry.addRecipe(new ItemStack(InfinityStorageBlocks.GRID, 1, EnumGridType.NORMAL.getId()),
            "ECE",
            "PMP",
            "EDE",
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED),
            'C', new ItemStack(InfinityStorageItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            'D', new ItemStack(InfinityStorageItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            'M', new ItemStack(InfinityStorageBlocks.MACHINE_CASING)
        );

        // Crafting Grid
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipeBasic(
            new ItemStack(InfinityStorageBlocks.GRID, 1, EnumGridType.CRAFTING.getId()),
            500,
            new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            new ItemStack(InfinityStorageBlocks.GRID, 1, EnumGridType.NORMAL.getId()),
            new ItemStack(Blocks.CRAFTING_TABLE)
        ));

        // Pattern Grid
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipeBasic(
            new ItemStack(InfinityStorageBlocks.GRID, 1, EnumGridType.PATTERN.getId()),
            500,
            new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            new ItemStack(InfinityStorageBlocks.GRID, 1, EnumGridType.NORMAL.getId()),
            new ItemStack(InfinityStorageItems.PATTERN)
        ));

        // Fluid Grid
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipeBasic(
            new ItemStack(InfinityStorageBlocks.GRID, 1, EnumGridType.FLUID.getId()),
            500,
            new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            new ItemStack(InfinityStorageBlocks.GRID, 1, EnumGridType.NORMAL.getId()),
            new ItemStack(Items.BUCKET)
        ));

        // Wireless Grid
        GameRegistry.addRecipe(new ItemStack(InfinityStorageItems.WIRELESS_GRID, 1, ItemWirelessGrid.TYPE_NORMAL),
            "EPE",
            "EAE",
            "EEE",
            'P', new ItemStack(Items.ENDER_PEARL),
            'A', new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON)
        );

        // Crafter
        GameRegistry.addRecipe(new ItemStack(InfinityStorageBlocks.CRAFTER),
            "ECE",
            "AMA",
            "EDE",
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'A', new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            'C', new ItemStack(InfinityStorageItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            'D', new ItemStack(InfinityStorageItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            'M', new ItemStack(InfinityStorageBlocks.MACHINE_CASING)
        );

        // Processing Pattern Encoder
        GameRegistry.addRecipe(new ItemStack(InfinityStorageBlocks.PROCESSING_PATTERN_ENCODER),
            "ECE",
            "PMP",
            "EFE",
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'M', new ItemStack(InfinityStorageBlocks.MACHINE_CASING),
            'P', new ItemStack(InfinityStorageItems.PATTERN),
            'C', new ItemStack(Blocks.CRAFTING_TABLE),
            'F', new ItemStack(Blocks.FURNACE)
        );

        // External Storage
        GameRegistry.addRecipe(new ItemStack(InfinityStorageBlocks.EXTERNAL_STORAGE),
            "CED",
            "HMH",
            "EPE",
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'H', new ItemStack(Blocks.CHEST),
            'C', new ItemStack(InfinityStorageItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            'D', new ItemStack(InfinityStorageItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            'M', new ItemStack(InfinityStorageBlocks.CABLE),
            'P', new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        );

        // Importer
        GameRegistry.addShapelessRecipe(new ItemStack(InfinityStorageBlocks.IMPORTER),
            new ItemStack(InfinityStorageBlocks.CABLE),
            new ItemStack(InfinityStorageItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        );

        // Exporter
        GameRegistry.addShapelessRecipe(new ItemStack(InfinityStorageBlocks.EXPORTER),
            new ItemStack(InfinityStorageBlocks.CABLE),
            new ItemStack(InfinityStorageItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        );

        // Destructor
        GameRegistry.addShapedRecipe(new ItemStack(InfinityStorageBlocks.DESTRUCTOR),
            "EDE",
            "RMR",
            "EIE",
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'D', new ItemStack(InfinityStorageItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            'R', new ItemStack(Items.REDSTONE),
            'M', new ItemStack(InfinityStorageBlocks.CABLE),
            'I', new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        );

        // Constructor
        GameRegistry.addShapedRecipe(new ItemStack(InfinityStorageBlocks.CONSTRUCTOR),
            "ECE",
            "RMR",
            "EIE",
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'C', new ItemStack(InfinityStorageItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            'R', new ItemStack(Items.REDSTONE),
            'M', new ItemStack(InfinityStorageBlocks.CABLE),
            'I', new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        );

        // Detector
        GameRegistry.addRecipe(new ItemStack(InfinityStorageBlocks.DETECTOR),
            "ECE",
            "RMR",
            "EPE",
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'R', new ItemStack(Items.REDSTONE),
            'C', new ItemStack(Items.COMPARATOR),
            'M', new ItemStack(InfinityStorageBlocks.MACHINE_CASING),
            'P', new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        );

        // Storage Parts
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(InfinityStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_1K),
            "SES",
            "GRG",
            "SGS",
            'R', new ItemStack(Items.REDSTONE),
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'S', "itemSilicon",
            'G', "blockGlass"
        ));

        GameRegistry.addRecipe(new ItemStack(InfinityStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_4K),
            "PEP",
            "SRS",
            "PSP",
            'R', new ItemStack(Items.REDSTONE),
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            'S', new ItemStack(InfinityStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_1K)
        );

        GameRegistry.addRecipe(new ItemStack(InfinityStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_16K),
            "PEP",
            "SRS",
            "PSP",
            'R', new ItemStack(Items.REDSTONE),
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED),
            'S', new ItemStack(InfinityStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_4K)
        );

        GameRegistry.addRecipe(new ItemStack(InfinityStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_64K),
            "PEP",
            "SRS",
            "PSP",
            'R', new ItemStack(Items.REDSTONE),
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            'S', new ItemStack(InfinityStorageItems.STORAGE_PART, 1, ItemStoragePart.TYPE_16K)
        );

        // Fluid Storage Parts
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(InfinityStorageItems.FLUID_STORAGE_PART, 1, ItemFluidStoragePart.TYPE_64K),
            "SES",
            "GRG",
            "SGS",
            'R', new ItemStack(Items.BUCKET),
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'S', "itemSilicon",
            'G', "blockGlass"
        ));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(InfinityStorageItems.FLUID_STORAGE_PART, 1, ItemFluidStoragePart.TYPE_128K),
            "PEP",
            "SRS",
            "PSP",
            'R', new ItemStack(Items.BUCKET),
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            'S', new ItemStack(InfinityStorageItems.FLUID_STORAGE_PART, 1, ItemFluidStoragePart.TYPE_64K)
        ));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(InfinityStorageItems.FLUID_STORAGE_PART, 1, ItemFluidStoragePart.TYPE_256K),
            "PEP",
            "SRS",
            "PSP",
            'R', new ItemStack(Items.BUCKET),
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED),
            'S', new ItemStack(InfinityStorageItems.FLUID_STORAGE_PART, 1, ItemFluidStoragePart.TYPE_128K)
        ));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(InfinityStorageItems.FLUID_STORAGE_PART, 1, ItemFluidStoragePart.TYPE_512K),
            "PEP",
            "SRS",
            "PSP",
            'R', new ItemStack(Items.BUCKET),
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED),
            'S', new ItemStack(InfinityStorageItems.FLUID_STORAGE_PART, 1, ItemFluidStoragePart.TYPE_256K)
        ));

        // Storage Housing
        GameRegistry.addRecipe(new ShapedOreRecipe(ItemStorageNBT.createStackWithNBT(new ItemStack(InfinityStorageItems.STORAGE_HOUSING)),
            "GRG",
            "R R",
            "EEE",
            'G', "blockGlass",
            'R', new ItemStack(Items.REDSTONE),
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON)
        ));

        // Storage Disks
        for (int type = 0; type <= 3; ++type) {
            ItemStack disk = ItemStorageNBT.createStackWithNBT(new ItemStack(InfinityStorageItems.STORAGE_DISK, 1, type));

            GameRegistry.addRecipe(new ShapedOreRecipe(disk,
                "GRG",
                "RPR",
                "EEE",
                'G', "blockGlass",
                'R', new ItemStack(Items.REDSTONE),
                'P', new ItemStack(InfinityStorageItems.STORAGE_PART, 1, type),
                'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON)
            ));

            GameRegistry.addShapelessRecipe(disk,
                new ItemStack(InfinityStorageItems.STORAGE_HOUSING),
                new ItemStack(InfinityStorageItems.STORAGE_PART, 1, type)
            );
        }

        // Fluid Storage Disks
        for (int type = 0; type <= 3; ++type) {
            ItemStack disk = FluidStorageNBT.createStackWithNBT(new ItemStack(InfinityStorageItems.FLUID_STORAGE_DISK, 1, type));

            GameRegistry.addRecipe(new ShapedOreRecipe(disk,
                "GRG",
                "RPR",
                "EEE",
                'G', "blockGlass",
                'R', new ItemStack(Items.REDSTONE),
                'P', new ItemStack(InfinityStorageItems.FLUID_STORAGE_PART, 1, type),
                'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON)
            ));

            GameRegistry.addShapelessRecipe(disk,
                new ItemStack(InfinityStorageItems.STORAGE_HOUSING),
                new ItemStack(InfinityStorageItems.FLUID_STORAGE_PART, 1, type)
            );
        }

        // Pattern
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(InfinityStorageItems.PATTERN),
            "GRG",
            "RGR",
            "EEE",
            'G', "blockGlass",
            'R', new ItemStack(Items.REDSTONE),
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON)
        ));

        // Upgrade
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(InfinityStorageItems.UPGRADE, 1, 0),
            "EGE",
            "EPE",
            "EGE",
            'G', "blockGlass",
            'P', new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED),
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON)
        ));

        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipeUpgrade(ItemUpgrade.TYPE_RANGE));
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipeUpgrade(ItemUpgrade.TYPE_SPEED));
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipeUpgrade(ItemUpgrade.TYPE_INTERDIMENSIONAL));
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipeUpgrade(ItemUpgrade.TYPE_CRAFTING));

        GameRegistry.addShapedRecipe(new ItemStack(InfinityStorageItems.UPGRADE, 1, ItemUpgrade.TYPE_STACK),
            "USU",
            "SUS",
            "USU",
            'U', new ItemStack(Items.SUGAR),
            'S', new ItemStack(InfinityStorageItems.UPGRADE, 1, ItemUpgrade.TYPE_SPEED)
        );

        // Storage Blocks
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipeStorage(EnumItemStorageType.TYPE_1K, ItemStoragePart.TYPE_1K));
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipeStorage(EnumItemStorageType.TYPE_4K, ItemStoragePart.TYPE_4K));
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipeStorage(EnumItemStorageType.TYPE_16K, ItemStoragePart.TYPE_16K));
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipeStorage(EnumItemStorageType.TYPE_64K, ItemStoragePart.TYPE_64K));

        // Fluid Storage Blocks
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipeFluidStorage(EnumFluidStorageType.TYPE_64K, ItemFluidStoragePart.TYPE_64K));
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipeFluidStorage(EnumFluidStorageType.TYPE_128K, ItemFluidStoragePart.TYPE_128K));
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipeFluidStorage(EnumFluidStorageType.TYPE_256K, ItemFluidStoragePart.TYPE_256K));
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipeFluidStorage(EnumFluidStorageType.TYPE_512K, ItemFluidStoragePart.TYPE_512K));

        // Crafting Monitor
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(InfinityStorageBlocks.CRAFTING_MONITOR),
            "EGE",
            "GMG",
            "EPE",
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'M', new ItemStack(InfinityStorageBlocks.MACHINE_CASING),
            'G', "blockGlass",
            'P', new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_IMPROVED)
        ));

        // Interface
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipeBasic(
            new ItemStack(InfinityStorageBlocks.INTERFACE),
            200,
            new ItemStack(InfinityStorageBlocks.IMPORTER),
            new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_BASIC),
            new ItemStack(InfinityStorageBlocks.EXPORTER)
        ));

        // Fluid Interface
        InfinityStorageAPI.instance().getSoldererRegistry().addRecipe(new SoldererRecipeBasic(
            new ItemStack(InfinityStorageBlocks.FLUID_INTERFACE),
            200,
            new ItemStack(Items.BUCKET),
            new ItemStack(InfinityStorageBlocks.INTERFACE),
            new ItemStack(Items.BUCKET)
        ));

        // Grid Filter
        GameRegistry.addShapedRecipe(new ItemStack(InfinityStorageItems.GRID_FILTER),
            "EPE",
            "PHP",
            "EPE",
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(Items.PAPER),
            'H', new ItemStack(Blocks.HOPPER)
        );

        // Network Card
        GameRegistry.addShapedRecipe(new ItemStack(InfinityStorageItems.NETWORK_CARD),
            "EEE",
            "PAP",
            "EEE",
            'E', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON),
            'P', new ItemStack(Items.PAPER),
            'A', new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED)
        );

        // Network Transmitter
        GameRegistry.addShapedRecipe(new ItemStack(InfinityStorageBlocks.NETWORK_TRANSMITTER),
            "EEE",
            "CMD",
            "AAA",
            'E', new ItemStack(Items.ENDER_PEARL),
            'C', new ItemStack(InfinityStorageItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            'M', new ItemStack(InfinityStorageBlocks.MACHINE_CASING),
            'D', new ItemStack(InfinityStorageItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            'A', new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED)
        );

        // Network Receiver
        GameRegistry.addShapedRecipe(new ItemStack(InfinityStorageBlocks.NETWORK_RECEIVER),
            "AAA",
            "CMD",
            "EEE",
            'E', new ItemStack(Items.ENDER_PEARL),
            'C', new ItemStack(InfinityStorageItems.CORE, 1, ItemCore.TYPE_CONSTRUCTION),
            'M', new ItemStack(InfinityStorageBlocks.MACHINE_CASING),
            'D', new ItemStack(InfinityStorageItems.CORE, 1, ItemCore.TYPE_DESTRUCTION),
            'A', new ItemStack(InfinityStorageItems.PROCESSOR, 1, ItemProcessor.TYPE_ADVANCED)
        );

        // Network Tool
        GameRegistry.addShapedRecipe(new ItemStack(InfinityStorageItems.NETWORK_TOOL),
                "I I",
                " I ",
                " I ",
                'I', new ItemStack(InfinityStorageItems.QUARTZ_ENRICHED_IRON));
    }

    public void init(FMLInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new IngameConfigEventHandler());
    }

    public void postInit(FMLPostInitializationEvent e) {
        // NO OP
    }

    private void registerBlock(BlockBase block) {
        GameRegistry.<Block>register(block);
        GameRegistry.register(block.createItem());
    }

    private void registerBlock(BlockCable cable) {
        GameRegistry.<Block>register(cable);
        GameRegistry.register(new ItemBlockBase(cable, cable.getPlacementType(), false));

        cableTypes.add(cable);
    }

    private void registerTile(Class<? extends TileBase> tile, String id) {
        GameRegistry.registerTileEntity(tile, InfinityStorage.ID + ":" + id);

        try {
            TileBase tileInstance = tile.newInstance();

            tileInstance.getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void registerItem(Item item) {
        GameRegistry.register(item);
    }
}
