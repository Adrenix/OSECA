package mod.adrenix.oldswing.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mod.adrenix.oldswing.OldSwing;
import mod.adrenix.oldswing.command.executors.*;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class CommandRegistry
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        final LiteralArgumentBuilder<CommandSource> builder = Commands.literal(OldSwing.MOD_ID);

        builder
            .then(
                Commands.literal("set")
                    .then(Swings.register())
                    .then(Animations.register())
                    .then(State.register())
                    .then(Candy.register())
            )
            .then(
                Commands.literal("get")
                    .then(Paths.register())
            )
            .then(
                Config.register()
            )
        ;

        dispatcher.register(builder);
    }
}
