package mod.adrenix.oldswing.config;

import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import mod.adrenix.oldswing.OldSwing;
import mod.adrenix.oldswing.config.gui.screen.SettingsScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import org.lwjgl.glfw.GLFW;

public class ConfigRegistry
{
    /* Configuration Key */
    private static KeyMapping openConfigScreen;

    public static void registerConfigurationKey()
    {
        openConfigScreen = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.oldswing.open_config",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "category.oldswing.name"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openConfigScreen.isDown())
                Minecraft.getInstance().setScreen(new SettingsScreen(Minecraft.getInstance().screen));
        });
    }

    /* Configuration Caching */
    public static ClientConfig cache;
    public static InteractionResult reloadConfiguration()
    {
        // Retrieve new config and validate its data
        cache = AutoConfig.getConfigHolder(ClientConfig.class).getConfig();
        CustomizedSwings.validateCustomizedSwings();

        // Let a console window know what happened what was loaded
        OldSwing.LOGGER.info(String.format("Config was reloaded with %d items", cache.custom.size()));

        return InteractionResult.SUCCESS;
    }

    public static void initializeConfiguration()
    {
        // Register and cache config
        AutoConfig.register(ClientConfig.class, JanksonConfigSerializer::new);
        AutoConfig.getConfigHolder(ClientConfig.class).registerLoadListener((manager, update) -> reloadConfiguration());
        AutoConfig.getConfigHolder(ClientConfig.class).registerSaveListener((manager, data) -> reloadConfiguration());
        reloadConfiguration();

        // Let the console window know what was loaded
        OldSwing.LOGGER.info(String.format("Registered %d custom swings.", cache.custom.size()));
    }
}
