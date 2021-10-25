package mod.adrenix.oldswing;

import mod.adrenix.oldswing.config.ConfigRegistry;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OldSwing implements ModInitializer
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "oldswing";

    @Override
    public void onInitialize()
    {
        // Initialize configuration
        ConfigRegistry.initializeConfiguration();
    }
}