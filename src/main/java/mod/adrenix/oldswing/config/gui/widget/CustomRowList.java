package mod.adrenix.oldswing.config.gui.widget;

import com.electronwill.nightconfig.core.Config;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import mod.adrenix.oldswing.config.ConfigHandler;
import mod.adrenix.oldswing.config.CustomSwing;
import mod.adrenix.oldswing.config.gui.screen.CustomScreen;
import mod.adrenix.oldswing.config.gui.screen.SwingScreen;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.List;

public class CustomRowList extends AbstractOptionList<CustomRowList.Row> {
    public static final List<Config.Entry> deleted = Lists.newArrayList();
    public static Config.Entry added;
    private final CustomScreen parentScreen;

    public CustomRowList(CustomScreen parentScreen, int width, int height, int y0, int y1, int itemHeight) {
        // x0 = 0 & x1 = width
        super(parentScreen.getMinecraft(), width, height, y0, y1, itemHeight);
        this.centerListVertically = false;
        this.parentScreen = parentScreen;
    }

    public void addItem(Config.Entry entry) {
        this.addEntry(CustomRowList.Row.item(parentScreen, entry, this.width));
    }

    public int getRowWidth() {
        return 400;
    }

    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 36;
    }

    public static class Row extends AbstractOptionList.Entry<CustomRowList.Row> {
        private final List<Widget> children;

        private Row(List<Widget> list) {
            this.children = list;
        }

        public static void disableWidgets(List<Widget> widgets) {
            widgets.forEach((widget) -> {
                if (widget instanceof AbstractSlider)
                    widget.setMessage(new StringTextComponent(TextFormatting.GRAY + TextFormatting.stripFormatting(widget.getMessage().getString())));
                widget.active = false;
            });

            widgets.get(widgets.size() - 1).active = true;
        }

        public static void enableWidgets(List<Widget> widgets, Config.Entry entry) {
            widgets.forEach((widget) -> {
                if (widget instanceof AbstractSlider)
                    widget.setMessage(getSliderTitle(entry));
                widget.active = true;
            });

            widgets.get(widgets.size() - 1).active = false;
        }

        private static StringTextComponent getSliderTitle(Config.Entry entry) {
            String registryName = CustomSwing.getRegistryName(CustomSwing.key(entry));
            return new StringTextComponent(registryName + ": " + SwingScreen.getRangeColor(entry.getValue()));
        }

        private static StringTextComponent getRemoveButtonTitle(TextFormatting color) {
            return new StringTextComponent(color + (TextFormatting.BOLD + "\u274c"));
        }

        private static StringTextComponent getUndoButtonTitle(TextFormatting color) {
            return new StringTextComponent(color + "\u2764");
        }

        public static CustomRowList.Row item(CustomScreen screen, Config.Entry entry, int width) {
            List<Widget> widgets = Lists.newArrayList();
            String key = CustomSwing.key(entry);
            String registryName = CustomSwing.getRegistryName(key);
            Widget rangeTipButton;
            Widget imageTipButton;
            Widget valueSlider;
            Widget remove;
            Widget undo;

            rangeTipButton = new Button(
                    width / 2 - 155,
                    0,
                    20,
                    20,
                    new StringTextComponent(entry.equals(CustomRowList.added) ? TextFormatting.YELLOW + "#" : "#"),
                    (unused) -> {},
                    (button, matrixStack, mouseX, mouseY) -> {
                        if (screen.suggestionsAreClosed())
                            screen.renderComponentTooltip(matrixStack, SwingScreen.rangeTip(), mouseX, mouseY);
                    }
            );

            imageTipButton = new ItemButton(screen, entry, width / 2 - 134, 0, 20, 20);
            valueSlider = (new SliderPercentageOption(
                    registryName,
                    ConfigHandler.MIN,
                    ConfigHandler.MAX,
                    1.0F,
                    unused -> (double) entry.getInt(),
                    (unused, value) -> entry.setValue(value.intValue()),
                    (settings, option) -> getSliderTitle(entry)
            )).createButton(screen.getMinecraft().options, width / 2 - 113, 0, 228);

            StringTextComponent notify = new StringTextComponent(TextFormatting.RED + I18n.get("oldswing.config.custom_removal", TextFormatting.WHITE + registryName + TextFormatting.RED));
            remove = new Button(
                    width / 2 + 116,
                    0,
                    20,
                    20,
                    getRemoveButtonTitle(TextFormatting.DARK_RED),
                    (self) -> {
                        CustomRowList.deleted.add(entry);
                        disableWidgets(widgets);
                    },
                    (button, matrixStack, mouseX, mouseY) -> {
                        if (screen.suggestionsAreClosed())
                            screen.renderTooltip(matrixStack, notify, mouseX, mouseY);
                    }
            );

            StringTextComponent undoable = new StringTextComponent(TextFormatting.GREEN + I18n.get("oldswing.config.custom_undo", TextFormatting.WHITE + registryName + TextFormatting.GREEN));
            undo = new Button(
                    width / 2 + 137,
                    0,
                    20,
                    20,
                    getUndoButtonTitle(TextFormatting.GRAY),
                    (self) -> {
                        CustomRowList.deleted.remove(entry);
                        enableWidgets(widgets, entry);
                    },
                    (button, matrixStack, mouseX, mouseY) -> {
                        if (screen.suggestionsAreClosed())
                            screen.renderTooltip(matrixStack, undoable, mouseX, mouseY);
                    }
            );

            undo.active = false;
            widgets.add(rangeTipButton);
            widgets.add(imageTipButton);
            widgets.add(valueSlider);
            widgets.add(remove);
            widgets.add(undo);

            return new CustomRowList.Row(ImmutableList.of(rangeTipButton, imageTipButton, valueSlider, remove, undo));
        }

        public void render(@Nonnull MatrixStack matrix, int x, int y, int unused2, int unused3, int unused4, int xPos, int yPos, boolean unused5, float partialTicks) {
            for (Widget child : this.children) {
                child.y = y;

                if (child instanceof ItemButton) {
                    ItemStack itemStack = CustomSwing.getItemStackFromKey(CustomSwing.key(((ItemButton) child).entry));
                    if (itemStack != null) {
                        int x1 = child.x + 2;
                        int y1 = child.y + 1;
                        if (itemStack.getItem() instanceof BlockItem)
                            y1 = child.y + 2;

                        ((ItemButton) child).parentScreen.getItemRenderer().renderGuiItem(itemStack, x1, y1);
                    }
                } else {
                    if (child instanceof Button) {
                        String title = TextFormatting.stripFormatting(child.getMessage().getString());

                        if (title != null && title.equals(TextFormatting.stripFormatting(getRemoveButtonTitle(TextFormatting.RESET).getText()))) {
                            child.setMessage(getRemoveButtonTitle(child.active ? TextFormatting.DARK_RED : TextFormatting.GRAY));
                        } else if (title != null && title.equals(TextFormatting.stripFormatting(getUndoButtonTitle(TextFormatting.RESET).getText()))) {
                            child.setMessage(getUndoButtonTitle(child.active ? TextFormatting.RED : TextFormatting.GRAY));
                        }
                    }

                    child.render(matrix, xPos, yPos, partialTicks);
                }
            }
        }

        @Nonnull
        public List<? extends IGuiEventListener> children() { return this.children; }
    }
}
