package mod.adrenix.oldswing.config.gui.widget;

import com.google.common.collect.Lists;
import mod.adrenix.oldswing.config.ConfigHandler;
import mod.adrenix.oldswing.config.gui.screen.SwingScreen;
import net.minecraft.client.GameSettings;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class Configuration
{
    public String title;
    private List<String> tooltips;

    public Configuration(String title)
    {
        this.title = title;
    }

    public void setTooltip(String... tooltips)
    {
        this.tooltips = Lists.newArrayList(tooltips);
    }

    @Nullable
    public List<String> getTooltip()
    {
        return this.tooltips;
    }

    public static class Slider extends Configuration
    {
        public final boolean isGlobal;
        private final Function<GameSettings, Double> getValue;
        private final BiConsumer<GameSettings, Double> setValue;
        private final BiFunction<GameSettings, SliderPercentageOption, ITextComponent> setTitle;

        public Slider(String title, boolean isGlobal, Function<GameSettings, Double> getValue, BiConsumer<GameSettings, Double> setValue)
        {
            super(title);
            this.getValue = getValue;
            this.setValue = setValue;
            this.isGlobal = isGlobal;
            this.setTitle = (settings, option) -> new StringTextComponent(title + ": " + SwingScreen.getRangeColor((int) option.get(settings)));
        }

        public SliderPercentageOption create()
        {
            return new SliderPercentageOption(
                this.title,
                isGlobal ? ConfigHandler.GLOBAL : ConfigHandler.MIN,
                ConfigHandler.MAX,
                1.0F,
                this.getValue,
                this.setValue,
                this.setTitle
            );
        }
    }

    public static class Toggle extends Configuration
    {
        private final Predicate<GameSettings> getValue;
        private final BiConsumer<GameSettings, Boolean> setValue;

        public Toggle(String title, Predicate<GameSettings> getValue, BiConsumer<GameSettings, Boolean> setValue)
        {
            super(title);
            this.getValue = getValue;
            this.setValue = setValue;
        }

        public BooleanButton create()
        {
            return new BooleanButton(
                this.title,
                this.getValue,
                this.setValue
            );
        }
    }
}
