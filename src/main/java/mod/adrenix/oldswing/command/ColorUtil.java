package mod.adrenix.oldswing.command;

import net.minecraft.util.text.TextFormatting;

public class ColorUtil {
    public static String value(String value) {
        if (value.equals("true"))
            return format("true", TextFormatting.GREEN);
        else if (value.equals("false"))
            return format("false", TextFormatting.RED);
        return format(value, TextFormatting.YELLOW);
    }

    public static String format(String in, TextFormatting color) {
        return TextFormatting.fromColorIndex(color.getColorIndex()) + in + TextFormatting.fromColorIndex(TextFormatting.RESET.getColorIndex());
    }
}
