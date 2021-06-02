package mod.adrenix.oldswing.command.executors;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mod.adrenix.oldswing.config.gui.screen.ConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class Config {
    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("config")
            .executes(context -> openConfig(context.getSource()))
        ;
    }

    private static int openConfig(CommandSource source) {
        KeyBinding openConfigKey = null;
        Minecraft.getInstance().setScreen(new ConfigScreen(Minecraft.getInstance().screen));

        for (KeyBinding binding : Minecraft.getInstance().options.keyMappings) {
            if (binding.getCategory().equals("oldswing.title")) {
                if (binding.getName().equals("oldswing.key.open_config")) {
                    openConfigKey = binding;
                    break;
                }
            }
        }

        if (openConfigKey != null) {
            String key = TextFormatting.YELLOW + openConfigKey.getKey().getDisplayName().getString() + TextFormatting.RESET;
            String out = I18n.get("oldswing.chat.open_config", key);
            source.sendSuccess(ITextComponent.nullToEmpty(out), true);
        }

        return 1;
    }
}
