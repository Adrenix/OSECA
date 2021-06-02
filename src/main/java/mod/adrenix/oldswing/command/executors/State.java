package mod.adrenix.oldswing.command.executors;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mod.adrenix.oldswing.command.ColorUtil;
import mod.adrenix.oldswing.config.ClientConfig;
import mod.adrenix.oldswing.config.ConfigHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;

public class State {
    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("mod")
            .then(Commands.literal("on")
                .executes(context -> modState(context.getSource(), true))
            )

            .then(Commands.literal("off")
                .executes(context -> modState(context.getSource(), false))
            )
        ;
    }

    private static int modState(CommandSource source, boolean flag) {
        ClientConfig.mod_enabled.set(flag);
        ConfigHandler.bake();

        final String state = I18n.get(flag ? "oldswing.on" : "oldswing.off");
        final String out = I18n.get("oldswing.cmd.state.status",
                ColorUtil.value(state));
        source.sendSuccess(ITextComponent.nullToEmpty(out), true);

        return 1;
    }
}
