package mod.adrenix.oldswing.config.gui.widget;

import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;

public class ResetOptions {
    public static AbstractOption[] add(Button.IPressable onReset, Button.IPressable onUndo) {
        return new AbstractOption[] { new Reset("r", onReset), new Undo("u", onUndo) };
    }

    private static class Reset extends AbstractOption {
        private final Button.IPressable reset;

        public Reset(String id, Button.IPressable onReset) {
            super(id);
            this.reset = onReset;
        }

        @Nonnull
        public Widget createButton(@Nonnull GameSettings settings, int x, int y, int w) {
            StringTextComponent title = new StringTextComponent(TextFormatting.RED + I18n.get("oldswing.config.reset"));
            return new OptionButton(x, y, w, 20, this, title, this.reset);
        }
    }

    private static class Undo extends AbstractOption {
        private final Button.IPressable undo;

        public Undo(String id, Button.IPressable onUndo) {
            super(id);
            this.undo = onUndo;
        }

        @Nonnull
        public Widget createButton(@Nonnull GameSettings settings, int x, int y, int w) {
            StringTextComponent title = new StringTextComponent(TextFormatting.GREEN + I18n.get("oldswing.config.undo"));
            return new OptionButton(x, y, w, 20, this, title, this.undo);
        }
    }
}
