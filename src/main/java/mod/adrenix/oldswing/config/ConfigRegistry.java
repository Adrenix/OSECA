package mod.adrenix.oldswing.config;

import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import mod.adrenix.oldswing.OldSwing;
import mod.adrenix.oldswing.config.gui.screen.SettingsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fmlclient.ConfigGuiHandler;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = OldSwing.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ConfigRegistry
{
    /* Config GUI Key */
    public static final KeyMapping OPEN_CONFIG_GUI = new KeyMapping(
        "key.oldswing.open_config",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_O,
        "category.oldswing.name"
    );

    /* Config caching */
    public static ClientConfig cache;
    public static InteractionResult reload()
    {
        // Retrieve new config and validate its data
        cache = AutoConfig.getConfigHolder(ClientConfig.class).getConfig();
        CustomizedSwings.validateCustomizedSwings();

        // Let a console window know what happened what was loaded
        OldSwing.LOGGER.info(String.format("Config was reloaded with %d items", cache.custom.size()));

        return InteractionResult.SUCCESS;
    }

    /* Client setup */
    public static void setup(final FMLClientSetupEvent event)
    {
        // Register config screen
        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () -> new ConfigGuiHandler.ConfigGuiFactory((minecraft, screen) -> new SettingsScreen(screen)));

        // Register and cache config
        AutoConfig.register(ClientConfig.class, JanksonConfigSerializer::new);
        AutoConfig.getConfigHolder(ClientConfig.class).registerLoadListener((manager, update) -> reload());
        AutoConfig.getConfigHolder(ClientConfig.class).registerSaveListener((manager, data) -> reload());
        reload();

        // Register key that opens config screen while in-game
        ClientRegistry.registerKeyBinding(ConfigRegistry.OPEN_CONFIG_GUI);

        // Let the console window know what was loaded
        OldSwing.LOGGER.info(String.format("Registered %d custom swings.", cache.custom.size()));
    }

    /* Subscribe config key */
    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
            if (OPEN_CONFIG_GUI.isDown())
                Minecraft.getInstance().setScreen(new SettingsScreen(Minecraft.getInstance().screen));
    }
}
