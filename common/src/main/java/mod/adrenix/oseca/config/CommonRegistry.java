package mod.adrenix.oseca.config;

import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import mod.adrenix.oseca.Oseca;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.InteractionResult;
import org.lwjgl.glfw.GLFW;

public abstract class CommonRegistry
{
    /* Configuration Key */
    public static KeyMapping getConfigurationKey()
    {
        return new KeyMapping(
            "key.oseca.open_config",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "category.oseca.name"
        );
    }

    /* Configuration Caching */
    public static ClientConfig cache;
    public static InteractionResult reloadConfiguration()
    {
        // Retrieve new config and validate its data
        CommonRegistry.cache = AutoConfig.getConfigHolder(ClientConfig.class).getConfig();
        CustomSwings.validate();

        // Let debugger screens know what happened and what was loaded
        Oseca.LOGGER.info(String.format("Config was reloaded with %d items", cache.custom.size()));

        return InteractionResult.SUCCESS;
    }

    public static void initializeConfiguration()
    {
        // Register and cache config
        AutoConfig.register(ClientConfig.class, JanksonConfigSerializer::new);
        AutoConfig.getConfigHolder(ClientConfig.class).registerLoadListener((manager, update) -> reloadConfiguration());
        AutoConfig.getConfigHolder(ClientConfig.class).registerSaveListener((manager, data) -> reloadConfiguration());
        reloadConfiguration();

        // Let debugger screens know what happened
        Oseca.LOGGER.info(String.format("Registered %d customized swing speeds", cache.custom.size()));
    }
}
