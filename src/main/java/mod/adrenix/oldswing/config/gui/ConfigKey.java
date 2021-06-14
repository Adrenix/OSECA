package mod.adrenix.oldswing.config.gui;

import mod.adrenix.oldswing.OldSwing;
import mod.adrenix.oldswing.config.gui.screen.ConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = OldSwing.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ConfigKey
{
    public static final KeyBinding OPEN_GUI = new KeyBinding(
            I18n.get("oldswing.key.open_config"),
            KeyConflictContext.UNIVERSAL,
            InputMappings.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            I18n.get("oldswing.title")
    );

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
            if (OPEN_GUI.isDown())
                Minecraft.getInstance().setScreen(new ConfigScreen(Minecraft.getInstance().screen));
    }
}
