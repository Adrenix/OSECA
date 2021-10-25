package mod.adrenix.oldswing.config;

import com.google.common.collect.Maps;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import mod.adrenix.oldswing.OldSwing;
import mod.adrenix.oldswing.config.gui.screen.SettingsScreen;

import java.util.Map;

@Config(name = OldSwing.MOD_ID)
@Config.Gui.Background(SettingsScreen.BACKGROUND)
public class ClientConfig implements ConfigData
{
    @ConfigEntry.Gui.Excluded public static final int MIN = 0;
    @ConfigEntry.Gui.Excluded public static final int MAX = 16;

    @ConfigEntry.Gui.NoTooltip
    @Comment("Use this option to enable or disable the mod.")
    public boolean isModEnabled = true;

    @ConfigEntry.Gui.CollapsibleObject()
    public EyeCandy eyeCandy = new EyeCandy();
    public static class EyeCandy
    {
        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.NoTooltip
        @Comment("Set this to true to bring back the old durability colors for items in the hotbar.")
        public boolean oldDamageColors = DefaultConfig.EyeCandy.OLD_DAMAGE_COLORS;

        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.NoTooltip
        @Comment("Set this to true to bring back the old item holding and swinging position.")
        public boolean oldItemHolding = DefaultConfig.EyeCandy.OLD_ITEM_HOLDING;

        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.NoTooltip
        @Comment("Set this to true to simulate the old 2D floating items. (WIP)")
        public boolean old2DItems = DefaultConfig.EyeCandy.OLD_2D_ITEMS;
    }

    @ConfigEntry.Gui.CollapsibleObject()
    public Animations animations = new Animations();
    public static class Animations
    {
        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.NoTooltip
        @Comment("Set this to false to prevent the cooldown animation that plays after every swing or change in slot.")
        public boolean shouldCooldown = DefaultConfig.Animations.SHOULD_COOLDOWN;

        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.NoTooltip
        @Comment("Set this to false to prevent the reequip animation that plays after breaking a block.")
        public boolean shouldReequip = DefaultConfig.Animations.SHOULD_REEQUIP;

        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.NoTooltip
        @Comment("Set this to false to prevent sweep particles from showing when attacking an entity.")
        public boolean shouldSweep = DefaultConfig.Animations.SHOULD_SWEEP;

        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.NoTooltip
        @Comment("Set this to false to prevent the subtle arm sway animation that plays when looking in different directions.")
        public boolean shouldArmSway = DefaultConfig.Animations.SHOULD_ARM_SWAY;

        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.NoTooltip
        @Comment("Set this to true to allow a subtle animation to be applied to the arm and viewing distance when jumping or falling.")
        public boolean shouldBobVertical = DefaultConfig.Animations.SHOULD_BOB_VERTICAL;

        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.NoTooltip
        @Comment("Set this to false to prevent the smooth transition animation that plays when sneaking.")
        public boolean shouldSneakSmooth = DefaultConfig.Animations.SHOULD_SNEAK_SMOOTH;
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
        public int tool = DefaultConfig.Swings.TOOL;

        @ConfigEntry.Gui.NoTooltip
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        @Comment("Give a custom swing speed that is used when placing blocks.")
        public int block = DefaultConfig.Swings.BLOCK;

        @ConfigEntry.Gui.NoTooltip
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        @Comment("Give a custom swing speed that is used when using swords.")
        public int sword = DefaultConfig.Swings.SWORD;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = MIN, max = MAX)
        @Comment("Assign a swing speed to anything that isn't a block, tool, or sword.")
        public int item = DefaultConfig.Swings.ITEM;

        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.NoTooltip
        @ConfigEntry.BoundedDiscrete(min = DefaultConfig.Swings.GLOBAL, max = MAX)
        @Comment("Assign a swing speed when the haste potion effect is active. Set to -1 to disable.")
        public int haste = DefaultConfig.Swings.HASTE;

        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.NoTooltip
        @ConfigEntry.BoundedDiscrete(min = DefaultConfig.Swings.GLOBAL, max = MAX)
        @Comment("Assign a swing speed when the mining fatigue potion effect is active. Set to -1 to disable.")
        public int miningFatigue = DefaultConfig.Swings.MINING_FATIGUE;

        @ConfigEntry.Gui.PrefixText
        @ConfigEntry.Gui.NoTooltip
        @ConfigEntry.BoundedDiscrete(min = DefaultConfig.Swings.GLOBAL, max = MAX)
        @Comment("Give a custom swing speed that is applied globally. Set to -1 to disable.\nThis will override all configured swing speeds that don't match this number.")
        public int global = DefaultConfig.Swings.GLOBAL;
    }

    @Comment("Define custom swing speeds for individualized items.\nExample: \"minecraft:stick\": 14")
    @ConfigEntry.Gui.Excluded
    public Map<String, Integer> custom = Maps.newHashMap();
}
