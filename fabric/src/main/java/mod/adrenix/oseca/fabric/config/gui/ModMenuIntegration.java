package mod.adrenix.oseca.fabric.config.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import mod.adrenix.oseca.config.gui.screen.SettingsScreen;
import net.minecraft.client.Minecraft;

public class ModMenuIntegration implements ModMenuApi
{
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return parent -> new SettingsScreen(Minecraft.getInstance().screen);
    }
}
