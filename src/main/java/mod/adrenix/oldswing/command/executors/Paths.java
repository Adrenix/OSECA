package mod.adrenix.oldswing.command.executors;

import com.electronwill.nightconfig.core.Config;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import mod.adrenix.oldswing.command.ColorUtil;
import mod.adrenix.oldswing.config.ClientConfig;
import mod.adrenix.oldswing.config.ConfigHandler;
import mod.adrenix.oldswing.config.CustomSwing;
import mod.adrenix.oldswing.MixinInjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.item.Item;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Map;

public class Paths
{
    private static final String[] PATH_KEYS = { "animations", "candy", "categories", "items", "holding" };
    private static final SuggestionProvider<CommandSource> CONFIG_SUGGESTION = (context, builder) ->
            ISuggestionProvider.suggest(PATH_KEYS, builder);

    public static RequiredArgumentBuilder<CommandSource, String> register()
    {
         return Commands.argument("path", StringArgumentType.string())
            .suggests(CONFIG_SUGGESTION)
            .executes(context -> getConfigurationStates(
                context.getSource(), StringArgumentType.getString(context, "path"))
            )
         ;
    }

    private static int getConfigurationStates(CommandSource source, String path)
    {
        if (!ConfigHandler.mod_enabled)
        {
            source.sendFailure(ITextComponent.nullToEmpty(I18n.get("oldswing.cmd.mod_enabled_paths")));
            return 0;
        }

        if (path.equals(PATH_KEYS[0]))
        {
            // Modded animations
            for (Map.Entry<String, ForgeConfigSpec.ConfigValue<?>> entry : ClientConfig.ANIMATIONS.entrySet())
            {
                ForgeConfigSpec.ConfigValue<?> value = entry.getValue();

                final String out = I18n.get("oldswing.cmd.paths.config",
                        ColorUtil.format(String.valueOf(value.getPath()), TextFormatting.LIGHT_PURPLE), ColorUtil.value(value.get().toString()));
                source.sendSuccess(ITextComponent.nullToEmpty(out), true);
            }

            return 1;
        }
        else if (path.equals(PATH_KEYS[1]))
        {
            // Eye candy
            for (Map.Entry<String, ForgeConfigSpec.ConfigValue<?>> entry : ClientConfig.EYE_CANDY.entrySet())
            {
                ForgeConfigSpec.ConfigValue<?> value = entry.getValue();

                final String out = I18n.get("oldswing.cmd.paths.config",
                        ColorUtil.format(String.valueOf(value.getPath()), TextFormatting.LIGHT_PURPLE), ColorUtil.value(value.get().toString()));
                source.sendSuccess(ITextComponent.nullToEmpty(out), true);
            }

            return 1;
        }
        else if (path.equals(PATH_KEYS[2]))
        {
            // Categorical swinging speeds
            for (Map.Entry<String, ForgeConfigSpec.IntValue> entry : ClientConfig.SPEEDS.entrySet())
            {
                ForgeConfigSpec.IntValue value = entry.getValue();

                final String out = I18n.get("oldswing.cmd.paths.category",
                        ColorUtil.format(String.valueOf(entry.getKey()), TextFormatting.LIGHT_PURPLE),
                        ColorUtil.format(String.valueOf(value.get()), TextFormatting.YELLOW));
                source.sendSuccess(ITextComponent.nullToEmpty(out), true);
            }

            return 1;
        }
        else if (path.equals(PATH_KEYS[3]))
        {
            // Individual defined swing speeds
            int counter = 0;

            for (Config.Entry entry : ConfigHandler.custom_speeds.entrySet())
            {
                if (counter == 9)
                {
                    source.sendSuccess(ITextComponent.nullToEmpty(
                            ColorUtil.format(I18n.get("oldswing.cmd.paths.remaining"), TextFormatting.ITALIC)), true);
                    break;
                }

                final String out = I18n.get("oldswing.cmd.paths.custom",
                        ColorUtil.format(CustomSwing.getRegistryName(CustomSwing.key(entry)), TextFormatting.LIGHT_PURPLE),
                        ColorUtil.format(entry.getValue().toString(), TextFormatting.YELLOW));
                source.sendSuccess(ITextComponent.nullToEmpty(out), true);

                counter++;
            }

            if (counter == 0)
            {
                source.sendFailure(ITextComponent.nullToEmpty(I18n.get("oldswing.cmd.paths.no_custom")));
                return 0;
            }

            return 1;
        }
        else if (path.equals(PATH_KEYS[4]))
        {
            // Checks swinging speed of item being held
            ClientPlayerEntity entity = Minecraft.getInstance().player;

            if (entity != null)
            {
                Item item = entity.getMainHandItem().getItem();
                String name = item.getRegistryName() != null ? item.getName(item.getDefaultInstance()).getString() : I18n.get("oldswing.unknown");
                int speed = MixinInjector.getSwingSpeed(entity);

                final String out = I18n.get("oldswing.cmd.paths.holding",
                        ColorUtil.format(name, TextFormatting.LIGHT_PURPLE),
                        ColorUtil.format(String.valueOf(speed), TextFormatting.YELLOW));
                source.sendSuccess(ITextComponent.nullToEmpty(out), true);
                return 1;
            }
            else
            {
                source.sendFailure(ITextComponent.nullToEmpty(I18n.get("oldswing.cmd.not_player")));
                return 0;
            }
        }

        source.sendFailure(ITextComponent.nullToEmpty(I18n.get("oldswing.cmd.paths.unknown")));
        return 0;
    }
}
