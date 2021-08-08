package mod.adrenix.oldswing.config.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import me.shedaniel.autoconfig.AutoConfig;
import mod.adrenix.oldswing.OldSwing;
import mod.adrenix.oldswing.config.ClientConfig;
import mod.adrenix.oldswing.config.CustomizedSwings;
import mod.adrenix.oldswing.config.gui.ItemSuggestionHelper;
import mod.adrenix.oldswing.config.gui.widget.CustomizedRowList;
import mod.adrenix.oldswing.config.gui.widget.ToggleCheckbox;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Map;

public class CustomizeScreen extends SettingsScreen
{
    private static final int TOP_HEIGHT = 24;
    private static final int BOTTOM_OFFSET = 32;
    private static final int ITEM_HEIGHT = 25;
    private final Checkbox toolsCheckbox;
    private final Checkbox blocksCheckbox;
    private final Checkbox itemsCheckbox;
    private final Checkbox resetCheckbox;
    private Button resetButton;
    private Button addItemButton;
    private Button autofillButton;
    private EditBox searchBox;
    private ItemSuggestionHelper itemSuggestions;
    private CustomizedRowList customizedRowList;
    private final Screen parent;
    private final Map<String, Integer> undo;
    private final List<Widget> renderables = Lists.newArrayList();
    private final int SEARCH_BOX_W = 226;
    private final int SEARCH_BOX_H = 18;
    private final int CHECKBOX_W = 20;
    private final int CHECKBOX_H = 20;
    private final int TOP_ROW_Y = 23;

    public CustomizeScreen(Screen parent)
    {
        super(parent, new TranslatableComponent("gui.oldswing.settings.customize"));

        this.parent = parent;
        this.undo = Map.copyOf(OldSwing.config.custom);
        this.toolsCheckbox = new ToggleCheckbox(this, 2, TOP_ROW_Y + 27, CHECKBOX_W, CHECKBOX_H, new TranslatableComponent("gui.oldswing.customize.tool"), true);
        this.blocksCheckbox = new ToggleCheckbox(this, 2, TOP_ROW_Y + 52, CHECKBOX_W, CHECKBOX_H, new TranslatableComponent("gui.oldswing.customize.block"), true);
        this.itemsCheckbox = new ToggleCheckbox(this, 2, TOP_ROW_Y + 77, CHECKBOX_W, CHECKBOX_H, new TranslatableComponent("gui.oldswing.customize.item"), true);
        this.resetCheckbox = new ToggleCheckbox(this, 2, TOP_ROW_Y - 1, CHECKBOX_W, CHECKBOX_H, new TranslatableComponent("gui.oldswing.customize.reset"), false);
    }

    /* Getters */

    public Button getAddItemButton()
    {
        return addItemButton;
    }

    public Minecraft getMinecraft()
    {
        return minecraft;
    }

    public ItemRenderer getItemRenderer()
    {
        return this.itemRenderer;
    }

    /* Helpers */

    public boolean suggestionsAreClosed()
    {
        if (this.itemSuggestions == null)
            return true;
        return !this.itemSuggestions.isSuggesting();
    }

    private void addCustomizedSwing(Item item)
    {
        CustomizedSwings.addItem(item);
        AutoConfig.getConfigHolder(ClientConfig.class).save();
        CustomizedRowList.added = CustomizedSwings.getEntryFromItem(item);

        this.refresh();
        this.openToast(item);
    }

