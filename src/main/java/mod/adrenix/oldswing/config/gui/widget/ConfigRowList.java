package mod.adrenix.oldswing.config.gui.widget;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import mod.adrenix.oldswing.config.gui.screen.ConfigScreen;
import mod.adrenix.oldswing.config.gui.screen.SwingScreen;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import java.util.List;

public class ConfigRowList extends AbstractOptionList<ConfigRowList.Row>
{
    public ConfigRowList(ConfigScreen parentScreen, List<Configuration> config, int width, int height, int startY, int endY, int itemHeight)
    {
        super(parentScreen.getMinecraft(), width, height, startY, endY, itemHeight);
        this.centerListVertically = false;

        for (Configuration item : config)
            this.addEntry(ConfigRowList.Row.item(parentScreen, item, this.width));
    }

    public void addSmall(AbstractOption first, AbstractOption last)
    {
        this.addEntry(ConfigRowList.Row.small(this.minecraft.options, this.width, first, last));
    }

    public void addSmall(AbstractOption[] buttons)
    {
        if (buttons[0] == null || buttons[1] == null)
            return;
        this.addSmall(buttons[0], buttons[1]);
    }

    public int getRowWidth()
    {
        return 400;
    }

    @Override
    protected int getScrollbarPosition()
    {
        return this.width - 4;
    }

    public static class Row extends AbstractOptionList.Entry<ConfigRowList.Row>
    {
        private final List<Widget> children;

        private Row(List<Widget> list) {
            this.children = list;
        }

        private static Button createInfoTooltip(ConfigScreen screen, int width, List<String> tooltips)
        {
            return new Button(
                width / 2 + 135,
                0,
                20,
                20,
                new StringTextComponent("?"),
                (unused) -> {},
                (button, matrixStack, mouseX, mouseY) ->
                {
                    List<ITextComponent> tips = Lists.newArrayList();
                    for (String line : tooltips)
                        tips.add(new StringTextComponent(line));
                    screen.renderComponentTooltip(matrixStack, tips, mouseX, mouseY);
                }
            );
        }

        private static Button createRangeTooltip(ConfigScreen screen, int width, boolean isGlobal)
        {
            return new Button(
                    width / 2 - 155,
                    0,
                    20,
                    20,
                    new StringTextComponent("#"),
                    (unused) -> {},
                    (button, matrixStack, mouseX, mouseY) -> screen.renderComponentTooltip(matrixStack, SwingScreen.rangeTip(isGlobal), mouseX, mouseY)
            );
        }

        public static ConfigRowList.Row item(ConfigScreen screen, Configuration config, int width)
        {
            List<Widget> widgets = Lists.newArrayList();

            if (config instanceof Configuration.Slider)
            {
                widgets.add(createRangeTooltip(screen, width, ((Configuration.Slider) config).isGlobal));
                widgets.add(((Configuration.Slider) config).create().createButton(screen.getMinecraft().options, width / 2 - 134, 0, 268));
            }
            else if (config instanceof Configuration.Toggle)
                widgets.add(((Configuration.Toggle) config).create().createButton(screen.getMinecraft().options, width / 2 - 155, 0, 289));

            if (config.getTooltip() != null)
                widgets.add(createInfoTooltip(screen, width, config.getTooltip()));

            return new ConfigRowList.Row(widgets);
        }

        public static ConfigRowList.Row small(GameSettings settings, int width, AbstractOption first, AbstractOption last)
        {
            Widget small1 = first.createButton(settings, width / 2 - 155, 0, 150);
            Widget small2 = last.createButton(settings, width / 2 - 155 + 160, 0, 150);
            return new ConfigRowList.Row(ImmutableList.of(small1, small2));
        }

        public void render(@Nonnull MatrixStack matrix, int x, int y, int unused2, int unused3, int unused4, int mouseX, int mouseY, boolean unused5, float partialTicks)
        {
            for (Widget child : this.children)
            {
                child.y = y;
                child.render(matrix, mouseX, mouseY, partialTicks);
            }
        }

        @Nonnull
        public List<? extends IGuiEventListener> children()
        {
            return this.children;
        }
    }
}
