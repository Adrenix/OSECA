package mod.adrenix.oldswing;

import mod.adrenix.oldswing.config.ConfigRegistry;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(OldSwing.MOD_ID)
public class OldSwing
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "oldswing";

    public OldSwing()
    {
        // Let everyone know this is a client-side mod
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        // Let the server know this mod isn't going to do anything
        if (FMLLoader.getDist().isDedicatedServer())
            LOGGER.warn("The oldswing mod is client-side only. Please remove it from the server's mod folder.");
        else
            // Set up the config on client startup
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ConfigRegistry::setup);
    }
}
