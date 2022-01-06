package mod.adrenix.oseca.config;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.EnumHand;

public abstract class MixinConfig
{
    /* Config References */

    private static final AnimationConfig ANIMATION = ConfigHandler.ANIMATION;
    private static final SwingConfig SWING = ConfigHandler.SWING;
    private static final CandyConfig CANDY = ConfigHandler.CANDY;
    private static final SoundConfig SOUND = ConfigHandler.SOUND;
    public static boolean isModEnabled() { return ConfigHandler.isModEnabled; }

    /* Swing Helpers */

    public static class Swing
    {
        public static int getSwingSpeed(EntityPlayer player)
        {
            if (!isModEnabled())
                return SwingConfig.Speeds.MODERN;
            else if (isSpeedGlobal())
                return getGlobalSpeed();

            Item item = player.getHeldItem(player.swingingHand == null ? EnumHand.MAIN_HAND : player.swingingHand).getItem();

            if (item instanceof ItemSword)
                return SWING.sword;
            else if (item instanceof ItemTool)
                return SWING.tool;
            else if (item instanceof ItemBlock)
                return SWING.block;

            return SwingConfig.Speeds.ALPHA;
        }

        public static int getSwingSpeed() { return getSwingSpeed(Minecraft.getMinecraft().player); }
        public static int getGlobalSpeed() { return SWING.global; }
        public static int getHasteSpeed() { return SWING.haste; }
        public static int getFatigueSpeed() { return SWING.fatigue; }
        public static boolean isSpeedGlobal() { return SWING.global != SwingConfig.Speeds.DISABLED; }
        public static boolean isOverridingHaste() { return isModEnabled() && SWING.haste != SwingConfig.Speeds.DISABLED; }
        public static boolean isOverridingFatigue() { return isModEnabled() && SWING.fatigue != SwingConfig.Speeds.DISABLED; }
        public static boolean isSpeedPhotosensitive() { return isModEnabled() && getGlobalSpeed() == SwingConfig.Speeds.PHOTOSENSITIVE; }
    }

    /* Sound */

    public static class Sound
    {
        public static boolean shouldAttack() { return !isModEnabled() || !SOUND.noAttack; }
        public static boolean shouldStepUnique() { return !isModEnabled() || !SOUND.noUniqueSteps;  }
        public static boolean shouldRedirectHurt() { return isModEnabled() && SOUND.doOldOof; }
        public static boolean shouldRedirectFall() { return isModEnabled() && SOUND.noFallSounds; }
    }

    /* Eye Candy */

    public static class Candy
    {
        public static boolean oldFog() { return isModEnabled() && CANDY.doOldFog; }
        public static boolean old2DItems() { return isModEnabled() && CANDY.do2DItems; }
        public static boolean oldMainMenu() { return isModEnabled() && CANDY.doOldMainMenu; }
        public static boolean oldLighting() { return isModEnabled() && CANDY.doOldLight; }
        public static boolean oldTooltips() { return isModEnabled() && CANDY.doOldTooltips; }
        public static boolean oldNetherFog() { return isModEnabled() && CANDY.doOldNetherFog; }
        public static boolean oldItemMerging() { return isModEnabled() && CANDY.noItemMerging; }
        public static boolean oldItemHolding() { return isModEnabled() && CANDY.doOldItemHolding; }
        public static boolean oldCloudHeight() { return isModEnabled() && CANDY.doOldCloudHeight; }
        public static boolean oldDamageColors() { return isModEnabled() && CANDY.doOldDamageBar; }
        public static boolean oldVersionOverlay() { return isModEnabled() && CANDY.doVersionOverlay; }
        public static boolean oldSunriseAtNorth() { return isModEnabled() && CANDY.doOldSunriseAtNorth; }
        public static boolean oldExplosionParticles() { return isModEnabled() && CANDY.doOldExplosions; }
        public static boolean fixLightDrivenFog() { return isModEnabled() && CANDY.noLightDrivenFog; }
        public static boolean noFMLBranding() { return isModEnabled() && CANDY.noFMLBranding; }
    }

    /* Animations */

    public static class Animation
    {
        public static boolean shouldZombieArmRaise() { return !isModEnabled() || !ANIMATION.noZombieArmRaise; }
        public static boolean shouldToolsExplode() { return !isModEnabled() || !ANIMATION.noToolExplosion; }
        public static boolean shouldLightFlicker() { return !isModEnabled() || !ANIMATION.noLightFlicker; }
        public static boolean shouldSweepAttack() { return !isModEnabled() || !ANIMATION.noSweepParticles; }
        public static boolean shouldCooldown() { return !isModEnabled() || !ANIMATION.noCooldown; }
        public static boolean shouldArmSway() { return !isModEnabled() || !ANIMATION.noArmSway; }
        public static boolean shouldReequip() { return !isModEnabled() || !ANIMATION.noReequip; }
    }
}
