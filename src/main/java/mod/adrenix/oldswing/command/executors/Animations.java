package mod.adrenix.oldswing.command.executors;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import mod.adrenix.oldswing.config.ClientConfig;
import mod.adrenix.oldswing.config.ConfigHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

public class Animations {
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
        ;
    }

    private static int toggleAnimation(CommandSource source, boolean flag, ForgeConfigSpec.BooleanValue config, String on, String off) {
        if (!ConfigHandler.mod_enabled) {
            source.sendErrorMessage(ITextComponent.func_244388_a("The must be enabled to change animation states."));
            return 0;
        }

        config.set(flag);
        ConfigHandler.bake();

        final String state = flag ? on : off;
        source.sendFeedback(ITextComponent.func_244388_a(state), true);

        return 1;
    }

    private static int toggleCooldown(CommandSource source, boolean flag) {
        String on = "The cooldown animation that plays after a swing or change in slot will now be prevented. This will not modify combat.";
        String off = "A cooldown animation will now play after every swing or change in slot. This will not modify combat.";
        return toggleAnimation(source, flag, ClientConfig.prevent_cooldown, on, off);
    }

    private static int toggleReequipAnimation(CommandSource source, boolean flag) {
        String on = "Reequip animation will no longer play when an item's durability changes.";
        String off = "Reequip animation will now play when an item's durability changes.";
        return toggleAnimation(source, flag, ClientConfig.prevent_reequip, on, off);
    }

    private static int toggleSwayAnimation(CommandSource source, boolean flag) {
        String on = "The subtle arm sway animation that happens when you look around will now be prevented.";
        String off = "You will now see a subtle arm sway animation occur as you look around.";
        return toggleAnimation(source, flag, ClientConfig.prevent_sway, on, off);
    }
}
