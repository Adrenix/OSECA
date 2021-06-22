package mod.adrenix.oldswing.config;

import com.google.common.collect.Maps;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import mod.adrenix.oldswing.config.gui.screen.SettingsScreen;

import java.util.Map;

@Config(name = "oldswing")
@Config.Gui.Background(SettingsScreen.BACKGROUND_BLOCK)
public class ClientConfig implements ConfigData
{
    @ConfigEntry.Gui.Excluded public static final int MIN = 0;
    @ConfigEntry.Gui.Excluded public static final int MAX = 16;

    @ConfigEntry.Gui.NoTooltip
    @Comment("Use this option to enable or disable the mod.")
    public boolean isModEnabled = true;

    @ConfigEntry.Gui.CollapsibleObject()
    public Animations animations = new Animations();
    public static class Animations
    {
        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.NoTooltip
        @Comment("Set this to false to prevent the cooldown animation that plays after every swing or change in slot.")
        public boolean shouldCooldown = DefaultConfig.Animations.shouldCooldown;
        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.NoTooltip
        @Comment("Set this to false to prevent the reequip animation that plays after breaking a block.")
        public boolean shouldReequip = DefaultConfig.Animations.shouldReequip;
        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.NoTooltip
        @Comment("Set this to false to prevent sweep particles from showing when attacking an entity.")
        public boolean shouldSweep = DefaultConfig.Animations.shouldSweep;
        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.NoTooltip
        @Comment("Set this to false to prevent the subtle arm sway animation that plays when looking in different directions.")
        public boolean shouldArmSway = DefaultConfig.Animations.shouldArmSway;
    }

    @ConfigEntry.Gui.Excluded
    static final String swingCategoryComment =
        "The higher the swing number is the slower the swinging animation will be.\n" +
        "Alpha/Beta Minecraft: 8\n" +
        "Modern Minecraft: 6"
    ;
    @Comment(swingCategoryComment)
    @ConfigEntry.Gui.NoTooltip
    @ConfigEntry.Gui.CollapsibleObject()
    public Swings swings = new Swings();
    public static class Swings
    {
        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.NoTooltip
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        @Comment("Give a custom swing speed that is used when using tools such as shovels, pickaxes, and hoes.")
        public int tool = DefaultConfig.Swings.tool;

        @ConfigEntry.Gui.NoTooltip
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        @Comment("Give a custom swing speed that is used when placing blocks.")
        public int block = DefaultConfig.Swings.block;

        @ConfigEntry.Gui.NoTooltip
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        @Comment("Give a custom swing speed that is used when using swords.")
        public int sword = DefaultConfig.Swings.sword;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        @Comment("Assign a swing speed to anything that isn't a block, tool, or sword.")
        public int item = DefaultConfig.Swings.item;

        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.NoTooltip
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        @Comment("Give a custom swing speed that is applied globally.\nThis will override all configured swing speeds.")
        public int global = DefaultConfig.Swings.global;

        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.NoTooltip
        @Comment("This must be set to true if you want the global override speed to take effect.")
        public boolean shouldGlobalizeSwings = DefaultConfig.Swings.shouldGlobalizeSwings;
    }

    @Comment("Define custom swing speeds for individualized items.\nExample: \"minecraft:stick\": 14")
    @ConfigEntry.Gui.Excluded
    public Map<String, Integer> custom = Maps.newHashMap();
}
