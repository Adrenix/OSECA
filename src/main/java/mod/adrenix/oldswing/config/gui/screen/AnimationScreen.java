package mod.adrenix.oldswing.config.gui.screen;

import mod.adrenix.oldswing.config.ClientConfig;
import mod.adrenix.oldswing.config.ConfigHandler;
import mod.adrenix.oldswing.config.gui.widget.BooleanButton;
import mod.adrenix.oldswing.config.gui.widget.ResetOptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;

public class AnimationScreen extends ConfigScreen {
    public AnimationScreen(Screen parentScreen) {
        super(I18n.get("oldswing.config.animation_settings"), parentScreen);
    }

    @Override
    protected void init() {
        this.optionsRowList = this.getRowList();

        this.addButton(addTooltip(I18n.get("oldswing.config.cooldown_description"), 1));
        this.optionsRowList.addBig(new BooleanButton(
                I18n.get("oldswing.config.cooldown_animation"),
                unused -> !ConfigHandler.prevent_cooldown,
                (unused, value) -> ConfigHandler.toggle(ClientConfig.prevent_cooldown, !value)
        ));

        this.addButton(addTooltip(I18n.get("oldswing.config.reequip_description"), 2));
        this.optionsRowList.addBig(new BooleanButton(
                I18n.get("oldswing.config.reequip_animation"),
                unused -> !ConfigHandler.prevent_reequip,
                (unused, value) -> ConfigHandler.toggle(ClientConfig.prevent_reequip, !value)
        ));

        this.addButton(addTooltip(I18n.get("oldswing.config.arm_sway_description"), 3));
        this.optionsRowList.addBig(new BooleanButton(
                I18n.get("oldswing.config.arm_sway"),
                unused -> !ConfigHandler.prevent_sway,
                (unused, value) -> ConfigHandler.toggle(ClientConfig.prevent_sway, !value)
        ));

        this.optionsRowList.addSmall(ResetOptions.add(this::reset, this::undo));
        this.children.add(this.optionsRowList);
        this.addButton(this.getDoneButton());
    }

    private void reset(Button unused) {
        ConfigHandler.storeAnimationValues();
        ConfigHandler.resetAnimationValues();
        Minecraft.getInstance().setScreen(Minecraft.getInstance().screen);
    }

    private void undo(Button unused) {
        ConfigHandler.undoAnimationReset();
        Minecraft.getInstance().setScreen(Minecraft.getInstance().screen);
    }
}
