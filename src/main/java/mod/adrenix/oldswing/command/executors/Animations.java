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

public class Animations
{
    private static final SuggestionProvider<CommandSource> FLAG_SUGGESTION = (context, builder) ->
            ISuggestionProvider.suggest(new String[]{"true", "false"}, builder);

    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("animation")
            .then(Commands.literal("disableReequip")
                .then(Commands.argument("flag", BoolArgumentType.bool())
                    .suggests(FLAG_SUGGESTION)
                    .executes(context -> toggleReequipAnimation(
                        context.getSource(), BoolArgumentType.getBool(context, "flag")
                    ))
                )
            )

            .then(Commands.literal("disableCooldown")
                .then(Commands.argument("flag", BoolArgumentType.bool())
                    .suggests(FLAG_SUGGESTION)
                    .executes(context -> toggleCooldown(
                        context.getSource(), BoolArgumentType.getBool(context, "flag")
                    ))
                )
            )

            .then(Commands.literal("disableArmSway")
                .then(Commands.argument("flag", BoolArgumentType.bool())
                    .suggests(FLAG_SUGGESTION)
                    .executes(context -> toggleSwayAnimation(
                        context.getSource(), BoolArgumentType.getBool(context, "flag")
                    ))
                )
            )

            .then(Commands.literal("disableSweepParticles")
                .then(Commands.argument("flag", BoolArgumentType.bool())
                    .suggests(FLAG_SUGGESTION)
                    .executes(context -> toggleSweepParticles(
                        context.getSource(), BoolArgumentType.getBool(context, "flag")
                    ))
                )
            )
        ;
    }

    private static int toggleAnimation(CommandSource source, boolean flag, ForgeConfigSpec.BooleanValue config, String on, String off)
    {
        if (!ConfigHandler.mod_enabled)
        {
            source.sendFailure(ITextComponent.nullToEmpty(I18n.get("oldswing.cmd.mod_enabled_animation")));
            return 0;
        }

        ConfigHandler.toggle(config, flag);

        final String state = flag ? on : off;
        source.sendSuccess(ITextComponent.nullToEmpty(state), true);

        return 1;
    }

    private static int toggleCooldown(CommandSource source, boolean flag)
    {
        String on = I18n.get("oldswing.cmd.cooldown.on");
        String off = I18n.get("oldswing.cmd.cooldown.off");
        return toggleAnimation(source, flag, ClientConfig.prevent_cooldown, on, off);
    }

    private static int toggleReequipAnimation(CommandSource source, boolean flag)
    {
        String on = I18n.get("oldswing.cmd.reequip.on");
        String off = I18n.get("oldswing.cmd.reequip.off");
        return toggleAnimation(source, flag, ClientConfig.prevent_reequip, on, off);
    }

    private static int toggleSwayAnimation(CommandSource source, boolean flag)
    {
        String on = I18n.get("oldswing.cmd.sway.on");
        String off = I18n.get("oldswing.cmd.sway.off");
        return toggleAnimation(source, flag, ClientConfig.prevent_sway, on, off);
    }

    private static int toggleSweepParticles(CommandSource source, boolean flag)
    {
        String on = I18n.get("oldswing.cmd.sweep.on");
        String off = I18n.get("oldswing.cmd.sweep.off");
        return toggleAnimation(source, flag, ClientConfig.prevent_sweep, on, off);
    }
}
