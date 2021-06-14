package mod.adrenix.oldswing;

import mod.adrenix.oldswing.command.CommandRegistry;
import mod.adrenix.oldswing.config.ClientConfig;
import mod.adrenix.oldswing.config.ConfigHandler;
import mod.adrenix.oldswing.config.gui.screen.ConfigScreen;
import mod.adrenix.oldswing.config.gui.ConfigKey;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(OldSwing.MOD_ID)
public class OldSwing
{
    public static final String MOD_ID = "oldswing";
    public static final Logger LOGGER = LogManager.getLogger();

    public OldSwing()
    {
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        if (FMLLoader.getDist().isDedicatedServer())
            return;

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHandler.CLIENT_SPEC);
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, screen) -> new ConfigScreen(screen));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::configSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public void configSetup(final FMLClientSetupEvent event)
    {
        ClientConfig.loadCustomItems();
        ClientRegistry.registerKeyBinding(ConfigKey.OPEN_GUI);
    }

    @SubscribeEvent
    public void serverStarting(FMLServerStartingEvent event)
    {
        CommandRegistry.register(event.getServer().getCommands().getDispatcher());
    }
}
