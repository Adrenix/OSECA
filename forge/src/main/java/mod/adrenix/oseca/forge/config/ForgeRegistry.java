package mod.adrenix.oseca.forge.config;

import mod.adrenix.oseca.Oseca;
import mod.adrenix.oseca.config.CommonRegistry;
import mod.adrenix.oseca.config.gui.screen.SettingsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Oseca.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public abstract class ForgeRegistry
{
    /* Configuration Key */
    public static final KeyMapping OPEN_CONFIG = CommonRegistry.getConfigurationKey();

    /* Client setup */
    public static void setup(final FMLClientSetupEvent event)
    {
        // Register config screen
        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () ->
            new ConfigGuiHandler.ConfigGuiFactory(((minecraft, screen) ->
                new SettingsScreen(screen))))
        ;

        // Register and cache config
        CommonRegistry.initializeConfiguration();

        // Register key that opens config while in-game
        ClientRegistry.registerKeyBinding(OPEN_CONFIG);
    }

    /* Subscribe Configuration Key */
    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
            if (OPEN_CONFIG.isDown() && !(Minecraft.getInstance().screen instanceof InventoryScreen))
                Minecraft.getInstance().setScreen(new SettingsScreen(Minecraft.getInstance().screen));
    }
}
