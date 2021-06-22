package mod.adrenix.oldswing;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import mod.adrenix.oldswing.config.ClientConfig;
import mod.adrenix.oldswing.config.CustomizedSwings;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.InteractionResult;

public class OldSwing implements ModInitializer
{
    public static final int OLD_SPEED = 8;
    public static final int NEW_SPEED = 6;
    public static ClientConfig config;

    public static void log(Object any)
    {
        System.out.println("[oldswing] " + any);
    }

    private InteractionResult setConfigCache()
    {
        config = AutoConfig.getConfigHolder(ClientConfig.class).getConfig();
        CustomizedSwings.validateCustomizedSwings();
        log("Configuration reloaded.");
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onInitialize()
    {
        AutoConfig.register(ClientConfig.class, JanksonConfigSerializer::new);
        AutoConfig.getConfigHolder(ClientConfig.class).registerLoadListener((manager, update) -> setConfigCache());
        AutoConfig.getConfigHolder(ClientConfig.class).registerSaveListener((manager, data) -> setConfigCache());
        setConfigCache();
        log("Configuration & Mod Initialized.");
    }
}