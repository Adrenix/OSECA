package mod.adrenix.oldswing.config.gui.screen;

import mod.adrenix.oldswing.config.ClientConfig;
import mod.adrenix.oldswing.config.ConfigHandler;
import mod.adrenix.oldswing.config.gui.widget.BooleanButton;
import mod.adrenix.oldswing.config.gui.widget.ResetOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class SwingScreen extends ConfigScreen
{
    public SwingScreen(Screen parentScreen)
    {
        super(I18n.get("oldswing.config.cat_swing_speed"), parentScreen);
    }

    public static List<ITextComponent> rangeTip()
    {
        List<ITextComponent> tip = new ArrayList<>();

        String alpha = I18n.get("oldswing.config.range_alpha");
        String modern = I18n.get("oldswing.config.range_modern");

        tip.add(new StringTextComponent(TextFormatting.GREEN + alpha + TextFormatting.RESET + ": " + TextFormatting.AQUA + ConfigHandler.OLD_SPEED));
        tip.add(new StringTextComponent(TextFormatting.GOLD + modern + TextFormatting.RESET + ": " + TextFormatting.AQUA + ConfigHandler.NEW_SPEED));

        return tip;
    }

    private Button addRangeTip(int row)
    {
        return new Button(
                this.width / 2 - 176,
                getRowSeparation(row),
                20,
                20,
                new StringTextComponent("#"),
                (unused) -> {},
                (button, matrixStack, mouseX, mouseY) -> this.renderComponentTooltip(matrixStack, SwingScreen.rangeTip(), mouseX, mouseY)
        );
    }

    public static String getRangeColor(int speed)
    {
        if (speed <= ConfigHandler.NEW_SPEED)
            return TextFormatting.GOLD.toString() + speed;
        return TextFormatting.GREEN.toString() + speed;
    }

    @Override
    protected void init()
    {
        String itemTitle = I18n.get("oldswing.config.item_swing_speed");
        String itemDesc = I18n.get("oldswing.config.item_swing_description");
        String blockTitle = I18n.get("oldswing.config.block_swing_speed");
        String blockDesc = I18n.get("oldswing.config.block_swing_description");
        String swordTitle = I18n.get("oldswing.config.sword_swing_speed");
        String swordDesc = I18n.get("oldswing.config.sword_swing_description");
        String toolTitle = I18n.get("oldswing.config.tool_swing_speed");
        String toolDesc = I18n.get("oldswing.config.tool_swing_description");
        String globalTitle = I18n.get("oldswing.config.global_swing_speed");
        String globalDesc = I18n.get("oldswing.config.global_swing_description_1");
        String globalPhoto = I18n.get("oldswing.config.global_swing_description_2");

        this.optionsRowList = this.getRowList();

        this.addButton(this.addRangeTip(1));
        this.addButton(this.addTooltip(itemDesc, 1));
        this.optionsRowList.addBig(new SliderPercentageOption(
                itemTitle,
                ConfigHandler.MIN,
                ConfigHandler.MAX,
                1.0F,
                unused -> (double) ConfigHandler.getSwingSpeed(),
                (unused, value) -> ConfigHandler.setSwingSpeed(value.intValue()),
                (gs, option) -> new StringTextComponent(itemTitle + ": " + getRangeColor((int) option.get(gs)))
        ));

        this.addButton(this.addRangeTip(2));
        this.addButton(this.addTooltip(blockDesc, 2));
        this.optionsRowList.addBig(new SliderPercentageOption(
                blockTitle,
                ConfigHandler.MIN,
                ConfigHandler.MAX,
                1.0F,
                unused -> (double) ConfigHandler.getBlockSpeed(),
                (unused, value) -> ConfigHandler.setBlockSpeed(value.intValue()),
                (gs, option) -> new StringTextComponent(blockTitle + ": " + getRangeColor((int) option.get(gs)))
        ));

        this.addButton(this.addRangeTip(3));
        this.addButton(this.addTooltip(swordDesc, 3));
        this.optionsRowList.addBig(new SliderPercentageOption(
                swordTitle,
                ConfigHandler.MIN,
                ConfigHandler.MAX,
                1.0F,
                unused -> (double) ConfigHandler.getSwordSpeed(),
                (unused, value) -> ConfigHandler.setSwordSpeed(value.intValue()),
                (gs, option) -> new StringTextComponent(swordTitle + ": " + getRangeColor((int) option.get(gs)))
        ));

        this.addButton(this.addRangeTip(4));
        this.addButton(this.addTooltip(toolDesc, 4));
        this.optionsRowList.addBig(new SliderPercentageOption(
                toolTitle,
                ConfigHandler.MIN,
                ConfigHandler.MAX,
                1.0F,
                unused -> (double) ConfigHandler.getToolSpeed(),
                (unused, value) -> ConfigHandler.setToolSpeed(value.intValue()),
                (gs, option) -> new StringTextComponent(toolTitle + ": " + getRangeColor((int) option.get(gs)))
        ));

        this.addButton(this.addRangeTip(5));
        this.addButton(this.addLinedTooltip(5, globalDesc, TextFormatting.YELLOW + globalPhoto));
        this.optionsRowList.addBig(new SliderPercentageOption(
                globalTitle,
                ConfigHandler.MIN,
                ConfigHandler.MAX,
                1.0F,
                unused -> (double) ConfigHandler.getGlobalSpeed(),
                (unused, value) -> ConfigHandler.setGlobalSpeed(value.intValue()),
                (gs, option) -> new StringTextComponent(globalTitle + ": " + getRangeColor((int) option.get(gs)))
        ));

        this.optionsRowList.addBig(new BooleanButton(
                I18n.get("oldswing.config.global_speed"),
                unused -> ConfigHandler.global_speed_enabled,
                (unused, value) ->
                {
                    ConfigHandler.sendSliderSetters();
                    ConfigHandler.toggle(ClientConfig.global_speed_enabled, value);
                }
        ));


        this.optionsRowList.addSmall(ResetOptions.add(this::reset, this::undo));
        this.children.add(this.optionsRowList);
        this.addButton(this.getDoneButton());
    }

    private void reset(Button unused)
    {
        ConfigHandler.sendSliderSetters();
        ConfigHandler.storeCategoricalSwingSpeeds();
        ConfigHandler.resetCategoricalSwingSpeeds();
        Minecraft.getInstance().setScreen(Minecraft.getInstance().screen);
    }

    private void undo(Button unused)
    {
        ConfigHandler.undoCategoricalSwingReset();
        Minecraft.getInstance().setScreen(Minecraft.getInstance().screen);
    }
}
