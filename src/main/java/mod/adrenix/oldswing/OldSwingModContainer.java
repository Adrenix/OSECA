package mod.adrenix.oldswing;

import com.google.common.eventbus.EventBus;
import mod.adrenix.oldswing.config.ConfigHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.util.Collections;

public class OldSwingModContainer extends DummyModContainer {
    public static final String MODID = "oldswingcore";
    public static final String VERSION = "1.0.0.0";

    public static Logger logger;

    public OldSwingModContainer() {
        super(new ModMetadata());

        ModMetadata meta = getMetadata();
        meta.modId = MODID;
        meta.name = "Old Swing Core";
        meta.description = "Transforms vanilla classes to bring back old swinging animations.";
        meta.version = VERSION;
        meta.authorList = Collections.singletonList("Adrenix");
        meta.credits = "Huge thanks to Vike, for his educational ASM bytecode manipulation tutorials on YouTube.";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);

        return true;
    }


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ConfigHandler());
        MinecraftForge.EVENT_BUS.register(this);

        logger.info("Initialized");
    }
}
