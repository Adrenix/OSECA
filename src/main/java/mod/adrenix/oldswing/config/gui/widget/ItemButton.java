package mod.adrenix.oldswing.config.gui.widget;

import com.electronwill.nightconfig.core.Config;
import mod.adrenix.oldswing.config.gui.screen.CustomScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public class ItemButton extends Button {
    public CustomScreen parentScreen;
    public Config.Entry entry;

    public ItemButton(CustomScreen parentScreen, Config.Entry entry, int startX, int startY, int width, int height) {
        super(startX, startY, width, height, new StringTextComponent(" "), (unused) -> {});
        this.parentScreen = parentScreen;
        this.entry = entry;
    }
}
