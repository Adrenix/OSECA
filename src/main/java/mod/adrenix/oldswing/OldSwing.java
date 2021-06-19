package mod.adrenix.oldswing;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod
(
    modid = OldSwing.MODID,
    name = OldSwing.NAME,
    version = OldSwing.VERSION,
    clientSideOnly = true,
    acceptedMinecraftVersions = OldSwing.MC_VERSION
)
public class OldSwing
{
    public static final String MODID = "oldswing";
    public static final String NAME = "Old Swing";
    public static final String VERSION = "1.1.2";
    public static final String MC_VERSION = "[1.12.2]";

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        ConfigManager.sync(MODID, Config.Type.INSTANCE);
        logger.info("Initialized");
    }
}

