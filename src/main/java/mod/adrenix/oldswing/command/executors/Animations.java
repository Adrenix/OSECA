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
                );
    }

    private static int toggleCooldown(CommandSource source, boolean flag) {
        ClientConfig.prevent_cooldown.set(flag);
        ConfigHandler.bake();

        final String state = flag ?
                "The cooldown animation that plays after a swing or change in slot will now be prevented. This will not modify combat." :
                "A cooldown animation will now play after every swing or change in slot. This will not modify combat.";
        source.sendFeedback(ITextComponent.func_244388_a(state), true);

        return 1;
    }

    private static int toggleReequipAnimation(CommandSource source, boolean flag) {
        ClientConfig.prevent_reequip.set(flag);
        ConfigHandler.bake();

        final String state = flag ?
                "Reequip animation will no longer play when an item's durability changes." :
                "Reequip animation will now play when an item's durability changes.";
        source.sendFeedback(ITextComponent.func_244388_a(state), true);

        return 1;
    }

    private static int toggleSwayAnimation(CommandSource source, boolean flag) {
        ClientConfig.prevent_sway.set(flag);
        ConfigHandler.bake();

        final String state = flag ?
                "The subtle arm sway animation that happens when you look around will now be prevented." :
                "You will now see a subtle arm sway animation occur as you look around.";
        source.sendFeedback(ITextComponent.func_244388_a(state), true);

        return 1;
    }
}
