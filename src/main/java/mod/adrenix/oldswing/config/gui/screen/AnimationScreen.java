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

public class AnimationScreen extends ConfigScreen
{
    public AnimationScreen(Screen parentScreen)
    {
        super(I18n.get("oldswing.config.animation_settings"), parentScreen);
    }

    @Override
    protected void init()
    {
        Configuration.Toggle cooldown = new Configuration.Toggle(
                I18n.get("oldswing.config.cooldown_animation"),
                (settings) -> !ConfigHandler.prevent_cooldown,
                (settings, value) -> ConfigHandler.toggle(ClientConfig.prevent_cooldown, !value)
        );

        Configuration.Toggle reequip = new Configuration.Toggle(
                I18n.get("oldswing.config.reequip_animation"),
                (settings) -> !ConfigHandler.prevent_reequip,
                (settings, value) -> ConfigHandler.toggle(ClientConfig.prevent_reequip, !value)
        );

        Configuration.Toggle sweep = new Configuration.Toggle(
                I18n.get("oldswing.config.sweep_particles"),
                (settings) -> !ConfigHandler.prevent_sweep,
                (settings, value) -> ConfigHandler.toggle(ClientConfig.prevent_sweep, !value)
        );

        Configuration.Toggle sway = new Configuration.Toggle(
                I18n.get("oldswing.config.arm_sway"),
                (settings) -> !ConfigHandler.prevent_sway,
                (settings, value) -> ConfigHandler.toggle(ClientConfig.prevent_sway, !value)
        );

        Configuration.Toggle bobbing = new Configuration.Toggle(
                I18n.get("oldswing.config.bob_vertical"),
                (settings) -> !ConfigHandler.prevent_bob_vertical,
                (settings, value) -> ConfigHandler.toggle(ClientConfig.prevent_bob_vertical, !value)
        );

        Configuration.Toggle sneak = new Configuration.Toggle(
                I18n.get("oldswing.config.smooth_sneak"),
                (settings) -> !ConfigHandler.prevent_smooth_sneak,
                (settings, value) -> ConfigHandler.toggle(ClientConfig.prevent_smooth_sneak, !value)
        );

        Configuration.Toggle disintegrate = new Configuration.Toggle(
                I18n.get("oldswing.config.tool_disintegration"),
                (settings) -> !ConfigHandler.prevent_tool_disintegration,
                (settings, value) -> ConfigHandler.toggle(ClientConfig.prevent_tool_disintegration, !value)
        );

        Configuration.Toggle drop = new Configuration.Toggle(
                I18n.get("oldswing.config.drop"),
                (settings) -> !ConfigHandler.prevent_swing_drop,
                (settings, value) -> ConfigHandler.toggle(ClientConfig.prevent_swing_drop, !value)
        );

        cooldown.setTooltip(I18n.get("oldswing.config.cooldown_description"));
        reequip.setTooltip(I18n.get("oldswing.config.reequip_description"));
        sweep.setTooltip(I18n.get("oldswing.config.sweep_particles_description"));
        sway.setTooltip(I18n.get("oldswing.config.arm_sway_description"));
        bobbing.setTooltip(I18n.get("oldswing.config.bob_vertical_description"));
        sneak.setTooltip(I18n.get("oldswing.config.smooth_sneak_description"));
        disintegrate.setTooltip(I18n.get("oldswing.config.tool_disintegration_description"));
        drop.setTooltip(I18n.get("oldswing.config.drop_description"));

        this.configRowList = this.getRowList(Lists.newArrayList(cooldown, reequip, sweep, sway, bobbing, sneak, disintegrate, drop));
        this.configRowList.addSmall(ResetOptions.add(this::reset, this::undo));
        this.children.add(this.configRowList);
        this.addButton(this.getDoneButton());
    }

    private void reset(Button button)
    {
        ConfigHandler.storeAnimationValues();
        ConfigHandler.resetAnimationValues();
        Minecraft.getInstance().setScreen(Minecraft.getInstance().screen);
    }

    private void undo(Button button)
    {
        ConfigHandler.undoAnimationReset();
        Minecraft.getInstance().setScreen(Minecraft.getInstance().screen);
    }
}
