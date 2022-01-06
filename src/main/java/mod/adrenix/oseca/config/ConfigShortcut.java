package mod.adrenix.oseca.config;

import mod.adrenix.oseca.OSECA;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(modid = OSECA.MODID, value = Side.CLIENT)
public abstract class ConfigShortcut
{
    /* Configuration Key */
    public static final KeyBinding OPEN_KEY = new KeyBinding(
        I18n.format("key.oseca.open_config"),
        Keyboard.KEY_O,
        I18n.format("category.oseca.name")
    );

    /* Subscribe Key */
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event)
    {
        if (OPEN_KEY.isKeyDown())
        {
            Minecraft minecraft = Minecraft.getMinecraft();
            minecraft.displayGuiScreen(new GuiConfig(minecraft.currentScreen, OSECA.MODID, OSECA.NAME));
        }
    }
}
