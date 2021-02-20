package mod.adrenix.oldswing.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mod.adrenix.oldswing.OldSwingMod;
import mod.adrenix.oldswing.command.executors.Animations;
import mod.adrenix.oldswing.command.executors.Paths;
import mod.adrenix.oldswing.command.executors.State;
import mod.adrenix.oldswing.command.executors.Swings;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class CommandRegistry {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        final LiteralArgumentBuilder<CommandSource> builder = Commands.literal(OldSwingMod.MOD_ID);

        builder
                .then(
                        Commands.literal("set")
                                .then(Swings.register())
                                .then(Animations.register())
                                .then(State.register())
                )
                .then(
                        Commands.literal("get")
                                .then(Paths.register())
                );

        dispatcher.register(builder);
    }
}
