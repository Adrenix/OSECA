package mod.adrenix.oldswing.command;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class ColorUtil
{
    public static String value(String value)
    {
        if (value.equals("true"))
            return format(I18n.get("oldswing.on"), TextFormatting.GREEN);
        else if (value.equals("false"))
            return format(I18n.get("oldswing.off"), TextFormatting.RED);
        return format(value, TextFormatting.YELLOW);
    }

    public static String format(String in, TextFormatting color)
    {
        return (new StringTextComponent(color + in + TextFormatting.RESET)).getString();
    }
}
