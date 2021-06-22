package mod.adrenix.oldswing.config;

import mod.adrenix.oldswing.OldSwing;

public abstract class DefaultConfig
{
    public static final int OLD_SPEED = OldSwing.OLD_SPEED;

    public static class Animations
    {
        public static boolean shouldCooldown = false;
        public static boolean shouldReequip = false;
        public static boolean shouldSweep = false;
        public static boolean shouldArmSway = false;
    }

    public static class Swings
    {
        public static int item = OLD_SPEED;
        public static int block = OLD_SPEED;
        public static int sword = OLD_SPEED;
        public static int tool = OLD_SPEED;
        public static int global = OLD_SPEED;
        public static boolean shouldGlobalizeSwings = false;
    }
}
