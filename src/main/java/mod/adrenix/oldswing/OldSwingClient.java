package mod.adrenix.oldswing;

import com.mojang.blaze3d.platform.InputConstants;
import mod.adrenix.oldswing.config.gui.screen.SettingsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class OldSwingClient implements ClientModInitializer
{
    private static KeyMapping openConfigScreen;

    @Override
    public void onInitializeClient()
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
}
