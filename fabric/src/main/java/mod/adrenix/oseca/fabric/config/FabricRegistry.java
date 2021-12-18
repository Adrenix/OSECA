package mod.adrenix.oseca.fabric.config;

import mod.adrenix.oseca.config.CommonRegistry;
import mod.adrenix.oseca.config.gui.screen.SettingsScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public abstract class FabricRegistry
{
    /* Configuration Key */
    private static KeyMapping openConfig;

    public static void registerConfigurationKey()
    {
        openConfig = KeyBindingHelper.registerKeyBinding(CommonRegistry.getConfigurationKey());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openConfig.isDown() && !(Minecraft.getInstance().screen instanceof InventoryScreen))
                Minecraft.getInstance().setScreen(new SettingsScreen(Minecraft.getInstance().screen));
        });
    }
}
