package mod.adrenix.oldswing.command.executors;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import mod.adrenix.oldswing.config.ClientConfig;
import mod.adrenix.oldswing.config.ConfigHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

public class Candy
{
    private static final SuggestionProvider<CommandSource> FLAG_SUGGESTION = (context, builder) ->
            ISuggestionProvider.suggest(new String[]{"true", "false"}, builder);

    public static LiteralArgumentBuilder<CommandSource> register()
    {
        return Commands.literal("candy")
            .then(Commands.literal("oldDamageColors")
                .then(Commands.argument("flag", BoolArgumentType.bool())
                    .suggests(FLAG_SUGGESTION)
                    .executes(context -> toggleOldDamageColors(
                        context.getSource(), BoolArgumentType.getBool(context, "flag")
                    ))
                )
            )

            .then(Commands.literal("oldItemHolding")
                .then(Commands.argument("flag", BoolArgumentType.bool())
                    .suggests(FLAG_SUGGESTION)
                    .executes(context -> toggleOldItemHolding(
                        context.getSource(), BoolArgumentType.getBool(context, "flag")
                    ))
                )
            )

            .then(Commands.literal("oldFloatingItems")
                .then(Commands.argument("flag", BoolArgumentType.bool())
                    .suggests(FLAG_SUGGESTION)
                    .executes(context -> toggleOldFloatingItems(
                        context.getSource(), BoolArgumentType.getBool(context, "flag")
                    ))
                )
            )

            .then(Commands.literal("oldLightFlicker")
                .then(Commands.argument("flag", BoolArgumentType.bool())
                    .suggests(FLAG_SUGGESTION)
                    .executes(context -> toggleOldFlickering(
                        context.getSource(), BoolArgumentType.getBool(context, "flag")
                    ))
                )
            )

            .then(Commands.literal("oldTooltips")
                .then(Commands.argument("flag", BoolArgumentType.bool())
                    .suggests(FLAG_SUGGESTION)
                    .executes(context -> toggleOldTooltips(
                        context.getSource(), BoolArgumentType.getBool(context, "flag")
                    ))
                )
            )
        ;
    }

    private static int toggleCandy(CommandSource source, boolean flag, ForgeConfigSpec.BooleanValue config, String on, String off)
    {
        if (!ConfigHandler.mod_enabled)
        {
            source.sendFailure(ITextComponent.nullToEmpty(I18n.get("oldswing.cmd.mod_enabled_candy")));
            return 0;
        }

        ConfigHandler.toggle(config, flag);

        final String state = flag ? on : off;
        source.sendSuccess(ITextComponent.nullToEmpty(state), true);

        return 1;
    }

    private static int toggleOldDamageColors(CommandSource source, boolean flag)
    {
        String on = I18n.get("oldswing.cmd.oldDamage.on");
        String off = I18n.get("oldswing.cmd.oldDamage.off");
        return toggleCandy(source, flag, ClientConfig.old_damage_colors, on, off);
    }

    private static int toggleOldItemHolding(CommandSource source, boolean flag)
    {
        String on = I18n.get("oldswing.cmd.oldHolding.on");
        String off = I18n.get("oldswing.cmd.oldHolding.off");
        return toggleCandy(source, flag, ClientConfig.old_item_holding, on, off);
    }

    private static int toggleOldFloatingItems(CommandSource source, boolean flag)
    {
        String on = I18n.get("oldswing.cmd.oldFloating.on");
        String off = I18n.get("oldswing.cmd.oldFloating.off");
        return toggleCandy(source, flag, ClientConfig.old_2d_items, on, off);
    }

    private static int toggleOldFlickering(CommandSource source, boolean flag)
    {
        String on = I18n.get("oldswing.cmd.oldFlicker.on");
        String off = I18n.get("oldswing.cmd.oldFlicker.off");
        return toggleCandy(source, flag, ClientConfig.old_light_flicker, on, off);
    }

    private static int toggleOldTooltips(CommandSource source, boolean flag)
    {
        String on = I18n.get("oldswing.cmd.oldTooltips.on");
        String off = I18n.get("oldswing.cmd.oldTooltips.off");
        return toggleCandy(source, flag, ClientConfig.old_tooltip_boxes, on, off);
    }
}
