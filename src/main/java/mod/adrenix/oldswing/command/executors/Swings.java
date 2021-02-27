package mod.adrenix.oldswing.command.executors;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import mod.adrenix.oldswing.command.ColorUtil;
import mod.adrenix.oldswing.config.ClientConfig;
import mod.adrenix.oldswing.config.ConfigHandler;
import mod.adrenix.oldswing.config.CustomSwing;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

public class Swings {
    private static final ArrayList<String> VALID_SPEEDS = new ArrayList<>();
    private static final int MIN = ConfigHandler.MIN;
    private static final int MAX = ConfigHandler.MAX;

    static {
        for (int i = MIN; i <= MAX; i++) {
            VALID_SPEEDS.add(Integer.toString(i));
        }
    }

    private static final SuggestionProvider<CommandSource> SPEED_SUGGESTION = (context, builder) ->
            ISuggestionProvider.suggest(VALID_SPEEDS, builder);

    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("swing")
                .then(Commands.literal("items")
                        .then(Commands.argument("speed", IntegerArgumentType.integer())
                                .suggests(SPEED_SUGGESTION)
                                .executes(context -> changeSwingSpeed(
                                        context.getSource(), IntegerArgumentType.getInteger(context, "speed"), "items"
                                ))
                        )
                )

                .then(Commands.literal("swords")
                        .then(Commands.argument("speed", IntegerArgumentType.integer())
                                .suggests(SPEED_SUGGESTION)
                                .executes(context -> changeSwingSpeed(
                                        context.getSource(), IntegerArgumentType.getInteger(context, "speed"), "swords"
                                ))
                        )
                )

                .then(Commands.literal("tools")
                        .then(Commands.argument("speed", IntegerArgumentType.integer())
                                .suggests(SPEED_SUGGESTION)
                                .executes(context -> changeSwingSpeed(
                                        context.getSource(), IntegerArgumentType.getInteger(context, "speed"), "tools"
                                ))
                        )
                )

                .then(Commands.literal("custom")
                        .then(Commands.argument("item", ItemArgument.item())
                                .then(Commands.argument("speed", IntegerArgumentType.integer())
                                        .suggests(SPEED_SUGGESTION)
                                        .executes(context -> addCustomSwing(
                                                context.getSource(),
                                                ItemArgument.getItem(context, "item"),
                                                IntegerArgumentType.getInteger(context, "speed")
                                        ))
                                )
                        )
                );
    }

    private static int rangeError(CommandSource source) {
        source.sendErrorMessage(ITextComponent.func_244388_a(String.format("Swing speed out of range! [Range: %d ~ %d]", MIN, MAX)));
        return 0;
    }

    private static int changeSwingSpeed(CommandSource source, int speed, String on) {
        if (speed < MIN || speed > MAX) {
            return rangeError(source);
        }

        switch (on) {
            case "items":
                ClientConfig.swing_speed.set(speed);
                break;
            case "swords":
                ClientConfig.sword_speed.set(speed);
                break;
            case "tools":
                ClientConfig.tool_speed.set(speed);
                break;
        }

        ConfigHandler.bake();

        final String oldSwing = String.format("%s: %s",
                ColorUtil.format("Alpha/Beta Minecraft", TextFormatting.LIGHT_PURPLE),
                ColorUtil.format("8", TextFormatting.YELLOW)
        );

        final String newSwing = String.format("%s: %s",
                ColorUtil.format("Modern Minecraft", TextFormatting.LIGHT_PURPLE),
                ColorUtil.format("6", TextFormatting.YELLOW)
        );

        final String info = String.format("%s\n%s\nSuccessfully changed %s swing speed to: %s.",
                oldSwing, newSwing, ColorUtil.format(on, TextFormatting.GOLD), ColorUtil.format(Integer.toString(speed), TextFormatting.AQUA));
        source.sendFeedback(ITextComponent.func_244388_a(info), true);

        return 1;
    }

    private static int addCustomSwing(CommandSource source, ItemInput itemIn, int speed) {
        Item item = itemIn.getItem();
        ResourceLocation resource = ForgeRegistries.ITEMS.getKey(item);

        if (resource == null) {
            source.sendErrorMessage(ITextComponent.func_244388_a("Item [" + itemIn.toString() + "] does not exist in the forge item registry."));
            return 0;
        } else if (speed < MIN || speed > MAX) {
            return rangeError(source);
        }

        CustomSwing.add(resource.toString(), speed);
        ConfigHandler.bake();

        final String out = String.format("Successfully added [%s] with speed: %s",
                ColorUtil.format(resource.toString(), TextFormatting.GREEN),
                ColorUtil.format(String.valueOf(speed), TextFormatting.YELLOW));
        source.sendFeedback(ITextComponent.func_244388_a(out), true);

        return 1;
    }
}
