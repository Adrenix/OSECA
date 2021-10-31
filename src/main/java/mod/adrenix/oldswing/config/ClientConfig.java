package mod.adrenix.oldswing.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.*;

public class ClientConfig
{
    /* Mod State */
    public static ForgeConfigSpec.BooleanValue mod_enabled;

    /* Eye Candy Options */
    public static ForgeConfigSpec.BooleanValue old_tooltip_boxes;
    public static ForgeConfigSpec.BooleanValue old_damage_colors;
    public static ForgeConfigSpec.BooleanValue old_light_flicker;
    public static ForgeConfigSpec.BooleanValue old_item_holding;
    public static ForgeConfigSpec.BooleanValue old_2d_items;
    public static final Map<String, ForgeConfigSpec.ConfigValue<?>> EYE_CANDY = new HashMap<>();

    /* Animation Options */
    public static ForgeConfigSpec.BooleanValue prevent_tool_disintegration;
    public static ForgeConfigSpec.BooleanValue prevent_bob_vertical;
    public static ForgeConfigSpec.BooleanValue prevent_smooth_sneak;
    public static ForgeConfigSpec.BooleanValue prevent_swing_drop;
    public static ForgeConfigSpec.BooleanValue prevent_cooldown;
    public static ForgeConfigSpec.BooleanValue prevent_reequip;
    public static ForgeConfigSpec.BooleanValue prevent_sweep;
    public static ForgeConfigSpec.BooleanValue prevent_sway;
    public static final Map<String, ForgeConfigSpec.ConfigValue<?>> ANIMATIONS = new HashMap<>();

    /* Swing Speeds */
    public static ForgeConfigSpec.IntValue swing_speed;
    public static ForgeConfigSpec.IntValue block_speed;
    public static ForgeConfigSpec.IntValue sword_speed;
    public static ForgeConfigSpec.IntValue tool_speed;
    public static ForgeConfigSpec.IntValue fatigue_speed;
    public static ForgeConfigSpec.IntValue haste_speed;
    public static ForgeConfigSpec.IntValue global_speed;
    public static ForgeConfigSpec.ConfigValue<Config> custom;
    public static final Map<String, ForgeConfigSpec.IntValue> SPEEDS = new HashMap<>();

