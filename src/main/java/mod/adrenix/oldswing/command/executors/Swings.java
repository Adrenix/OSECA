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
import net.minecraft.client.resources.I18n;
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

public class Swings
{
    private static final ArrayList<String> VALID_SPEEDS = new ArrayList<>();
    private static final ArrayList<String> GLOBAL_SPEEDS = new ArrayList<>();
    private static final int MIN = ConfigHandler.MIN;
    private static final int MAX = ConfigHandler.MAX;
    private static final int GLOBAL = ConfigHandler.GLOBAL;

    static
    {
        for (int i = MIN; i <= MAX; i++)
            VALID_SPEEDS.add(Integer.toString(i));

        for (int i = GLOBAL; i <= MAX; i++)
            GLOBAL_SPEEDS.add(Integer.toString(i));
    }

    private static final SuggestionProvider<CommandSource> SPEED_SUGGESTION = (context, builder) ->
            ISuggestionProvider.suggest(VALID_SPEEDS, builder);

    private static final SuggestionProvider<CommandSource> GLOBAL_SUGGESTION = (context, builder) ->
            ISuggestionProvider.suggest(GLOBAL_SPEEDS, builder);

    private static final String[] SWING_KEYS = { "global", "items", "swords", "tools", "holding", "blocks", "fatigue", "haste", "custom" };

    private static SuggestionProvider<CommandSource> removeSuggestion()
    {
        return ((context, builder) -> {
            String[] suggestions = new String[ConfigHandler.custom_speeds.size()];

            if (ConfigHandler.custom_speeds.size() == 0)
                suggestions = new String[] { I18n.get("oldswing.cmd.swings.no_custom") };

            int index = 0;

            for (Config.Entry entry : ConfigHandler.custom_speeds.entrySet())
            {
                suggestions[index] = String.format("\"%s\"", entry.getKey());
                index++;
            }

            return ISuggestionProvider.suggest(suggestions, builder);
        });
    }

