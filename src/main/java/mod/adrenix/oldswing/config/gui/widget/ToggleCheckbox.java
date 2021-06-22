package mod.adrenix.oldswing.config.gui.widget;

import mod.adrenix.oldswing.config.gui.screen.CustomizeScreen;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;

public class ToggleCheckbox extends Checkbox
{
    private final CustomizeScreen screen;

    public ToggleCheckbox(CustomizeScreen screen, int x, int y, int width, int height, Component label, boolean state)
    {
        super(x, y, width, height, label, state, true);
        this.screen = screen;
    }

    @Override
    public void onPress()
    {
        super.onPress();
        this.screen.getMinecraft().setScreen(this.screen);
    }
}