    public ClientConfig(ForgeConfigSpec.Builder builder)
    {
        /* Mod State */
        mod_enabled = builder
                .comment(" Use this option to enable or disable the mod.")
                .define("mod.enabled", true);

        /* Eye Candy */
        old_tooltip_boxes = builder
                .comment(" Set this to true to bring back the old tooltip boxes from Minecraft Beta 1.7.")
                .define("eye_candy.old_tooltip_boxes", true);

        old_damage_colors = builder
                .comment(" Set this to true to bring back the old durability bar colors for items in the hotbar.")
                .define("eye_candy.old_damage_colors", true);

        old_item_holding = builder
                .comment(" Set this to true to bring back the old item holding position and swinging rotations.")
                .define("eye_candy.old_item_holding", true);

        old_2d_items = builder
                .comment(" Set this to true to simulate the old 2D floating items. (WIP)")
                .define("eye_candy.old_2d_items", true);

        old_light_flicker = builder
                .comment(" Set this to true to disable torch light flickering.",
                         " It is recommended to combine this settings with an old lighting resource pack.")
                .define("eye_candy.old_torch_flicker", true);

        /* Standard Animations */
        prevent_tool_disintegration = builder
                .comment(" Set this to true to prevent the breaking animation that happens when a tool runs out of durability.")
                .define("animation.prevent_tool_disintegration", true);

        prevent_bob_vertical = builder
                .comment(" Set this to false to allow a subtle animation to be applied to the arm and viewing distance when jumping or falling.")
                .define("animation.prevent_bob_vertical", false);

        prevent_smooth_sneak = builder
                .comment(" Set this to true to prevent the smooth transition animation that plays when sneaking.")
                .define("animation.prevent_smooth_sneak", true);

        prevent_cooldown = builder
                .comment(" Enabling this option will prevent the cooldown animation that plays after every swing or change in slot.",
                         " This does not disable or modify the cooldown attack system, this only disables the animation.")
                .define("animation.prevent_cooldown", true);

        prevent_reequip = builder
                .comment(" Enabling this option will prevent the reequip animation from playing when an item takes damage.",
                         " The purpose of this is to prevent the animation from playing after every block break.",
                         " Disable this option if you are experiencing issues with other mods.")
                .define("animation.prevent_reequip", true);

        prevent_sweep = builder
                .comment(" Enabling this option will prevent the sweep particles from spawning when attacking an entity.")
                .define("animation.prevent_sweep", true);

        prevent_sway = builder
                .comment(" Enabling this option will prevent the subtle arm sway when looking in different directions.")
                .define("animation.prevent_sway", true);

        prevent_swing_drop = builder
                .comment(" Enabling this option will prevent hand movement when dropping items.")
                .define("animation.prevent_drop", true);

        /* Swing Speeds */
        builder.comment(
                " The higher the swing number is the slower the swinging animation will be.",
                " Alpha/Beta Minecraft: 8",
                " Modern Minecraft: 6");
        builder.push("swings");

        swing_speed = builder
                .comment(" Use this option to assign a swing speed to anything that isn't a block, tool, or sword.")
                .defineInRange("item_speed", ConfigHandler.OLD_SPEED, ConfigHandler.MIN, ConfigHandler.MAX);

        block_speed = builder
                .comment(" Give a custom swing speed when placing blocks.")
                .defineInRange("block_speed", ConfigHandler.OLD_SPEED, ConfigHandler.MIN, ConfigHandler.MAX);

        sword_speed = builder
                .comment(" Give a custom swing speed for swords.")
                .defineInRange("sword_speed", ConfigHandler.OLD_SPEED, ConfigHandler.MIN, ConfigHandler.MAX);

        tool_speed = builder
                .comment(" Give a custom swing speed for tools.",
                         " Like shovels, pickaxes, and axes.")
                .defineInRange("tool_speed", ConfigHandler.OLD_SPEED, ConfigHandler.MIN, ConfigHandler.MAX);

        fatigue_speed = builder
                .comment(" Assign a swing speed when the mining fatigue potion effect is active.",
                         " Set to -1 to disable.")
                .defineInRange("fatigue_speed", ConfigHandler.GLOBAL, ConfigHandler.GLOBAL, ConfigHandler.MAX);

        haste_speed = builder
                .comment(" Assign a swing speed when the haste potion effect is active.",
                         " Set to -1 to disable.")
                .defineInRange("haste_speed", ConfigHandler.GLOBAL, ConfigHandler.GLOBAL, ConfigHandler.MAX);

        global_speed = builder
                .comment(" Give a global swing speed that is applied to everything regardless of configured values.",
                         " Set to -1 to disable.")
                .defineInRange( "global_speed", ConfigHandler.GLOBAL, ConfigHandler.GLOBAL, ConfigHandler.MAX);

        custom = builder
                .comment(" Add a custom swing speed for any item in the game.",
                         " It is recommended to add items in-game through the config GUI or the command system.",
                         " If you want to manually add items, then reference the following example:",
                         " ",
                         " [swings.custom]",
                         "     minecraft-diamond_pickaxe = 10",
                         "     minecraft-iron_shovel = 8")
                .define("custom", CommentedConfig.inMemoryConcurrent().createSubConfig());

        /* Command References */

        EYE_CANDY.put("oldTooltipBoxes", old_tooltip_boxes);
        EYE_CANDY.put("oldDamageColors", old_damage_colors);
        EYE_CANDY.put("oldItemHolding", old_item_holding);
        EYE_CANDY.put("oldFloatingItems", old_2d_items);
        EYE_CANDY.put("oldTorchFlicker", old_light_flicker);

        ANIMATIONS.put("reequipAnimation", prevent_reequip);
        ANIMATIONS.put("cooldownAnimation", prevent_cooldown);
        ANIMATIONS.put("sweepParticles", prevent_sweep);
        ANIMATIONS.put("armSway", prevent_sway);
        ANIMATIONS.put("dropAnimation", prevent_swing_drop);
        ANIMATIONS.put("verticalBobbing", prevent_bob_vertical);
        ANIMATIONS.put("smoothSneaking", prevent_smooth_sneak);
        ANIMATIONS.put("toolDisintegration", prevent_tool_disintegration);

        SPEEDS.put("tools", tool_speed);
        SPEEDS.put("swords", sword_speed);
        SPEEDS.put("items", swing_speed);
        SPEEDS.put("blocks", block_speed);
        SPEEDS.put("fatigue", fatigue_speed);
        SPEEDS.put("haste", haste_speed);
        SPEEDS.put("global", global_speed);
    }

    public static void loadCustomItems()
    {
        custom.get().entrySet().forEach(entry -> custom.get().add(entry.getKey(), entry.getValue()));
    }
}
