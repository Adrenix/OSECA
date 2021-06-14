package mod.adrenix.oldswing.config.gui.screen;

import com.electronwill.nightconfig.core.Config;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import mod.adrenix.oldswing.config.ClientConfig;
import mod.adrenix.oldswing.config.ConfigHandler;
import mod.adrenix.oldswing.config.CustomSwing;
import mod.adrenix.oldswing.config.gui.ItemSuggestionHelper;
import mod.adrenix.oldswing.config.gui.widget.CustomRowList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CustomScreen extends ConfigScreen
{
    protected int TOP_ROW_Y = 23;
    protected ItemSuggestionHelper itemSuggestions;
    protected Button addItemButton;
    protected Button autoFillButton;
    private Button resetButton;
    private final CheckboxButton toggleTools;
    private final CheckboxButton toggleBlocks;
    private final CheckboxButton toggleItems;
    private final CheckboxButton toggleReset;
    private boolean toolsToggleState = true;
    private boolean blocksToggleState = true;
    private boolean itemsToggleState = true;
    private boolean resetToggleState = false;
    private final int checkX = 4;
    private final int checkW = 20;
    private final int checkH = 20;

    public CustomScreen(Screen parentScreen)
    {
        super(I18n.get("oldswing.config.custom_swing_speed"), parentScreen);

        this.toggleTools = new CheckboxButton(checkX, TOP_ROW_Y + 27, checkW, checkH, new StringTextComponent(I18n.get("oldswing.tool")), this.toolsToggleState);
        this.toggleBlocks = new CheckboxButton(checkX, TOP_ROW_Y + 52, checkW, checkH, new StringTextComponent(I18n.get("oldswing.block")), this.blocksToggleState);
        this.toggleItems = new CheckboxButton(checkX, TOP_ROW_Y + 77, checkW, checkH, new StringTextComponent(I18n.get("oldswing.item")), this.itemsToggleState);
        this.toggleReset = new CheckboxButton(checkX, TOP_ROW_Y - 1, checkW, checkH, new StringTextComponent(I18n.get("oldswing.config.reset")), this.resetToggleState, false);
    }

    public boolean suggestionsAreClosed()
    {
        if (this.itemSuggestions == null)
            return true;
        return !this.itemSuggestions.isSuggesting();
    }

    public ItemRenderer getItemRenderer()
    {
        return this.itemRenderer;
    }

    @Nullable
    public Button getAddItemButton()
    {
        return this.addItemButton;
    }

    public CustomRowList getCustomRowList()
    {
        return new CustomRowList(
                this, this.width, this.height,
                OPTIONS_LIST_TOP_HEIGHT + 22,
                this.height - OPTIONS_LIST_BOTTOM_OFFSET,
                OPTIONS_LIST_ITEM_HEIGHT
        );
    }

    public TextFieldWidget getSearchBox()
    {
        return new TextFieldWidget(
                this.font,
                this.width / 2 - 112,
                TOP_ROW_Y,
                226,
                18,
                this.searchBox,
                new StringTextComponent(I18n.get("oldswing.config.item_search"))
        );
    }

    private void openCustomToast(String itemName)
    {
        ITextComponent message = new StringTextComponent(TextFormatting.WHITE + I18n.get("oldswing.config.toast_add_item"));
        ITextComponent item = new StringTextComponent(TextFormatting.GREEN + itemName);
        this.getMinecraft().getToasts().addToast(new SystemToast(SystemToast.Type.TUTORIAL_HINT, message, item));
    }

    private void refresh()
    {
        this.itemSuggestions.resetInputField();
        this.getMinecraft().setScreen(this.getMinecraft().screen);
        this.searchBox.setFocus(false);
        this.resetButton.active = this.toggleReset.selected();
    }

    private void addCustomItem(@Nullable Item item)
    {
        if (item != null)
        {
            CustomSwing.add(Objects.requireNonNull(item.getRegistryName()).toString(), ConfigHandler.OLD_SPEED);
            ConfigHandler.bake();

            Config.Entry entry = CustomSwing.getConfigEntry(item);
            CustomRowList.added = entry;

            if (entry != null)
                openCustomToast(CustomSwing.getRegistryName(CustomSwing.key(entry)));
        }

        this.refresh();
        CustomRowList.deleted.clear();
    }

    public Button getLightningButton()
    {
        StringTextComponent tip = new StringTextComponent(TextFormatting.YELLOW + I18n.get("oldswing.config.auto_add_item"));
        return new Button(
                this.width / 2 - 134,
                TOP_ROW_Y - 1,
                20,
                20,
                new StringTextComponent(TextFormatting.YELLOW + (TextFormatting.BOLD + "\u26a1")),
                (unused) ->
                {
                    if (this.getMinecraft().player != null)
                        this.addCustomItem(CustomSwing.addFromHand(this.getMinecraft().player, ConfigHandler.OLD_SPEED));
                },
                (button, matrixStack, mouseX, mouseY) -> this.renderTooltip(matrixStack, tip, mouseX, mouseY)
        );
    }

    public Button getAddButton()
    {
        StringTextComponent tip = new StringTextComponent(TextFormatting.GREEN + I18n.get("oldswing.config.add_item"));
        return new Button(
                this.width / 2 + 116,
                TOP_ROW_Y - 1,
                20,
                20,
                new StringTextComponent(TextFormatting.GREEN + "+"),
                (unused) -> this.addCustomItem(this.itemSuggestions.getItem()),
                (button, matrixStack, mouseX, mouseY) -> this.renderTooltip(matrixStack, tip, mouseX, mouseY)
        );
    }

    private void resetCustomList()
    {
        ClientConfig.custom.get().clear();
        this.toggleReset.onPress();
        this.getMinecraft().setScreen(this.getMinecraft().screen);
    }

    public Button getResetButton()
    {
        StringTextComponent topTip = new StringTextComponent(TextFormatting.RED + I18n.get("oldswing.config.clear_custom_list"));
        StringTextComponent bottomTip = new StringTextComponent(I18n.get("oldswing.config.clear_custom_list_undone", TextFormatting.DARK_RED, TextFormatting.UNDERLINE, TextFormatting.DARK_RED));
        List<ITextComponent> tip = Lists.newArrayList(topTip, bottomTip);

        return new Button(
                checkX + 23,
                TOP_ROW_Y - 1,
                checkW,
                checkH,
                new StringTextComponent(" "),
                (unused) -> this.resetCustomList(),
                (button, matrixStack, mouseX, mouseY) -> this.renderComponentTooltip(matrixStack, tip, mouseX, mouseY)
        );
    }

    @Override
    public void tick()
    {
        this.searchBox.tick();

        if (this.toolsToggleState != this.toggleTools.selected())
        {
            this.toolsToggleState = this.toggleTools.selected();
            this.refresh();
        }
        else if (this.blocksToggleState != this.toggleBlocks.selected())
        {
            this.blocksToggleState = this.toggleBlocks.selected();
            this.refresh();
        }
        else if (this.itemsToggleState != this.toggleItems.selected())
        {
            this.itemsToggleState = this.toggleItems.selected();
            this.refresh();
        }

        if (this.resetToggleState != this.toggleReset.selected())
        {
            this.resetToggleState = this.toggleReset.selected();
            this.resetButton.active = this.toggleReset.selected();
        }
    }

    @Override
    public void resize(@Nonnull Minecraft mc, int x, int y)
    {
        String s = this.searchBox.getValue();
        this.init(mc, x, y);
        this.searchBox.setValue(s);
        this.itemSuggestions.updateItemSuggestions();
        this.resetButton.active = this.toggleReset.selected();
    }

    private void onEdited(String unused)
    {
        this.itemSuggestions.updateItemSuggestions();
    }

    @Override
    public boolean keyPressed(int key, int i2, int i3)
    {
        if (this.itemSuggestions.keyPressed(key))
            return true;
        else if (key == 257 && this.addItemButton.active)
        {
            this.addCustomItem(this.itemSuggestions.getItem());
            return true;
        }
        else if (super.keyPressed(key, i2, i3))
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

    private String getEntryId(Config.Entry entry)
    {
        return entry.getKey().replaceFirst(".+-", "");
    }

    private List<Config.Entry> sortCustomItems()
    {
        List<Config.Entry> sorted = Lists.newArrayList();
        List<Config.Entry> tools = Lists.newArrayList();
        List<Config.Entry> blocks = Lists.newArrayList();
        List<Config.Entry> unknown = Lists.newArrayList();
        List<Config.Entry> uncategorized = Lists.newArrayList();
        Config.Entry added = null;

        for (Config.Entry entry : ConfigHandler.custom_speeds.entrySet())
        {
            ItemStack stack = CustomSwing.getItemStackFromKey(CustomSwing.key(entry));

            if (entry.equals(CustomRowList.added))
                added = entry;
            else if (stack == null)
                unknown.add(entry);
            else if (stack.getItem() instanceof ToolItem || stack.getItem() instanceof SwordItem)
                if (this.toggleTools.selected())
                    tools.add(entry);
            else if (stack.getItem() instanceof BlockItem)
                if (this.toggleBlocks.selected())
                    blocks.add(entry);
            else
                if (this.toggleItems.selected())
                    uncategorized.add(entry);
        }

        sorted.addAll(tools);
        sorted.addAll(blocks);
        sorted.addAll(uncategorized);
        sorted.addAll(unknown);
        sorted.sort(Comparator.comparing(this::getEntryId));

        if (added != null)
            sorted.add(0, added);

        return sorted;
    }

    @Override
    protected void init()
    {
        this.addButton(this.toggleTools);
        this.addButton(this.toggleBlocks);
        this.addButton(this.toggleItems);
        this.addButton(this.toggleReset);

        this.customRowList = getCustomRowList();
        this.searchBox = getSearchBox();
        this.searchBox.setResponder(this::onEdited);

        for (Config.Entry entry : sortCustomItems())
            this.customRowList.addItem(entry);

        this.addItemButton = this.getAddButton();
        this.autoFillButton = this.getLightningButton();
        this.resetButton = this.getResetButton();
        this.resetButton.active = false;
        this.addItemButton.active = false;

        this.addButton(this.autoFillButton);
        this.addButton(this.addItemButton);
        this.addButton(this.resetButton);
        this.addButton(this.getDoneButton());
        this.children.add(this.searchBox);
        this.children.add(this.customRowList);
        this.setInitialFocus(this.searchBox);
        this.itemSuggestions = new ItemSuggestionHelper(this.minecraft, this, this.searchBox, this.font, 7, -16777216);
        this.itemSuggestions.setAllowSuggestions(true);
        this.itemSuggestions.updateItemSuggestions();
    }

    @Nullable
    private Item getResetBlock()
    {
        return ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse("minecraft:tnt"));
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.autoFillButton.active = this.getMinecraft().level != null;
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.itemSuggestions.render(matrixStack, mouseX, mouseY);

        Item tnt = getResetBlock();
        if (tnt != null)
            this.getItemRenderer().renderGuiItem(tnt.getDefaultInstance(), this.resetButton.x + 2, this.resetButton.y + 2);
    }

    @Override
    public void onClose()
    {
        if (!CustomRowList.deleted.isEmpty())
            for (Config.Entry entry : CustomRowList.deleted)
                CustomSwing.remove(entry.getKey());

        CustomRowList.deleted.clear();
        CustomRowList.added = null;

        super.onClose();
    }
}
