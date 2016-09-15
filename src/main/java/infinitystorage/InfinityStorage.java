package infinitystorage;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import infinitystorage.proxy.CommonProxy;

@Mod(modid = InfinityStorage.ID, version = InfinityStorage.VERSION, dependencies = InfinityStorage.DEPENDENCIES, guiFactory = "infinitystorage.integration.ingameConfig.GuiFactoryIngameConfig")
public final class InfinityStorage extends InfinityConfig{
    public static final String ID = "infinitystorage";
    public static final String VERSION = "0.2";
    public static final String DEPENDENCIES = "required-after:mcmultipart@[1.2.1,);";
    public Configuration config;

    @SidedProxy(clientSide = "infinitystorage.proxy.ClientProxy", serverSide = "infinitystorage.proxy.ServerProxy")
    public static CommonProxy PROXY;

    @Instance
    public static InfinityStorage INSTANCE;

    public final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(ID);

    public final CreativeTabs tab = new CreativeTabs(ID) {
        @Override
        public ItemStack getIconItemStack() {
            return new ItemStack(InfinityStorageItems.STORAGE_HOUSING);
        }

        @Override
        public Item getTabIconItem() {
            return null;
        }
    };

    static {
        FluidRegistry.enableUniversalBucket();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        config = new Configuration(e.getSuggestedConfigurationFile());

        reloadConfig();

        PROXY.preInit(e);

        config.save();
    }

    @EventHandler
    public void init(FMLInitializationEvent e) {
        PROXY.init(e);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        PROXY.postInit(e);
    }

    public void reloadConfig(){
        InfinityConfig.reloadConfig(config);
        reloadConfig(config);

        if(config.hasChanged())
            config.save();
    }
}
