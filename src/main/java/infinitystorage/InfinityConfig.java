package infinitystorage;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class InfinityConfig {

    public static boolean channelsEnabled;
    public static int maxChannels;
    public static int channelTimeUpdate;

    public static void preInit(Configuration config) {
        channelsEnabled = config.getBoolean("channelsEnabled", "channels", true, "Do cables have a specific number of channels?");
        maxChannels = config.getInt("maxChannels", "channels", 8,  1, Integer.MAX_VALUE, "The maximum amount of devices that can connect to a controller");
        channelTimeUpdate = config.getInt("channelTimeUpdate", "channels", 2, 1, Integer.MAX_VALUE, "The amount of channels to update each second NOTE: The higher the number, the more lag it will cause.");
    }
}
