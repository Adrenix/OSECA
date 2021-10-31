package mod.adrenix.oldswing.config.gui.screen;

import com.google.common.collect.Lists;
import mod.adrenix.oldswing.config.ConfigHandler;
import mod.adrenix.oldswing.config.gui.widget.Configuration;
import mod.adrenix.oldswing.config.gui.widget.ResetOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
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

    @Override
    public boolean keyPressed(int key, int x, int y)
    {
        if (key == 256 && this.shouldCloseOnEsc())
        {
            this.onClose();
            return true;
        }
        else if (super.keyPressed(key, x, y))
            return true;
        else
            return key == 257 || key == 335;
    }

    public static List<ITextComponent> rangeTip(boolean isDisabled)
    {
        List<ITextComponent> tip = new ArrayList<>();

        String alpha = I18n.get("oldswing.config.range_alpha");
        String modern = I18n.get("oldswing.config.range_modern");
        String disabled = I18n.get("oldswing.config.disabled");
        String photosensitive = I18n.get("oldswing.config.photosensitive");

        tip.add(new StringTextComponent(TextFormatting.GREEN + alpha + TextFormatting.RESET + ": " + TextFormatting.AQUA + ConfigHandler.OLD_SPEED));
        tip.add(new StringTextComponent(TextFormatting.GOLD + modern + TextFormatting.RESET + ": " + TextFormatting.AQUA + ConfigHandler.NEW_SPEED));
        tip.add(new StringTextComponent(TextFormatting.YELLOW + photosensitive + TextFormatting.RESET + ": " + TextFormatting.AQUA + ConfigHandler.DISABLED));

        if (isDisabled)
            tip.add(new StringTextComponent(TextFormatting.RED + disabled + TextFormatting.RESET + ": " + TextFormatting.AQUA + ConfigHandler.GLOBAL));

        return tip;
    }

    public static List<ITextComponent> rangeTip()
    {
        return rangeTip(false);
    }

    public static String getRangeColor(int speed)
    {
        if (speed == ConfigHandler.DISABLED)
            return TextFormatting.YELLOW.toString() + speed;
        else if (speed == ConfigHandler.GLOBAL)
            return TextFormatting.RED.toString() + speed;
        else if (speed <= ConfigHandler.NEW_SPEED)
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

        String fatigueTitle = I18n.get("oldswing.config.fatigue");
        String fatigueDesc1 = I18n.get("oldswing.config.fatigue_description_1");
        String fatigueDesc2 = I18n.get("oldswing.config.fatigue_description_2", TextFormatting.RED, TextFormatting.RESET);

        String hasteTitle = I18n.get("oldswing.config.haste");
        String hasteDesc1 = I18n.get("oldswing.config.haste_description_1");
        String hasteDesc2 = I18n.get("oldswing.config.haste_description_2", TextFormatting.RED, TextFormatting.RESET);

        String globalTitle = I18n.get("oldswing.config.global_swing_speed");
        String globalDesc1 = I18n.get("oldswing.config.global_swing_description_1");
        String globalDesc2 = I18n.get("oldswing.config.global_swing_description_2", TextFormatting.YELLOW, TextFormatting.RESET, TextFormatting.RED, TextFormatting.RESET);
        String globalDesc3 = I18n.get("oldswing.config.global_swing_description_3", TextFormatting.RED, TextFormatting.RESET);

        Configuration.Slider items = new Configuration.Slider(
                itemTitle,
                false,
                (settings) -> (double) ConfigHandler.getSwingSpeed(),
                (settings, value) -> ConfigHandler.setSwingSpeed(value.intValue())
        );

        Configuration.Slider blocks = new Configuration.Slider(
                blockTitle,
                false,
                (settings) -> (double) ConfigHandler.getBlockSpeed(),
                (settings, value) -> ConfigHandler.setBlockSpeed(value.intValue())
        );

        Configuration.Slider swords = new Configuration.Slider(
                swordTitle,
                false,
                (settings) -> (double) ConfigHandler.getSwordSpeed(),
                (settings, value) -> ConfigHandler.setSwordSpeed(value.intValue())
        );

        Configuration.Slider tools = new Configuration.Slider(
                toolTitle,
                false,
                (settings) -> (double) ConfigHandler.getToolSpeed(),
                (settings, value) -> ConfigHandler.setToolSpeed(value.intValue())
        );

        Configuration.Slider fatigue = new Configuration.Slider(
                fatigueTitle,
                true,
                (settings) -> (double) ConfigHandler.getFatigueSpeed(),
                (settings, value) -> ConfigHandler.setFatigueSpeed(value.intValue())
        );

        Configuration.Slider haste = new Configuration.Slider(
                hasteTitle,
                true,
                (settings) -> (double) ConfigHandler.getHasteSpeed(),
                (settings, value) -> ConfigHandler.setHasteSpeed(value.intValue())
        );

        Configuration.Slider global = new Configuration.Slider(
                globalTitle,
                true,
                (settings) -> (double) ConfigHandler.getGlobalSpeed(),
                (settings, value) -> ConfigHandler.setGlobalSpeed(value.intValue())
        );

        items.setTooltip(itemDesc);
        blocks.setTooltip(blockDesc);
        swords.setTooltip(swordDesc);
        tools.setTooltip(toolDesc);
        fatigue.setTooltip(fatigueDesc1, fatigueDesc2);
        haste.setTooltip(hasteDesc1, hasteDesc2);
        global.setTooltip(globalDesc1, globalDesc2, globalDesc3);

        this.configRowList = this.getRowList(Lists.newArrayList(items, blocks, swords, tools, fatigue, haste, global));
        this.configRowList.addSmall(ResetOptions.add(this::reset, this::undo));
        this.children.add(this.configRowList);
        this.addButton(this.getDoneButton());
    }

    private void reset(Button button)
    {
        ConfigHandler.storeCategoricalSwingSpeeds();
        ConfigHandler.resetCategoricalSwingSpeeds();
        Minecraft.getInstance().setScreen(Minecraft.getInstance().screen);
    }

    private void undo(Button button)
    {
        ConfigHandler.undoCategoricalSwingReset();
        Minecraft.getInstance().setScreen(Minecraft.getInstance().screen);
    }

    @Override
    public void onClose()
    {
        ConfigHandler.sendSliderSetters();
        super.onClose();
    }
}