    private void openToast(Item item)
    {
        Component message = new TranslatableComponent("gui.oldswing.customize.add").withStyle(ChatFormatting.WHITE);
        Component display = new TranslatableComponent(item.getName(item.getDefaultInstance()).getString()).withStyle(ChatFormatting.GREEN);
        this.minecraft.getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.TUTORIAL_HINT, message, display));
    }

    private void clearSearchBox()
    {
        this.itemSuggestions.resetInputBox();
    }

    private void refresh()
    {
        this.clearSearchBox();
        this.minecraft.setScreen(this);
        this.searchBox.setFocus(false);
        this.resetButton.active = this.resetCheckbox.selected();
    }

    private void resetCustomizedList()
    {
        OldSwing.config.custom.clear();
        CustomizedRowList.added = null;

        this.resetCheckbox.onPress();
        this.clearSearchBox();
        this.minecraft.setScreen(this);
    }

    private int getSavePosition()
    {
        return this.width / 2 + 3;
    }

    private void renderWidget(Widget widget, PoseStack stack, int mouseX, int mouseY, float ticks)
    {
        if (widget instanceof Button && ((Button) widget).x == this.getSavePosition())
            ((Button) widget).active = this.isSavable();
        widget.render(stack, mouseX, mouseY, ticks);
    }

    private boolean isSavable()
    {
        if (this.undo.size() != OldSwing.config.custom.size())
            return true;
        else if (CustomizedRowList.deleted.size() > 0)
            return true;

        for (Map.Entry<String, Integer> entry : OldSwing.config.custom.entrySet())
            if (this.undo.get(entry.getKey()).intValue() != entry.getValue().intValue())
                return true;

        return false;
    }

    private void sortCustomizedRowList()
    {
        boolean addTools = this.toolsCheckbox.selected();
        boolean addBlocks = this.blocksCheckbox.selected();
        boolean addItems = this.itemsCheckbox.selected();
        List<Map.Entry<String, Integer>> sorted = CustomizedSwings.getSortedItems(addTools, addBlocks, addItems);
        for (Map.Entry<String, Integer> entry : sorted)
            this.customizedRowList.addItem(entry);
    }

    /* Overrides */

    @Override
    public void tick()
    {
        this.searchBox.tick();

        if (this.resetButton.active != this.resetCheckbox.selected())
            this.resetButton.active = this.resetCheckbox.selected();
    }

    @Override
    public void resize(Minecraft minecraft, int x, int y)
    {
        String searching = this.searchBox.getValue();
        this.init(minecraft, x, y);
        this.searchBox.setValue(searching);
        this.itemSuggestions.updateItemSuggestions();
    }

    @Override
    public boolean keyPressed(int key, int x, int y)
    {
        if (this.itemSuggestions.keyPressed(key))
            return true;
        else if (key == 257 && this.addItemButton.active)
        {
            this.addCustomizedSwing(this.itemSuggestions.getItem());
            return true;
        }
        else if (key == 256 && this.shouldCloseOnEsc())
        {
            this.onCancel();
            return true;
        }
        else if (super.keyPressed(key, x, y))
            return true;
        else
            return key == 257 || key == 335;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double f)
    {
        return this.itemSuggestions.mouseScrolled(f) || super.mouseScrolled(x, y, f);
    }

    @Override
    public boolean mouseClicked(double x, double y, int i)
    {
        return this.itemSuggestions.mouseClicked(x, y) || super.mouseClicked(x, y, i);
    }

    @Override
    protected <T extends GuiEventListener & Widget & NarratableEntry> T addRenderableWidget(T widget)
    {
        this.renderables.add(widget);
        return this.addWidget(widget);
    }

    @Override
    protected void removeWidget(GuiEventListener widget)
    {
        if (widget instanceof Widget)
            this.renderables.remove(widget);
        super.removeWidget(widget);
    }

    @Override
    protected void clearWidgets()
    {
        this.renderables.clear();
        super.clearWidgets();
    }

    @Override
    protected void init()
    {
        this.addRenderableWidget(this.toolsCheckbox);
        this.addRenderableWidget(this.blocksCheckbox);
        this.addRenderableWidget(this.itemsCheckbox);
        this.addRenderableWidget(this.resetCheckbox);

        WidgetProvider widget = new WidgetProvider();

        this.customizedRowList = widget.createCustomizedRowList();
        this.searchBox = widget.createSearchBox();
        this.searchBox.setResponder(this::onEdited);

        this.sortCustomizedRowList();

        this.addItemButton = widget.createAddButton();
        this.autofillButton = widget.createAutofillButton();
        this.resetButton = widget.createResetButton();
        this.resetButton.active = false;
        this.autofillButton.active = false;
        this.addItemButton.active = false;

        this.addWidget(this.customizedRowList);
        this.addRenderableWidget(this.searchBox);
        this.addRenderableWidget(this.addItemButton);
        this.addRenderableWidget(this.autofillButton);
        this.addRenderableWidget(this.resetButton);
        this.addRenderableWidget(widget.createCancelButton());
        this.addRenderableWidget(widget.createSaveButton());

        this.setInitialFocus(this.searchBox);

        this.itemSuggestions = new ItemSuggestionHelper(this, this.searchBox, this.font, 7, -16777216);
        this.itemSuggestions.setAllowSuggestions(true);
        this.itemSuggestions.updateItemSuggestions();
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float ticks)
    {
        this.autofillButton.active = this.minecraft.level != null;
        this.renderCustomizedBackground(stack, false);
        this.customizedRowList.render(stack, mouseX, mouseY, ticks);
        this.renderScreenTitle(stack, 7);
        this.searchBox.render(stack, mouseX, mouseY, ticks);
        this.renderables.forEach(widget -> this.renderWidget(widget, stack, mouseX, mouseY, ticks));
        this.itemSuggestions.render(stack, mouseX, mouseY);
        this.itemRenderer.renderGuiItem(new ItemStack(Items.TNT), this.resetButton.x + 2, this.resetButton.y + 2);
    }

    /* Cancelling Consumer */

    private class CancelConsumer implements BooleanConsumer
    {
        @Override
        public void accept(boolean understood)
        {
            if (understood)
            {
                CustomizeScreen.this.onClose(true);
                CustomizeScreen.this.minecraft.setScreen(CustomizeScreen.this.parent);
            }
            else
                CustomizeScreen.this.minecraft.setScreen(CustomizeScreen.this);
        }
    }

    /* On-click Providers */

    private void onEdited(String ignored)
    {
        this.itemSuggestions.updateItemSuggestions();
    }

    private void onAddCustomizedSwing()
    {
        this.addCustomizedSwing(this.itemSuggestions.getItem());
    }

    private void onAutofill()
    {
        if (this.minecraft.player != null)
            this.addCustomizedSwing(this.minecraft.player.getMainHandItem().getItem());
    }

    private void onCancel()
    {
        if (!this.isSavable())
        {
            this.onClose(true);
            return;
        }

        this.minecraft.setScreen(
            new ConfirmScreen(
                new CancelConsumer(),
                new TranslatableComponent("text.cloth-config.quit_config"),
                new TranslatableComponent("text.cloth-config.quit_config_sure"),
                new TranslatableComponent("text.cloth-config.quit_discard"),
                new TranslatableComponent("gui.cancel")
            )
        );
    }

    private void onClose(boolean isCancelled)
    {
        if (!isCancelled)
        {
            for (Map.Entry<String, Integer> entry : CustomizedRowList.deleted)
                OldSwing.config.custom.remove(entry.getKey());
        }
        else
        {
            OldSwing.config.custom.clear();
            OldSwing.config.custom.putAll(this.undo);
        }

        CustomizedRowList.deleted.clear();
        CustomizedRowList.added = null;

        super.onClose();
    }

    /* Widget Provider */

    private class WidgetProvider
    {
        public CustomizedRowList createCustomizedRowList()
        {
            return new CustomizedRowList(
                CustomizeScreen.this,
                CustomizeScreen.this.width,
                CustomizeScreen.this.height,
                TOP_HEIGHT + 22,
                CustomizeScreen.this.height - BOTTOM_OFFSET,
                    ITEM_HEIGHT
            );
        }

        public EditBox createSearchBox()
        {
            return new EditBox(
                CustomizeScreen.this.font,
                CustomizeScreen.this.width / 2 - 112,
                CustomizeScreen.this.TOP_ROW_Y,
                CustomizeScreen.this.SEARCH_BOX_W,
                CustomizeScreen.this.SEARCH_BOX_H,
                CustomizeScreen.this.searchBox,
                TextComponent.EMPTY
            );
        }

        public Button createAddButton()
        {
            Component tooltip = new TranslatableComponent("gui.oldswing.customize.add.@Tooltip").withStyle(ChatFormatting.GREEN);
            return new Button(
                CustomizeScreen.this.width / 2 + 116,
                CustomizeScreen.this.TOP_ROW_Y - 1,
                CustomizeScreen.this.CHECKBOX_W,
                CustomizeScreen.this.CHECKBOX_H,
                new TextComponent("+").withStyle(ChatFormatting.GREEN),
                (button) -> CustomizeScreen.this.onAddCustomizedSwing(),
                (button, stack, mouseX, mouseY) -> CustomizeScreen.this.renderTooltip(stack, tooltip, mouseX, mouseY)
            );
        }

        public Button createAutofillButton()
        {
            Component tooltip = new TranslatableComponent("gui.oldswing.customize.autofill.@Tooltip").withStyle(ChatFormatting.YELLOW);
            return new Button(
                CustomizeScreen.this.width / 2 - 134,
                CustomizeScreen.this.TOP_ROW_Y - 1,
                CustomizeScreen.this.CHECKBOX_W,
                CustomizeScreen.this.CHECKBOX_H,
                new TextComponent("\u26a1").withStyle(ChatFormatting.YELLOW).withStyle(ChatFormatting.BOLD),
                (button) -> CustomizeScreen.this.onAutofill(),
                (button, stack, mouseX, mouseY) -> CustomizeScreen.this.renderTooltip(stack, tooltip, mouseX, mouseY)
            );
        }

        public Button createResetButton()
        {
            List<Component> tooltip = Lists.newArrayList(
                new TranslatableComponent("gui.oldswing.customize.reset.@Tooltip[0]").withStyle(ChatFormatting.RED),
                new TranslatableComponent("gui.oldswing.customize.reset.@Tooltip[1]").withStyle(ChatFormatting.DARK_RED)
            );

            return new Button(
                CustomizeScreen.this.autofillButton.x - 21,
                CustomizeScreen.this.TOP_ROW_Y - 1,
                CustomizeScreen.this.CHECKBOX_W,
                CustomizeScreen.this.CHECKBOX_H,
                TextComponent.EMPTY,
                (button) -> CustomizeScreen.this.resetCustomizedList(),
                (button, stack, mouseX, mouseY) -> CustomizeScreen.this.renderComponentTooltip(stack, tooltip, mouseX, mouseY)
            );
        }

        public Button createSaveButton()
        {
            return new Button(
                CustomizeScreen.this.getSavePosition(),
                CustomizeScreen.this.height - DONE_BUTTON_TOP_OFFSET,
                this.getSmallWidth(),
                BUTTON_HEIGHT,
                new TranslatableComponent("text.cloth-config.save_and_done"),
                (button) -> CustomizeScreen.this.onClose(false)
            );
        }

        public Button createCancelButton()
        {
            return new Button(
                CustomizeScreen.this.width / 2 - getSmallWidth() - 3,
                CustomizeScreen.this.height - DONE_BUTTON_TOP_OFFSET,
                this.getSmallWidth(),
                BUTTON_HEIGHT,
                new TranslatableComponent("gui.cancel"),
                (button) -> CustomizeScreen.this.onCancel()
            );
        }

        private int getSmallWidth()
        {
            return Math.min(200, (CustomizeScreen.this.width - 50 - 12) / 3);
        }
    }
}
