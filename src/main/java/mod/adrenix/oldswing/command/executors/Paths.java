package mod.adrenix.oldswing.command.executors;

import com.electronwill.nightconfig.core.Config;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import mod.adrenix.oldswing.command.ColorUtil;
import mod.adrenix.oldswing.config.ClientConfig;
import mod.adrenix.oldswing.config.ConfigHandler;
import mod.adrenix.oldswing.config.CustomSwing;
import mod.adrenix.oldswing.config.TransformerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class Paths {
    private static final String[] PATH_KEYS = { "animations", "categories", "items", "holding" };
    private static final SuggestionProvider<CommandSource> CONFIG_SUGGESTION = (context, builder) ->
            ISuggestionProvider.suggest(PATH_KEYS, builder);

    public static RequiredArgumentBuilder<CommandSource, String> register() {
         return Commands.argument("path", StringArgumentType.string())
            .suggests(CONFIG_SUGGESTION)
            .executes(context -> getConfigurationStates(
                context.getSource(), StringArgumentType.getString(context, "path"))
            )
         ;
    }

    private static int getConfigurationStates(CommandSource source, String path) {
        if (!ConfigHandler.mod_enabled) {
            source.sendErrorMessage(ITextComponent.func_244388_a("The mod must be enabled to see config values."));
            return 0;
        }

        if (path.equals(PATH_KEYS[0])) {
            // Modded animations
            for (Map.Entry<String, ForgeConfigSpec.ConfigValue<?>> entry : ClientConfig.ANIMATIONS.entrySet()) {
                ForgeConfigSpec.ConfigValue<?> value = entry.getValue();

                final String out = String.format("Config option %s is %s.",
                        ColorUtil.format(String.valueOf(value.getPath()), TextFormatting.LIGHT_PURPLE), ColorUtil.value(value.get().toString()));
                source.sendFeedback(ITextComponent.func_244388_a(out), true);
            }

            return 1;
        } else if (path.equals(PATH_KEYS[1])) {
            // Categorical swinging speeds
            for (Map.Entry<String, ForgeConfigSpec.IntValue> entry : ClientConfig.SPEEDS.entrySet()) {
                ForgeConfigSpec.IntValue value = entry.getValue();

                final String out = String.format("Swing speeds for %s has a swing speed of %s.",
                        ColorUtil.format(String.valueOf(entry.getKey()), TextFormatting.LIGHT_PURPLE),
                        ColorUtil.format(String.valueOf(value.get()), TextFormatting.YELLOW));
                source.sendFeedback(ITextComponent.func_244388_a(out), true);
            }

            return 1;
        } else if (path.equals(PATH_KEYS[2])) {
            // Individual defined swing speeds
            int counter = 0;

            for (Config.Entry entry : ConfigHandler.custom_speeds.entrySet()) {
                if (counter == 15) {
                    source.sendFeedback(ITextComponent.func_244388_a(
                            ColorUtil.format("...remaining items can be found in the config file.", TextFormatting.ITALIC)), true);
                    break;
                }

                String key = CustomSwing.key(entry);
                boolean isResource = false;

                for (ResourceLocation resource : ForgeRegistries.ITEMS.getKeys()) {
                    if (key.equals(resource.toString())) {
                        isResource = true;
                        break;
                    }
                }

                Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryCreate(key));
                String info = isResource && item != null ? item.getName().getString() : "unknown";

                final String out = String.format("%s has a swing speed of %s.",
                        ColorUtil.format(info, TextFormatting.LIGHT_PURPLE),
                        ColorUtil.format(entry.getValue().toString(), TextFormatting.YELLOW));
                source.sendFeedback(ITextComponent.func_244388_a(out), true);

                counter++;
            }

            if (counter == 0) {
                source.sendErrorMessage(ITextComponent.func_244388_a("There are no items with custom swing speeds."));
                return 0;
            }

            return 1;
        } else if (path.equals(PATH_KEYS[3])) {
            // Checks swinging speed of item being held
            ClientPlayerEntity entity = Minecraft.getInstance().player;

            if (entity != null) {
                String name = entity.getHeldItemMainhand().getItem().getName().getString();
                int speed = TransformerHelper.swingSpeed(entity);

                final String out = String.format("%s has a swing speed of %s.",
                        ColorUtil.format(name, TextFormatting.LIGHT_PURPLE),
                        ColorUtil.format(String.valueOf(speed), TextFormatting.YELLOW));
                source.sendFeedback(ITextComponent.func_244388_a(out), true);
                return 1;
            } else {
                source.sendErrorMessage(ITextComponent.func_244388_a("Calling source is not a client player entity."));
                return 0;
            }
        }

        source.sendErrorMessage(ITextComponent.func_244388_a("Unknown configuration path."));

        return 0;
    }
}
