package mod.adrenix.oseca;

import mod.adrenix.oseca.config.CommonRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Oseca
{
    public static final String MOD_ID = "oseca";
    public static final Logger LOGGER = LogManager.getLogger();
    
    public static void init()
    {
        // Initialize configuration
        CommonRegistry.initializeConfiguration();

        // Let console windows know we're finished
        Oseca.LOGGER.info("Universally Initialized");
    }
}
