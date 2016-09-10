package infinitystorage;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class InfinityConfig {

    public static boolean channelsEnabled;
    public static int maxChannels;
    public static int channelTimeUpdate;

    public static int controllerBaseUsage;
    public static int cableUsage;
    public static int constructorUsage;
    public static int crafterUsage;
    public static int crafterPerPatternUsage;
    public static int craftingMonitorUsage;
    public static int destructorUsage;
    public static int detectorUsage;
    public static int diskDriveUsage;
    public static int diskDrivePerDiskUsage;
    public static int externalStorageUsage;
    public static int externalStoragePerStorageUsage;
    public static int exporterUsage;
    public static int importerUsage;
    public static int interfaceUsage;
    public static int fluidInterfaceUsage;
    public static int relayUsage;
    public static int soldererUsage;
    public static int storageUsage;
    public static int fluidStorageUsage;
    public static int wirelessTransmitterUsage;
    public static int gridUsage;
    public static int craftingGridUsage;
    public static int patternGridUsage;
    public static int fluidGridUsage;
    public static int networkTransmitterUsage;
    public static float networkTransmitterPerBlockUsage;
    public static int networkReceiverUsage;

    public static int controllerCapacity;
    public static boolean controllerUsesEnergy;

    public static int wirelessTransmitterBaseRange;
    public static int wirelessTransmitterRangePerUpgrade;

    public static boolean wirelessGridUsesEnergy;
    public static int wirelessGridOpenUsage;
    public static int wirelessGridExtractUsage;
    public static int wirelessGridInsertUsage;

    public static int rangeUpgradeUsage;
    public static int speedUpgradeUsage;
    public static int craftingUpgradeUsage;
    public static int stackUpgradeUsage;
    public static int interdimensionalUpgradeUsage;

    public static boolean translucentCables;

    public static void reloadConfig(Configuration config) {
        channelsEnabled = config.getBoolean("channelsEnabled", "channels", true, "Do cables have a specific number of channels?");
        maxChannels = config.getInt("maxChannels", "channels", 8,  1, Integer.MAX_VALUE, "The maximum amount of devices that can connect to a controller");
        channelTimeUpdate = config.getInt("channelTimeUpdate", "channels", 2, 1, Integer.MAX_VALUE, "The amount of channels to update each second NOTE: The higher the number, the more lag it will cause.");;

        controllerBaseUsage = config.getInt("controllerBase", "energy", 0, 0, Integer.MAX_VALUE, "The base energy used by the Controller");
        cableUsage = config.getInt("cable", "energy", 0, 0, Integer.MAX_VALUE, "The energy used by Cables");
        constructorUsage = config.getInt("constructor", "energy", 1, 0, Integer.MAX_VALUE, "The energy used by Constructors");
        crafterUsage = config.getInt("crafter", "energy", 2, 0, Integer.MAX_VALUE, "The base energy used by Crafters");
        crafterPerPatternUsage = config.getInt("crafterPerPattern", "energy", 1, 0, Integer.MAX_VALUE, "The additional energy used per Pattern in a Crafter");
        craftingMonitorUsage = config.getInt("craftingMonitor", "energy", 2, 0, Integer.MAX_VALUE, "The energy used by Crafting Monitors");
        destructorUsage = config.getInt("destructor", "energy", 1, 0, Integer.MAX_VALUE, "The energy used by Destructors");
        detectorUsage = config.getInt("detector", "energy", 2, 0, Integer.MAX_VALUE, "The energy used by Detectors");
        diskDriveUsage = config.getInt("diskDrive", "energy", 0, 0, Integer.MAX_VALUE, "The base energy used by Disk Drives");
        diskDrivePerDiskUsage = config.getInt("diskDrivePerDisk", "energy", 1, 0, Integer.MAX_VALUE, "The additional energy used by Storage Disks in Disk Drives");
        externalStorageUsage = config.getInt("externalStorage", "energy", 0, 0, Integer.MAX_VALUE, "The base energy used by External Storages");
        externalStoragePerStorageUsage = config.getInt("externalStoragePerStorage", "energy", 1, 0, Integer.MAX_VALUE, "The additional energy used per connected storage to an External Storage");
        exporterUsage = config.getInt("exporter", "energy", 1, 0, Integer.MAX_VALUE, "The energy used by Exporters");
        importerUsage = config.getInt("importer", "energy", 1, 0, Integer.MAX_VALUE, "The energy used by Importers");
        interfaceUsage = config.getInt("interface", "energy", 3, 0, Integer.MAX_VALUE, "The energy used by Interfaces");
        fluidInterfaceUsage = config.getInt("fluidInterface", "energy", 3, 0, Integer.MAX_VALUE, "The energy used by Fluid Interfaces");
        relayUsage = config.getInt("relay", "energy", 1, 0, Integer.MAX_VALUE, "The energy used by Relays");
        soldererUsage = config.getInt("solderer", "energy", 3, 0, Integer.MAX_VALUE, "The energy used by Solderers");
        storageUsage = config.getInt("storage", "energy", 1, 0, Integer.MAX_VALUE, "The energy used by Storage Blocks");
        fluidStorageUsage = config.getInt("fluidStorage", "energy", 1, 0, Integer.MAX_VALUE, "The energy used by Fluid Storage Blocks");
        wirelessTransmitterUsage = config.getInt("wirelessTransmitter", "energy", 8, 0, Integer.MAX_VALUE, "The energy used by Wireless Transmitters");
        gridUsage = config.getInt("grid", "energy", 2, 0, Integer.MAX_VALUE, "The energy used by Grids");
        craftingGridUsage = config.getInt("craftingGrid", "energy", 4, 0, Integer.MAX_VALUE, "The energy used by Crafting Grids");
        patternGridUsage = config.getInt("patternGrid", "energy", 3, 0, Integer.MAX_VALUE, "The energy used by Pattern Grids");
        fluidGridUsage = config.getInt("fluidGrid", "energy", 2, 0, Integer.MAX_VALUE, "The energy used by Fluid Grids");
        networkTransmitterUsage = config.getInt("networkTransmitter", "energy", 50, 0, Integer.MAX_VALUE, "The base energy used by Network Transmitters");
        networkTransmitterPerBlockUsage = config.getFloat("networkTransmitterPerBlock", "energy", 4, 0, Float.MAX_VALUE, "The additional energy per block that the Network Transmitter uses, gets rounded up");
        networkReceiverUsage = config.getInt("networkReceiver", "energy", 15, 0, Integer.MAX_VALUE, "The energy used by Network Receivers");

        controllerCapacity = config.getInt("capacity", "controller", 32000, 0, Integer.MAX_VALUE, "The energy capacity of the Controller");
        controllerUsesEnergy = config.getBoolean("usesEnergy", "controller", true, "Whether the Controller uses energy");

        wirelessTransmitterBaseRange = config.getInt("range", "wirelessTransmitter", 16, 0, Integer.MAX_VALUE, "The base range of the Wireless Transmitter");
        wirelessTransmitterRangePerUpgrade = config.getInt("rangePerUpgrade", "wirelessTransmitter", 8, 0, Integer.MAX_VALUE, "The additional range per Range Upgrade in the Wireless Transmitter");

        wirelessGridUsesEnergy = config.getBoolean("usesEnergy", "wirelessGrid", true, "Whether the Wireless Grid uses energy");
        wirelessGridOpenUsage = config.getInt("open", "wirelessGrid", 30, 0, Integer.MAX_VALUE, "The energy used by the Wireless Grid to open");
        wirelessGridInsertUsage = config.getInt("insert", "wirelessGrid", 3, 0, Integer.MAX_VALUE, "The energy used by the Wireless Grid to insert items");
        wirelessGridExtractUsage = config.getInt("extract", "wirelessGrid", 3, 0, Integer.MAX_VALUE, "The energy used by the Wireless Grid to extract items");

        rangeUpgradeUsage = config.getInt("range", "upgrades", 8, 0, Integer.MAX_VALUE, "The additional energy used per Range Upgrade");
        speedUpgradeUsage = config.getInt("speed", "upgrades", 2, 0, Integer.MAX_VALUE, "The additional energy used per Speed Upgrade");
        craftingUpgradeUsage = config.getInt("crafting", "upgrades", 5, 0, Integer.MAX_VALUE, "The additional energy used per Crafting Upgrade");
        stackUpgradeUsage = config.getInt("stack", "upgrades", 12, 0, Integer.MAX_VALUE, "The additional energy used per Stack Upgrade");
        interdimensionalUpgradeUsage = config.getInt("interdimensional", "upgrades", 1000, 0, Integer.MAX_VALUE, "The additional energy used by the Interdimensional Upgrade");

        translucentCables = config.getBoolean("translucentCables", "misc", false, "For resource pack makers that want a translucent cable");
    }
}
