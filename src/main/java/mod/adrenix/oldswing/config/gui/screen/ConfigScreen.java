package mod.adrenix.oldswing.config.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.adrenix.oldswing.config.ClientConfig;
import mod.adrenix.oldswing.config.ConfigHandler;
import mod.adrenix.oldswing.config.gui.widget.ConfigRowList;
import mod.adrenix.oldswing.config.gui.widget.Configuration;
import mod.adrenix.oldswing.config.gui.widget.CustomRowList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.List;

public class ConfigScreen extends Screen
{
    protected static final int OPTIONS_LIST_TOP_HEIGHT = 24;
    protected static final int OPTIONS_LIST_BOTTOM_OFFSET = 32;
    protected static final int OPTIONS_LIST_ITEM_HEIGHT = 25;
    protected static final int BUTTON_WIDTH = 200;
    protected static final int BUTTON_HEIGHT = 20;
    protected static final int DONE_BUTTON_TOP_OFFSET = 26;
    protected static final int TITLE_HEIGHT = 8;

    private static final int BUTTON_LARGE_WIDTH = 204;
    private static final int BUTTON_SMALL_WIDTH = 98;
    private final Screen parentScreen;
    protected ConfigRowList configRowList;
    protected CustomRowList customRowList;
    protected TextFieldWidget searchBox;

    public ConfigScreen(Screen parentScreen)
    {
        super(ITextComponent.nullToEmpty(I18n.get("oldswing.config.settings")));
        this.parentScreen = parentScreen;
    }

    public ConfigScreen(String title, Screen parentScreen)
    {
        super(ITextComponent.nullToEmpty(title));
        this.parentScreen = parentScreen;
    }

    protected ConfigRowList getRowList(List<Configuration> config)
    {
        return new ConfigRowList(
                this,
                config,
                this.width,
                this.height,
                OPTIONS_LIST_TOP_HEIGHT,
                this.height - OPTIONS_LIST_BOTTOM_OFFSET,
                OPTIONS_LIST_ITEM_HEIGHT
        );
    }

    protected Button getDoneButton()
    {
        return new Button(
                (this.width - BUTTON_WIDTH) / 2,
                this.height - DONE_BUTTON_TOP_OFFSET,
                BUTTON_WIDTH, BUTTON_HEIGHT,
                new TranslationTextComponent("gui.done"),
                (unused) -> this.onClose()
        );
    }

    private StringTextComponent getModState()
    {
        boolean flag = ConfigHandler.mod_enabled;
        String title = I18n.get("oldswing.mod") + ": ";
        String state = new TranslationTextComponent(flag ? "options.on.composed" : "options.off.composed").getString().replaceAll("\\s?:\\s", "");
        return new StringTextComponent(title + (flag ? TextFormatting.GREEN : TextFormatting.RED) + state);
    }

    @Override
    protected void init()
    {
        this.addButton(new Button(
                this.width / 2 - 102,
                this.height / 4 + 24 - 16,
                BUTTON_LARGE_WIDTH,
                BUTTON_HEIGHT,
                getModState(),
                (unused) ->
                {
                    ConfigHandler.toggle(ClientConfig.mod_enabled, !ConfigHandler.mod_enabled);
                    this.getMinecraft().setScreen(this);
                }
        ));

        this.addButton(new Button(
                this.width / 2 - 102, // StartX position
                this.height / 4 + 48 - 16, // StartY position
                BUTTON_SMALL_WIDTH, // Width of button
                BUTTON_HEIGHT, // Height of button
                new TranslationTextComponent("oldswing.config.eye_candy"), // Text for button
                (unused) -> this.getMinecraft().setScreen(new CandyScreen(this)) // On click
        ));

        this.addButton(new Button(
                this.width / 2 + 4,
                this.height / 4 + 48 - 16,
                BUTTON_SMALL_WIDTH,
                BUTTON_HEIGHT,
                new TranslationTextComponent(I18n.get("oldswing.config.animations")),
                (unused) -> this.getMinecraft().setScreen(new AnimationScreen(this))
        ));

        this.addButton(new Button(
                this.width / 2 - 102,
                this.height / 4 + 72 - 16,
                BUTTON_SMALL_WIDTH,
                BUTTON_HEIGHT,
                new TranslationTextComponent(I18n.get("oldswing.config.swing_speeds")),
                (unused) -> this.getMinecraft().setScreen(new SwingScreen(this))
        ));

        this.addButton(new Button(
                this.width / 2 + 4,
                this.height / 4 + 72 - 16,
                BUTTON_SMALL_WIDTH,
                BUTTON_HEIGHT,
                new TranslationTextComponent(I18n.get("oldswing.config.custom_swings")),
                (unused) -> this.getMinecraft().setScreen(new CustomScreen(this))
        ));

        this.addButton(new Button(
                this.width / 2 - 102,
                this.height / 4 + 96 - 16,
                BUTTON_LARGE_WIDTH,
                BUTTON_HEIGHT,
                new TranslationTextComponent("gui.done"),
                (unused) -> this.onClose()
        ));
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        int height = TITLE_HEIGHT;
        this.renderBackground(matrixStack);

        if (this.configRowList != null || this.customRowList != null)
        {
            if (this.configRowList != null)
                this.configRowList.render(matrixStack, mouseX, mouseY, partialTicks);
            else this.customRowList.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        else
            height = 40;

        if (this.searchBox != null)
            this.searchBox.render(matrixStack, mouseX, mouseY, partialTicks);

        drawCenteredString(matrixStack, this.font, this.title.getString(), this.width / 2, height, 0xFFFFFF);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void save()
    {
        ConfigHandler.bake();
    }

    @Override
    public void onClose()
    {
        this.save();
        this.getMinecraft().setScreen(parentScreen);
    }
}
