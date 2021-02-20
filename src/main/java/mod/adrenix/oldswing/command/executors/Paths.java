package mod.adrenix.oldswing.command.executors;

import com.electronwill.nightconfig.core.Config;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import mod.adrenix.oldswing.command.ColorUtil;
import mod.adrenix.oldswing.config.ClientConfig;
import mod.adrenix.oldswing.config.ConfigHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Map;

public class Paths {
    private static final SuggestionProvider<CommandSource> CONFIG_SUGGESTION = (context, builder) ->
            ISuggestionProvider.suggest(new String[]{"animations", "swings", "items"}, builder);

    public static RequiredArgumentBuilder<CommandSource, String> register() {
         return Commands.argument("path", StringArgumentType.string())
                .suggests(CONFIG_SUGGESTION)
                .executes(context -> getConfigurationStates(
                        context.getSource(), StringArgumentType.getString(context, "path"))
                );
    }

    private static int getConfigurationStates(CommandSource source, String path) {
        if (!ConfigHandler.mod_enabled) {
            source.sendErrorMessage(ITextComponent.func_244388_a("The mod must be enabled to see config values."));
            return 0;
        }

        switch (path) {
            case "animations":
                for (Map.Entry<String, ForgeConfigSpec.ConfigValue<?>> entry : ClientConfig.ANIMATIONS.entrySet()) {
                    ForgeConfigSpec.ConfigValue<?> value = entry.getValue();

                    final String out = String.format("Config option %s has value of: %s",
                            ColorUtil.format(String.valueOf(value.getPath()), TextFormatting.LIGHT_PURPLE), ColorUtil.value(value.get().toString()));
                    source.sendFeedback(ITextComponent.func_244388_a(out), true);
                }

                return 1;
            case "swings":
                for (Map.Entry<String, ForgeConfigSpec.IntValue> entry : ClientConfig.SPEEDS.entrySet()) {
                    ForgeConfigSpec.IntValue value = entry.getValue();

                    final String out = String.format("Swing speeds for %s has a swing speed of: %s",
                            ColorUtil.format(String.valueOf(entry.getKey()), TextFormatting.LIGHT_PURPLE),
                            ColorUtil.format(String.valueOf(value.get()), TextFormatting.YELLOW));
                    source.sendFeedback(ITextComponent.func_244388_a(out), true);
                }

                return 1;
            case "items":
                int counter = 0;
                for (Config.Entry entry : ConfigHandler.custom_speeds.entrySet()) {
                    counter++; if (counter == 15) {
                        source.sendFeedback(ITextComponent.func_244388_a(
                                ColorUtil.format("...remaining items can be found in config file.", TextFormatting.ITALIC)), true);
                        break;
                    }

                    final String out = String.format("Item [%s] has swing speed of: %s",
                            ColorUtil.format(String.valueOf(entry.getKey()), TextFormatting.LIGHT_PURPLE),
                            ColorUtil.format(entry.getValue().toString(), TextFormatting.YELLOW));
                    source.sendFeedback(ITextComponent.func_244388_a(out), true);
                }

                if (counter == 0) {
                    source.sendErrorMessage(ITextComponent.func_244388_a("There are no items with custom swing speeds."));
                    return 0;
                }

                return 1;
        }

        source.sendErrorMessage(ITextComponent.func_244388_a("Unknown configuration path."));

        return 0;
    }
}