    public static LiteralArgumentBuilder<CommandSource> register()
    {
        return Commands.literal("swing")
            .then(Commands.literal(SWING_KEYS[0])
                .then(Commands.argument("speed", IntegerArgumentType.integer())
                    .suggests(GLOBAL_SUGGESTION)
                    .executes(context -> changeSwingSpeed(
                            context.getSource(), IntegerArgumentType.getInteger(context, "speed"), SWING_KEYS[0], true
                    ))
                )
            )

            .then(Commands.literal(SWING_KEYS[1])
                .then(Commands.argument("speed", IntegerArgumentType.integer())
                    .suggests(SPEED_SUGGESTION)
                    .executes(context -> changeSwingSpeed(
                            context.getSource(), IntegerArgumentType.getInteger(context, "speed"), SWING_KEYS[1], false
                    ))
                )
            )

            .then(Commands.literal(SWING_KEYS[2])
                .then(Commands.argument("speed", IntegerArgumentType.integer())
                    .suggests(SPEED_SUGGESTION)
                    .executes(context -> changeSwingSpeed(
                            context.getSource(), IntegerArgumentType.getInteger(context, "speed"), SWING_KEYS[2], false
                    ))
                )
            )

            .then(Commands.literal(SWING_KEYS[3])
                .then(Commands.argument("speed", IntegerArgumentType.integer())
                    .suggests(SPEED_SUGGESTION)
                    .executes(context -> changeSwingSpeed(
                            context.getSource(), IntegerArgumentType.getInteger(context, "speed"), SWING_KEYS[3], false
                    ))
                )
            )

            .then(Commands.literal(SWING_KEYS[4])
                .then(Commands.argument("speed", IntegerArgumentType.integer())
                    .suggests(SPEED_SUGGESTION)
                    .executes(context -> changeSwingSpeed(
                            context.getSource(), IntegerArgumentType.getInteger(context, "speed"), SWING_KEYS[4], false
                    ))
                )
            )

            .then(Commands.literal(SWING_KEYS[5])
                .then(Commands.argument("speed", IntegerArgumentType.integer())
                    .suggests(SPEED_SUGGESTION)
                    .executes(context -> changeSwingSpeed(
                            context.getSource(), IntegerArgumentType.getInteger(context, "speed"), SWING_KEYS[5], false
                    ))
                )
            )

            .then(Commands.literal(SWING_KEYS[6])
                .then(Commands.argument("speed", IntegerArgumentType.integer())
                    .suggests(GLOBAL_SUGGESTION)
                    .executes(context -> changeSwingSpeed(
                            context.getSource(), IntegerArgumentType.getInteger(context, "speed"), SWING_KEYS[6], true
                    ))
                )
            )

            .then(Commands.literal(SWING_KEYS[7])
                .then(Commands.argument("speed", IntegerArgumentType.integer())
                    .suggests(GLOBAL_SUGGESTION)
                    .executes(context -> changeSwingSpeed(
                            context.getSource(), IntegerArgumentType.getInteger(context, "speed"), SWING_KEYS[7], true
                    ))
                )
            )

            .then(Commands.literal(SWING_KEYS[8])
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

    private static int rangeError(CommandSource source, boolean isGlobal)
    {
        int min = isGlobal ? GLOBAL : MIN;
        source.sendFailure(ITextComponent.nullToEmpty(I18n.get("oldswing.cmd.swings.out_of_range", min, MAX)));
        return 0;
    }

    private static int modStateError(CommandSource source)
    {
        if (ConfigHandler.mod_enabled)
            return 1;

        source.sendFailure(ITextComponent.nullToEmpty(I18n.get("oldswing.cmd.mod_enabled_swings")));
        return 0;
    }

    private static int changeSwingSpeed(CommandSource source, int speed, String on, boolean isGlobal)
    {
        if (modStateError(source) == 0)
            return 0;

        int min = isGlobal ? GLOBAL : MIN;

        if (speed < min || speed > MAX)
            return rangeError(source, isGlobal);

        if (on.equals(SWING_KEYS[0]))
            // Change global swing speed
            ClientConfig.global_speed.set(speed);
        else if (on.equals(SWING_KEYS[1]))
            // Change swing speeds of items that are not swords or tools
            ClientConfig.swing_speed.set(speed);
        else if (on.equals(SWING_KEYS[2]))
            // Change swing speed of swords
            ClientConfig.sword_speed.set(speed);
        else if (on.equals(SWING_KEYS[3]))
            // Change swing speed of tools
            ClientConfig.tool_speed.set(speed);
        else if (on.equals(SWING_KEYS[4]))
        {
            // Set swing speed of currently held item

            ClientPlayerEntity entity = Minecraft.getInstance().player;

            if (entity != null)
            {
                Item holding = CustomSwing.addFromHand(entity, speed);

                if (holding == null)
                {
                    source.sendFailure(ITextComponent.nullToEmpty(I18n.get("oldswing.cmd.swings.holding")));
                    return 0;
                }

                on = holding.toString();
            }
            else
            {
                source.sendFailure(ITextComponent.nullToEmpty(I18n.get("oldswing.cmd.not_player")));
                return 0;
            }
        }
        else if (on.equals(SWING_KEYS[5]))
            // Set swing speed when placing blocks
            ClientConfig.block_speed.set(speed);
        else if (on.equals(SWING_KEYS[6]))
            // Set swing speed of fatigue potion
            ClientConfig.fatigue_speed.set(speed);
        else if (on.equals(SWING_KEYS[7]))
            // Set swing speed of haste potion
            ClientConfig.haste_speed.set(speed);

        ConfigHandler.bake();

        final String oldSwing = String.format("%s: %s",
                ColorUtil.format(I18n.get("oldswing.config.range_alpha"), TextFormatting.LIGHT_PURPLE),
                ColorUtil.format(String.valueOf(ConfigHandler.OLD_SPEED), TextFormatting.YELLOW)
        );

        final String newSwing = String.format("%s: %s",
                ColorUtil.format(I18n.get("oldswing.config.range_modern"), TextFormatting.LIGHT_PURPLE),
                ColorUtil.format(String.valueOf(ConfigHandler.NEW_SPEED), TextFormatting.YELLOW)
        );

        String plural = I18n.get("oldswing.speed");

        if (on.equals(SWING_KEYS[0]))
        {
            on = I18n.get("oldswing.all_config");
            plural = I18n.get("oldswing.speeds");
        }

        final String speeds = String.format("%s\n%s", oldSwing, newSwing);
        final String info = speeds + I18n.get("oldswing.cmd.swings.changed",
                ColorUtil.format(on, TextFormatting.GOLD), plural, ColorUtil.format(Integer.toString(speed), TextFormatting.AQUA));
        source.sendSuccess(ITextComponent.nullToEmpty(info), true);

        return 1;
    }

    private static int addCustomSwing(CommandSource source, ItemInput itemIn, int speed)
    {
        if (modStateError(source) == 0)
            return 0;

        Item item = itemIn.getItem();
        ResourceLocation resource = ForgeRegistries.ITEMS.getKey(item);

        if (resource == null)
        {
            source.sendFailure(ITextComponent.nullToEmpty(I18n.get("oldswing.cmd.swings.nonexistent", itemIn)));
            return 0;
        }
        else if (speed < MIN || speed > MAX)
            return rangeError(source, false);

        CustomSwing.add(resource.toString(), speed);
        ConfigHandler.bake();

        final String out = I18n.get("oldswing.cmd.swings.custom_change",
                ColorUtil.format(item.getRegistryName() != null ? item.getRegistryName().toString() : I18n.get("oldswing.unknown"), TextFormatting.GREEN),
                ColorUtil.format(String.valueOf(speed), TextFormatting.YELLOW));
        source.sendSuccess(ITextComponent.nullToEmpty(out), true);

        return 1;
    }

    private static int removeCustomSwing(CommandSource source, String item)
    {
        if (modStateError(source) == 0)
            return 0;

        String out;

        if (CustomSwing.remove(item))
        {
            ConfigHandler.bake();

            out = I18n.get("oldswing.cmd.swings.custom_remove", ColorUtil.format(item, TextFormatting.GOLD));
            source.sendSuccess(ITextComponent.nullToEmpty(out), true);

            return 1;
        }

        out = I18n.get("oldswing.cmd.swings.custom_nonexistent", item);
        source.sendFailure(ITextComponent.nullToEmpty(out));

        return 0;
    }
}
