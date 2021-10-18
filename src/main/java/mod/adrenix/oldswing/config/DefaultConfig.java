package mod.adrenix.oldswing.config;

public abstract class DefaultConfig
{
    public static final int OLD_SPEED = 8;
    public static final int NEW_SPEED = 6;

    public static class Animations
    {
        public static final boolean SHOULD_COOLDOWN = false;
        public static final boolean SHOULD_REEQUIP = false;
        public static final boolean SHOULD_SWEEP = false;
        public static final boolean SHOULD_ARM_SWAY = false;
    }

    public static class Swings
    {
        public static final int ITEM = OLD_SPEED;
        public static final int BLOCK = OLD_SPEED;
        public static final int SWORD = OLD_SPEED;
        public static final int TOOL = OLD_SPEED;
        public static final int GLOBAL = -1;
    }
}
