package mod.adrenix.oldswing.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.*;

public class ClientConfig {
    /* Mod State */
    public static ForgeConfigSpec.BooleanValue mod_enabled;

    /* Animation Options */
    public static ForgeConfigSpec.BooleanValue prevent_cooldown;
    public static ForgeConfigSpec.BooleanValue prevent_reequip;
    public static ForgeConfigSpec.BooleanValue prevent_sway;
    public static final Map<String, ForgeConfigSpec.ConfigValue<?>> ANIMATIONS = new HashMap<>();

    /* Swing Speeds */
    public static ForgeConfigSpec.IntValue swing_speed;
    public static ForgeConfigSpec.IntValue block_speed;
    public static ForgeConfigSpec.IntValue sword_speed;
    public static ForgeConfigSpec.IntValue tool_speed;
    public static ForgeConfigSpec.IntValue global_speed;
    public static ForgeConfigSpec.BooleanValue global_speed_enabled;
    public static ForgeConfigSpec.ConfigValue<Config> custom;
    public static final Map<String, ForgeConfigSpec.IntValue> SPEEDS = new HashMap<>();

    public ClientConfig(ForgeConfigSpec.Builder builder) {
        /* Mod State */
        mod_enabled = builder
                .comment(" Use this option to enable or disable the mod.")
                .define("mod.enabled", true);

        /* Standard Animations */
        prevent_cooldown = builder
                .comment(" Enabling this option will prevent the cooldown animation that plays after every swing or change in slot.",
                         " This does not disable or modify the cooldown attack system, this only disables the animation.")
                .define("animation.prevent_cooldown", true);

        prevent_reequip = builder
                .comment(" Enabling this option will prevent the reequip animation from playing when an item takes damage.",
                         " The purpose of this is to prevent the animation from playing after every block break.",
                         " Disable this option if you are experiencing issues with other mods.")
                .define("animation.prevent_reequip", true);

        prevent_sway = builder
                .comment(" Enabling this option will prevent the subtle arm sway when looking in different directions.")
                .define("animation.prevent_sway", true);

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

        global_speed = builder
                .comment(" Give a global swing speed that is applied to everything regardless of configured values.",
                         " This can be toggled using the 'global_speed_enabled' setting.")
                .defineInRange( "global_speed", ConfigHandler.OLD_SPEED, ConfigHandler.MIN, ConfigHandler.MAX);

        global_speed_enabled = builder
                .comment(" Enabling this option will override all swing speeds with the value set in the 'global_speed' setting.")
                .define("global_speed_enabled", false);

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
        ANIMATIONS.put("reequipAnimation", prevent_reequip);
        ANIMATIONS.put("cooldownAnimation", prevent_cooldown);
        ANIMATIONS.put("armSway", prevent_sway);

        // NOTE: Adding a new entry here requires updating the "change all" method.
        SPEEDS.put("tools", tool_speed);
        SPEEDS.put("swords", sword_speed);
        SPEEDS.put("items", swing_speed);
        SPEEDS.put("blocks", block_speed);
    }

    public static void loadCustomItems() {
        custom.get().entrySet().forEach(entry -> custom.get().add(entry.getKey(), entry.getValue()));
    }
}
