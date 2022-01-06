package mod.adrenix.oseca;

import mod.adrenix.oseca.config.ConfigShortcut;
import mod.adrenix.oseca.util.SoundRedirect;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod
(
    modid = OSECA.MODID,
    name = OSECA.NAME,
    version = OSECA.VERSION,
    clientSideOnly = true,
    acceptedMinecraftVersions = OSECA.MC_VERSION
)
public class OSECA
{
    public static final String MODID = "oseca";
    public static final String NAME = "Old Sound, Eye Candy, & Animations";
    public static final String VERSION = "1.2.0";
    public static final String MC_VERSION = "[1.12.2]";

    public static Logger logger;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        ClientRegistry.registerKeyBinding(ConfigShortcut.OPEN_KEY);
        SoundRedirect.registerSounds();

        logger.info("Initialized");
    }
}

