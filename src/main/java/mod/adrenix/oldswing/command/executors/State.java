package mod.adrenix.oldswing.command.executors;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mod.adrenix.oldswing.command.ColorUtil;
import mod.adrenix.oldswing.config.ClientConfig;
import mod.adrenix.oldswing.config.ConfigHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;

public class State {
    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("mod")
                .then(Commands.literal("enable")
                        .executes(context -> modState(context.getSource(), true))
                )

                .then(Commands.literal("disable")
                        .executes(context -> modState(context.getSource(), false))
                );
    }

    private static int modState(CommandSource source, boolean flag) {
        ClientConfig.mod_enabled.set(flag);
        ConfigHandler.bake();

        final String out = String.format("OldSwing enabled: %s",
                ColorUtil.value(String.valueOf(flag)));
        source.sendFeedback(ITextComponent.func_244388_a(out), true);

        return 1;
    }
}
