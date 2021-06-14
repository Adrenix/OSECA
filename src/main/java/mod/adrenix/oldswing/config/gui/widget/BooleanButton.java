package mod.adrenix.oldswing.config.gui.widget;

import net.minecraft.client.GameSettings;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class BooleanButton extends BooleanOption
{
    public BooleanButton(String title, Predicate<GameSettings> predicate, BiConsumer<GameSettings, Boolean> consumer)
    {
        super(title, null, predicate, consumer);
    }

    @Override
    @Nonnull
    public ITextComponent getMessage(@Nonnull GameSettings settings)
    {
        boolean flag = this.get(settings);
        String title = this.getCaption().getString() + ": ";
        TranslationTextComponent state = new TranslationTextComponent(flag ? "options.on.composed" : "options.off.composed");
        return new StringTextComponent(title + (flag ? TextFormatting.GREEN : TextFormatting.RED) + state.getString().replaceAll("\\s?:\\s", ""));
    }
}
