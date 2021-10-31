package mod.adrenix.oldswing.config.gui.screen;

import com.google.common.collect.Lists;
import mod.adrenix.oldswing.config.ClientConfig;
import mod.adrenix.oldswing.config.ConfigHandler;
import mod.adrenix.oldswing.config.gui.widget.Configuration;
import mod.adrenix.oldswing.config.gui.widget.ResetOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class CandyScreen extends ConfigScreen
{
    public CandyScreen(Screen parent)
    {
        super(I18n.get("oldswing.config.eye_candy_settings"), parent);
    }

    @Override
    protected void init()
    {
        Configuration.Toggle colors = new Configuration.Toggle(
                I18n.get("oldswing.config.old_damage_colors"),
                (settings) -> ConfigHandler.old_damage_colors,
                (settings, value) -> ConfigHandler.toggle(ClientConfig.old_damage_colors, value)
        );

        Configuration.Toggle holding = new Configuration.Toggle(
                I18n.get("oldswing.config.old_item_holding"),
                (settings) -> ConfigHandler.old_item_holding,
                (settings, value) -> ConfigHandler.toggle(ClientConfig.old_item_holding, value)
        );

        Configuration.Toggle floating = new Configuration.Toggle(
                I18n.get("oldswing.config.old_2d_items"),
                (settings) -> ConfigHandler.old_2d_items,
                (settings, value) -> ConfigHandler.toggle(ClientConfig.old_2d_items, value)
        );

        Configuration.Toggle flicker = new Configuration.Toggle(
                I18n.get("oldswing.config.old_light_flicker"),
                (settings) -> ConfigHandler.old_light_flicker,
                (settings, value) -> ConfigHandler.toggle(ClientConfig.old_light_flicker, value)
        );

        Configuration.Toggle tooltips = new Configuration.Toggle(
                I18n.get("oldswing.config.old_tooltips"),
                (settings) -> ConfigHandler.old_tooltip_boxes,
                (settings, value) -> ConfigHandler.toggle(ClientConfig.old_tooltip_boxes, value)
        );

        colors.setTooltip(I18n.get("oldswing.config.old_damage_colors_description"));
        holding.setTooltip(I18n.get("oldswing.config.old_item_holding_description"));
        floating.setTooltip(I18n.get("oldswing.config.old_2d_items_description", TextFormatting.RED, TextFormatting.RESET));
        flicker.setTooltip(I18n.get("oldswing.config.old_light_flicker_description_1"), " ", I18n.get("oldswing.config.old_light_flicker_description_2", TextFormatting.YELLOW));
        tooltips.setTooltip(I18n.get("oldswing.config.old_tooltips_description"));

        this.configRowList = this.getRowList(Lists.newArrayList(colors, holding, floating, flicker, tooltips));
        this.configRowList.addSmall(ResetOptions.add(this::reset, this::undo));
        this.children.add(this.configRowList);
        this.addButton(this.getDoneButton());
    }

    private void reset(Button button)
    {
        ConfigHandler.storeCandyValues();
        ConfigHandler.resetCandyValues();
        Minecraft.getInstance().setScreen(Minecraft.getInstance().screen);
    }

    private void undo(Button button)
    {
        ConfigHandler.undoCandyReset();
        Minecraft.getInstance().setScreen(Minecraft.getInstance().screen);
    }
}
