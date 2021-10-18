package mod.adrenix.oldswing.config.gui.widget;

import mod.adrenix.oldswing.config.gui.screen.CustomizeScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;

import java.util.Map;

public class ItemButton extends Button
{
    public CustomizeScreen screen;
    public Map.Entry<String, Integer> entry;

    public ItemButton(CustomizeScreen screen, Map.Entry<String, Integer> entry, int startX, int startY, int width, int height)
    {
        super(startX, startY, width, height, TextComponent.EMPTY, (ignored) -> {});
        this.screen = screen;
        this.entry = entry;
        this.active = false;
    }
}
