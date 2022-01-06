package mod.adrenix.oseca.config.gui.widget;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mod.adrenix.oseca.config.CustomSwings;
import mod.adrenix.oseca.config.DefaultConfig;
import mod.adrenix.oseca.config.gui.screen.CustomizeScreen;
import mod.adrenix.oseca.config.gui.screen.SettingsScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomizedRowList extends ContainerObjectSelectionList<CustomizedRowList.Row>
{
    public static final List<Map.Entry<String, Integer>> deleted = new ArrayList<>();
    public static Map.Entry<String, Integer> added;
    private final CustomizeScreen screen;

    public CustomizedRowList(CustomizeScreen screen, int width, int height, int y0, int y1, int itemHeight)
    {
        super(screen.getMinecraft(), width, height, y0, y1, itemHeight);
        this.centerListVertically = false;
        this.screen = screen;
    }

    /* Row List Utilities */

    public void addItem(Map.Entry<String, Integer> entry)
    {
        this.addEntry(CustomizedRowList.Row.item(screen, entry));
    }

    /* Overrides */

    @Override
    public int getRowWidth()
    {
        return 400;
    }

    @Override
    protected int getScrollbarPosition()
    {
        return this.width - 4;
    }

    @Override
    public boolean mouseClicked(double x, double y, int button)
    {
        boolean clicked = super.mouseClicked(x, y, button);

        if (clicked)
            this.screen.setSuggestionFocus(false);
        return clicked;
    }

    @Override
    public void render(PoseStack stack, int x, int y, float ticks)
    {
        if (this.minecraft.level == null)
            this.screen.renderDirtBackground(0);

        int scrollbarPosition = this.getScrollbarPosition();
        int scrollbarPositionOffset = scrollbarPosition + 6;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

        int startX = this.getRowLeft();
        int startY = this.y0 + 4 - (int) this.getScrollAmount();

        this.fillGradient(stack, this.x0, this.y0, this.x1, this.y1, 0x68000000, 0x68000000);
        this.renderList(stack, startX, startY, x, y, ticks);

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, SettingsScreen.BACKGROUND_LOCATION);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(519);

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        builder.vertex(this.x0, this.y0, -100.0D).uv(0.0F, (float) this.y0 / 32.0F).color(64, 64, 64, 255).endVertex();
        builder.vertex((this.x0 + this.width), this.y0, -100.0D).uv((float) this.width / 32.0F, (float) this.y0 / 32.0F).color(64, 64, 64, 255).endVertex();
        builder.vertex((this.x0 + this.width), 0.0D, -100.0D).uv((float) this.width / 32.0F, 0.0F).color(64, 64, 64, 255).endVertex();
        builder.vertex(this.x0, 0.0D, -100.0D).uv(0.0F, 0.0F).color(64, 64, 64, 255).endVertex();
        builder.vertex(this.x0, this.height, -100.0D).uv(0.0F, (float) this.height / 32.0F).color(64, 64, 64, 255).endVertex();
        builder.vertex((this.x0 + this.width), this.height, -100.0D).uv((float) this.width / 32.0F, (float) this.height / 32.0F).color(64, 64, 64, 255).endVertex();
        builder.vertex((this.x0 + this.width), this.y1, -100.0D).uv((float) this.width / 32.0F, (float) this.y1 / 32.0F).color(64, 64, 64, 255).endVertex();
        builder.vertex(this.x0, this.y1, -100.0D).uv(0.0F, (float) this.y1 / 32.0F).color(64, 64, 64, 255).endVertex();
        tesselator.end();

        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        builder.vertex(this.x0, (this.y0 + 4), 0.0D).color(0, 0, 0, 0).endVertex();
        builder.vertex(this.x1, (this.y0 + 4), 0.0D).color(0, 0, 0, 0).endVertex();
        builder.vertex(this.x1, this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
        builder.vertex(this.x0, this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
        builder.vertex(this.x0, this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
        builder.vertex(this.x1, this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
        builder.vertex(this.x1, (this.y1 - 4), 0.0D).color(0, 0, 0, 0).endVertex();
        builder.vertex(this.x0, (this.y1 - 4), 0.0D).color(0, 0, 0, 0).endVertex();
        tesselator.end();

        int maxScroll = this.getMaxScroll();
        if (maxScroll > 0)
        {
            RenderSystem.disableTexture();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            int heightOffset = (int) ((float) ((this.y1 - this.y0) * (this.y1 - this.y0)) / (float) this.getMaxPosition());
            heightOffset = Mth.clamp(heightOffset, 32, (this.y1 - this.y0 - 8));
            int scrollOffset = (int) this.getScrollAmount() * (this.y1 - this.y0 - heightOffset) / maxScroll + this.y0;

            if (scrollOffset < this.y0)
            {
                scrollOffset = this.y0;
            }

            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            builder.vertex(scrollbarPosition, this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
            builder.vertex(scrollbarPositionOffset, this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
            builder.vertex(scrollbarPositionOffset, this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
            builder.vertex(scrollbarPosition, this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
            builder.vertex(scrollbarPosition, (scrollOffset + heightOffset), 0.0D).color(128, 128, 128, 255).endVertex();
            builder.vertex(scrollbarPositionOffset, (scrollOffset + heightOffset), 0.0D).color(128, 128, 128, 255).endVertex();
            builder.vertex(scrollbarPositionOffset, scrollOffset, 0.0D).color(128, 128, 128, 255).endVertex();
            builder.vertex(scrollbarPosition, scrollOffset, 0.0D).color(128, 128, 128, 255).endVertex();
            builder.vertex(scrollbarPosition, (scrollOffset + heightOffset - 1), 0.0D).color(192, 192, 192, 255).endVertex();
            builder.vertex((scrollbarPositionOffset - 1), (scrollOffset + heightOffset - 1), 0.0D).color(192, 192, 192, 255).endVertex();
            builder.vertex((scrollbarPositionOffset - 1), scrollOffset, 0.0D).color(192, 192, 192, 255).endVertex();
            builder.vertex(scrollbarPosition, scrollOffset, 0.0D).color(192, 192, 192, 255).endVertex();
            tesselator.end();
        }

        this.renderDecorations(stack, x, y);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    /* Row Widgets for Customized Swing */

    protected static class Row extends ContainerObjectSelectionList.Entry<Row>
    {
        private final List<AbstractWidget> children;
        private final Map.Entry<String, Integer> entry;

        private Row(List<AbstractWidget> list, Map.Entry<String, Integer> entry)
        {
            this.children = list;
            this.entry = entry;
        }

        /* Create Item Row */

        public static CustomizedRowList.Row item(CustomizeScreen screen, Map.Entry<String, Integer> entry)
        {
            List<AbstractWidget> widgets = new ArrayList<>();
            Button range = Widgets.createRange(screen, entry);
            ItemButton item = Widgets.createImage(screen, entry);
            SpeedSlider slider = Widgets.createSlider(screen, entry);
            Button remove = Widgets.createRemove(screen, entry, widgets);
            Button undo = Widgets.createUndo(screen, entry, widgets);
            Button reset = Widgets.createReset(screen, entry, slider);

            undo.active = false;
            reset.active = entry.getValue() != DefaultConfig.Swings.OLD_SPEED;

            widgets.add(range);
            widgets.add(item);
            widgets.add(slider);
            widgets.add(remove);
            widgets.add(reset);
            widgets.add(undo);

            CustomizedRowList.deleted.forEach((deleted) -> {
                if (deleted.getKey().equals(entry.getKey()))
                    disableWidgets(widgets);
            });

            return new CustomizedRowList.Row(ImmutableList.copyOf(widgets), entry);
        }

        /* Row Management */

        public static void disableWidgets(List<AbstractWidget> widgets)
        {
            widgets.forEach((widget) -> widget.active = false);
            widgets.get(widgets.size() - 1).active = true;
        }

        public static void enableWidgets(List<AbstractWidget> widgets)
        {
            widgets.forEach((widget) -> widget.active = true);
            widgets.get(widgets.size() - 1).active = false;
        }

        public static void delete(Map.Entry<String, Integer> entry, List<AbstractWidget> widgets)
        {
            deleted.add(entry);
            disableWidgets(widgets);
        }

        public static void undo(Map.Entry<String, Integer> entry, List<AbstractWidget> widgets)
        {
            deleted.remove(entry);
            enableWidgets(widgets);
        }

        private void renderWidget(AbstractWidget widget, PoseStack stack, int mouseX, int mouseY, float ticks)
        {
            if (widget instanceof SpeedSlider)
                ((SpeedSlider) widget).updateMessage();
            else if (widget instanceof Button)
            {
                String title = ChatFormatting.stripFormatting(widget.getMessage().getString());
                if (title != null && title.equals(Widgets.REMOVE))
                    widget.setMessage(new TextComponent(Widgets.REMOVE).withStyle(widget.active ? ChatFormatting.DARK_RED : ChatFormatting.GRAY).withStyle(ChatFormatting.BOLD));
                else if (title != null && title.equals(Widgets.UNDO))
                    widget.setMessage(new TextComponent(Widgets.UNDO).withStyle(widget.active ? ChatFormatting.RED : ChatFormatting.GRAY));
                else if (title != null && title.equals(new TranslatableComponent("text.cloth-config.reset_value").getString()))
                    widget.active = !CustomizedRowList.deleted.contains(entry) && this.entry.getValue() != DefaultConfig.Swings.OLD_SPEED;
            }

            widget.render(stack, mouseX, mouseY, ticks);
        }

        /* Overrides */

        @Override
        public void render(PoseStack stack, int x, int y, int ignored1, int ignored2, int ignored3, int mouseX, int mouseY, boolean ignored4, float ticks)
        {
            for (AbstractWidget widget : this.children)
            {
                widget.y = y;

                if (widget instanceof ItemButton)
                {
                    Item item = CustomSwings.getItem(((ItemButton) widget).entry);
                    int startX = widget.x + 2;
                    int startY = widget.y + 1;

                    if (item instanceof BlockItem)
                        startY = widget.y + 2;

                    boolean isValid = CustomSwings.isValidEntry(item, ((ItemButton) widget).entry);
                    ((ItemButton) widget).screen.getItemRenderer().renderGuiItem(isValid ? item.getDefaultInstance() : new ItemStack(Items.BARRIER), startX, startY);
                }
                else
                    this.renderWidget(widget, stack, mouseX, mouseY, ticks);
            }
        }

        @Override
        public List<? extends NarratableEntry> narratables()
        {
            return this.children;
        }

        @Override
        public List<? extends GuiEventListener> children()
        {
            return this.children;
        }

        /* Widgets */
        private static class Widgets
        {
            public static final String REMOVE = "\u274c";
            public static final String UNDO = "\u2764";

            public static ItemButton createImage(CustomizeScreen screen, Map.Entry<String, Integer> entry)
            {
                return new ItemButton(screen, entry, screen.width / 2 - 134, 0, 20, 20);
            }

            public static SpeedSlider createSlider(CustomizeScreen screen, Map.Entry<String, Integer> entry)
            {
                return new SpeedSlider(entry, screen.width / 2 - 113, 0, 228, 20);
            }

            public static Button createRange(CustomizeScreen screen, Map.Entry<String, Integer> entry)
            {
                boolean isAdded = CustomizedRowList.added != null && CustomizedRowList.added.getKey().equals(entry.getKey());
                return new Button(
                    screen.width / 2 - 155,
                    0,
                    20,
                    20,
                    new TranslatableComponent("#").withStyle(isAdded ? ChatFormatting.YELLOW : ChatFormatting.RESET),
                    (ignored) -> {},
                    (button, stack, mouseX, mouseY) ->
                    {
                        if (screen.suggestionsAreClosed())
                            screen.renderComponentTooltip(stack, CustomSwings.rangeTooltip(), mouseX, mouseY);
                    }
                );
            }

            public static Button createRemove(CustomizeScreen screen, Map.Entry<String, Integer> entry, List<AbstractWidget> widgets)
            {
                return new Button(
                    screen.width / 2 + 116,
                    0,
                    20,
                    20,
                    new TranslatableComponent(REMOVE),
                    (button) -> CustomizedRowList.Row.delete(entry, widgets),
                    (button, stack, mouseX, mouseY) ->
                    {
                        if (screen.suggestionsAreClosed())
                            screen.renderTooltip(stack, CustomSwings.removeTooltip(entry), mouseX, mouseY);
                    }
                );
            }

            public static Button createUndo(CustomizeScreen screen, Map.Entry<String, Integer> entry, List<AbstractWidget> widgets)
            {
                return new Button(
                    screen.width / 2 + 137,
                    0,
                    20,
                    20,
                    new TranslatableComponent(UNDO),
                    (button) -> CustomizedRowList.Row.undo(entry, widgets),
                    (button, stack, mouseX, mouseY) ->
                    {
                        if (screen.suggestionsAreClosed())
                            screen.renderTooltip(stack, CustomSwings.undoTooltip(entry), mouseX, mouseY);
                    }
                );
            }

            public static Button createReset(CustomizeScreen screen, Map.Entry<String, Integer> entry, SpeedSlider slider)
            {
                TranslatableComponent title = new TranslatableComponent("text.cloth-config.reset_value");
                return new Button(
                    screen.width / 2 + 158,
                    0,
                    screen.getMinecraft().font.width(title) + 6,
                    20,
                    title,
                    (button) ->
                    {
                        entry.setValue(DefaultConfig.Swings.OLD_SPEED);
                        slider.setValue(DefaultConfig.Swings.OLD_SPEED);
                    },
                    (button, stack, mouseX, mouseY) -> button.active = entry.getValue() != DefaultConfig.Swings.OLD_SPEED
                );
            }
        }
    }
}
