package mod.adrenix.oldswing.config.gui.widget;

import mod.adrenix.oldswing.config.ClientConfig;
import mod.adrenix.oldswing.config.CustomizedSwings;
import mod.adrenix.oldswing.config.DefaultConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;

import java.util.Map;

public class SpeedSlider extends AbstractSliderButton
{
    private final Map.Entry<String, Integer> entry;
    private final int MIN = ClientConfig.MIN;
    private final int MAX = ClientConfig.MAX;

    public SpeedSlider(Map.Entry<String, Integer> entry, int x, int y, int width, int height)
    {
        super(x, y, width, height, TextComponent.EMPTY, (double) entry.getValue());
        this.entry = entry;
        this.updateMessage();
        this.setValue(entry.getValue());
    }

    @Override protected void updateMessage()
    {
        ChatFormatting color = entry.getValue() <= DefaultConfig.NEW_SPEED ? ChatFormatting.GOLD : ChatFormatting.GREEN;
        String text = CustomizedSwings.getLocalizedItem(this.entry) + ": " + (this.active ? color : ChatFormatting.GRAY) + entry.getValue().toString();
        this.setMessage(new TextComponent(text));
    }

    @Override protected void applyValue()
    {
        this.entry.setValue((int) (MIN + Math.abs(MAX - MIN) * this.value));
    }

    public void setValue(int value)
    {
        this.value = (Mth.clamp(value, MIN, MAX) - MIN) / (double) Math.abs(MAX - MIN);
    }
}
