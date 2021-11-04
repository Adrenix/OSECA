package mod.adrenix.oldswing.config;

public abstract class DefaultConfig
{
    public static final int OLD_SPEED = 8;
    public static final int NEW_SPEED = 6;

    public static class EyeCandy
    {
        public static final boolean OLD_TOOLTIP_BOXES = true;
        public static final boolean OLD_LIGHT_FLICKER = true;
        public static final boolean OLD_DAMAGE_COLORS = true;
        public static final boolean OLD_ITEM_HOLDING = true;
        public static final boolean OLD_2D_ITEMS = true;
    }

    public static class Animations
    {
        public static final boolean SHOULD_COOLDOWN = false;
        public static final boolean SHOULD_REEQUIP = false;
        public static final boolean SHOULD_SWEEP = false;
        public static final boolean SHOULD_ARM_SWAY = false;
        public static final boolean SHOULD_BOB_VERTICAL = true;
        public static final boolean SHOULD_SNEAK_SMOOTH = false;
        public static final boolean SHOULD_SWING_DROP = false;
        public static final boolean SHOULD_TOOL_DISINTEGRATE = false;
    }

    public static class Swings
    {
        public static final int ITEM = OLD_SPEED;
        public static final int BLOCK = OLD_SPEED;
        public static final int SWORD = OLD_SPEED;
        public static final int TOOL = OLD_SPEED;
        public static final int HASTE = -1;
        public static final int MINING_FATIGUE = -1;
        public static final int GLOBAL = -1;
        public static final int DISABLED = 0;
    }
}
