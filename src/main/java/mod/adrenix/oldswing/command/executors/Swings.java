package mod.adrenix.oldswing.command.executors;

import com.electronwill.nightconfig.core.Config;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import mod.adrenix.oldswing.command.ColorUtil;
import mod.adrenix.oldswing.config.ClientConfig;
import mod.adrenix.oldswing.config.ConfigHandler;
import mod.adrenix.oldswing.config.CustomSwing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
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
        for (int i = MIN; i <= MAX; i++)
            VALID_SPEEDS.add(Integer.toString(i));
    }

    private static final SuggestionProvider<CommandSource> SPEED_SUGGESTION = (context, builder) ->
            ISuggestionProvider.suggest(VALID_SPEEDS, builder);

    private static final String[] SWING_KEYS = { "all", "items", "swords", "tools", "holding", "blocks", "custom" };

    private static SuggestionProvider<CommandSource> removeSuggestion() {
        return ((context, builder) -> {
            String[] suggestions = new String[ConfigHandler.custom_speeds.size()];

            if (ConfigHandler.custom_speeds.size() == 0) {
                suggestions = new String[] { "you have no custom swing speeds" };
            }

            int index = 0;
            for (Config.Entry entry : ConfigHandler.custom_speeds.entrySet()) {
                suggestions[index] = String.format("\"%s\"", entry.getKey());
                index++;
            }

            return ISuggestionProvider.suggest(suggestions, builder);
        });
    }

    public static LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("swing")
            .then(Commands.literal(SWING_KEYS[0])
                .then(Commands.argument("speed", IntegerArgumentType.integer())
                    .suggests(SPEED_SUGGESTION)
                    .executes(context -> changeSwingSpeed(
                            context.getSource(), IntegerArgumentType.getInteger(context, "speed"), SWING_KEYS[0]
                    ))
                )
            )

            .then(Commands.literal(SWING_KEYS[1])
                .then(Commands.argument("speed", IntegerArgumentType.integer())
                    .suggests(SPEED_SUGGESTION)
                    .executes(context -> changeSwingSpeed(
                            context.getSource(), IntegerArgumentType.getInteger(context, "speed"), SWING_KEYS[1]
                    ))
                )
            )

            .then(Commands.literal(SWING_KEYS[2])
                .then(Commands.argument("speed", IntegerArgumentType.integer())
                    .suggests(SPEED_SUGGESTION)
                    .executes(context -> changeSwingSpeed(
                            context.getSource(), IntegerArgumentType.getInteger(context, "speed"), SWING_KEYS[2]
                    ))
                )
            )

            .then(Commands.literal(SWING_KEYS[3])
                .then(Commands.argument("speed", IntegerArgumentType.integer())
                    .suggests(SPEED_SUGGESTION)
                    .executes(context -> changeSwingSpeed(
                            context.getSource(), IntegerArgumentType.getInteger(context, "speed"), SWING_KEYS[3]
                    ))
                )
            )

            .then(Commands.literal(SWING_KEYS[4])
                .then(Commands.argument("speed", IntegerArgumentType.integer())
                    .suggests(SPEED_SUGGESTION)
                    .executes(context -> changeSwingSpeed(
                            context.getSource(), IntegerArgumentType.getInteger(context, "speed"), SWING_KEYS[4]
                    ))
                )
            )

            .then(Commands.literal(SWING_KEYS[5])
                .then(Commands.argument("speed", IntegerArgumentType.integer())
                    .suggests(SPEED_SUGGESTION)
                    .executes(context -> changeSwingSpeed(
                            context.getSource(), IntegerArgumentType.getInteger(context, "speed"), SWING_KEYS[5]
                    ))
                )
            )

            .then(Commands.literal(SWING_KEYS[6])
                .then(Commands.literal("add")
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
                )
                .then(Commands.literal("remove")
                    .then(Commands.argument("item", StringArgumentType.string())
                        .suggests(removeSuggestion())
                        .executes(context -> removeCustomSwing(
                                context.getSource(),
                                StringArgumentType.getString(context, "item")
                        ))
                    )
                )
            )
        ;
    }

    private static int rangeError(CommandSource source) {
        source.sendErrorMessage(ITextComponent.func_244388_a(String.format("Swing speed is out of range [Range: %d ~ %d]", MIN, MAX)));
        return 0;
    }

    private static int modStateError(CommandSource source) {
        if (ConfigHandler.mod_enabled) {
            return 1;
        }

        source.sendErrorMessage(ITextComponent.func_244388_a("The mod must be enabled to change swing speeds."));
        return 0;
    }

    private static int changeSwingSpeed(CommandSource source, int speed, String on) {
        if (modStateError(source) == 0) {
            return 0;
        }

        if (speed < MIN || speed > MAX)
            return rangeError(source);

        if (on.equals(SWING_KEYS[0])) {
            // Change all item speeds in config

            ClientConfig.swing_speed.set(speed);
            ClientConfig.sword_speed.set(speed);
            ClientConfig.tool_speed.set(speed);
            ClientConfig.block_speed.set(speed);

            for (Config.Entry entry : ConfigHandler.custom_speeds.entrySet()) {
                CustomSwing.add(entry.getKey(), speed);
            }
        } else if (on.equals(SWING_KEYS[1])) {
            // Change swing speeds of items that are not swords or tools

            ClientConfig.swing_speed.set(speed);
        } else if (on.equals(SWING_KEYS[2])) {
            // Change swing speed of swords

            ClientConfig.sword_speed.set(speed);
        } else if (on.equals(SWING_KEYS[3])) {
            // Change swing speed of tools

            ClientConfig.tool_speed.set(speed);
        } else if (on.equals(SWING_KEYS[4])) {
            // Set swing speed of currently held item

            ClientPlayerEntity entity = Minecraft.getInstance().player;

            if (entity != null) {
                ResourceLocation resource = ForgeRegistries.ITEMS.getKey(entity.getHeldItemMainhand().getItem());

                if (resource == null) {
                    source.sendErrorMessage(ITextComponent.func_244388_a("You must be holding something to set a custom speed."));
                    return 0;
                }

                CustomSwing.add(resource.toString(), speed);
                on = entity.getHeldItemMainhand().getItem().getName().getString();
            } else {
                source.sendErrorMessage(ITextComponent.func_244388_a("Calling source is not a client player entity."));
                return 0;
            }
        } else if (on.equals(SWING_KEYS[5])) {
            // Set swing speed when placing blocks

            ClientConfig.block_speed.set(speed);
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

        String plural = "speed";

        if (on.equals(SWING_KEYS[0])) {
            on = "all config";
            plural = "speeds";
        }

        final String info = String.format("%s\n%s\nChanged %s swing %s to %s.",
                oldSwing, newSwing, ColorUtil.format(on, TextFormatting.GOLD), plural, ColorUtil.format(Integer.toString(speed), TextFormatting.AQUA));
        source.sendFeedback(ITextComponent.func_244388_a(info), true);

        return 1;
    }

    private static int addCustomSwing(CommandSource source, ItemInput itemIn, int speed) {
        if (modStateError(source) == 0) {
            return 0;
        }

        Item item = itemIn.getItem();
        ResourceLocation resource = ForgeRegistries.ITEMS.getKey(item);

        if (resource == null) {
            source.sendErrorMessage(ITextComponent.func_244388_a("Item [" + itemIn + "] does not exist in the forge item registry."));
            return 0;
        } else if (speed < MIN || speed > MAX) {
            return rangeError(source);
        }

        CustomSwing.add(resource.toString(), speed);
        ConfigHandler.bake();

        final String out = String.format("Changed swing speed of %s to %s.",
                ColorUtil.format(item.getName().getString(), TextFormatting.GREEN),
                ColorUtil.format(String.valueOf(speed), TextFormatting.YELLOW));
        source.sendFeedback(ITextComponent.func_244388_a(out), true);

        return 1;
    }

    private static int removeCustomSwing(CommandSource source, String item) {
        if (modStateError(source) == 0) {
            return 0;
        }

        String out;

        if (CustomSwing.remove(item)) {
            ConfigHandler.bake();

            out = String.format("Removed %s from custom swing list.", ColorUtil.format(item, TextFormatting.GOLD));
            source.sendFeedback(ITextComponent.func_244388_a(out), true);

            return 1;
        }

        out = String.format("%s does not exist in custom swing list.", item);
        source.sendErrorMessage(ITextComponent.func_244388_a(out));

        return 0;
    }
}
