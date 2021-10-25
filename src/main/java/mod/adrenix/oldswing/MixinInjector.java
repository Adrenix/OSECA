package mod.adrenix.oldswing;

import mod.adrenix.oldswing.config.ClientConfig;
import mod.adrenix.oldswing.config.ConfigRegistry;
import mod.adrenix.oldswing.config.CustomizedSwings;
import mod.adrenix.oldswing.config.DefaultConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.*;

import java.util.Map;

public class MixinInjector
{
    private static final ClientConfig.EyeCandy EYE_CANDY = ConfigRegistry.cache.eyeCandy;
    private static final ClientConfig.Animations ANIMATIONS = ConfigRegistry.cache.animations;
    private static final ClientConfig.Swings SWINGS = ConfigRegistry.cache.swings;
    private static final ClientConfig CONFIG = ConfigRegistry.cache;

    /* Config Helpers */

    public static boolean isModEnabled()
    {
        return CONFIG.isModEnabled;
    }

    /* Swing Speed Injection Helpers */

    public static int getSpeedFromItem(Item item)
    {
        Map.Entry<String, Integer> entry = CustomizedSwings.getEntryFromItem(item);

        if (SWINGS.global != DefaultConfig.Swings.GLOBAL)
            return SWINGS.global;
        else if (entry != null)
            return entry.getValue();
        else if (item instanceof SwordItem)
            return SWINGS.sword;
        else if (item instanceof BlockItem)
            return SWINGS.block;
        else if (item instanceof DiggerItem)
            return SWINGS.tool;
        return SWINGS.item;
    }

    public static int getSwingSpeed(AbstractClientPlayer player)
    {
        if (CONFIG.isModEnabled)
            return getSpeedFromItem(player.getMainHandItem().getItem());
        return DefaultConfig.NEW_SPEED;
    }

    public static int getSwingSpeed()
    {
        return getSwingSpeed(Minecraft.getInstance().player);
    }

    public static int getHasteSpeed()
    {
        return SWINGS.haste;
    }

    public static boolean isOverridingHaste()
    {
        return CONFIG.isModEnabled && SWINGS.haste != DefaultConfig.Swings.GLOBAL;
    }

    public static int getFatigueSpeed()
    {
        return SWINGS.miningFatigue;
    }

    public static boolean isOverridingFatigue()
    {
        return CONFIG.isModEnabled && SWINGS.miningFatigue != DefaultConfig.Swings.GLOBAL;
    }

    /* Eye Candy Injection Helpers */

    public static boolean oldDamageColors()
    {
        return CONFIG.isModEnabled && EYE_CANDY.oldDamageColors;
    }

    public static boolean oldFloatingItems()
    {
        return CONFIG.isModEnabled && EYE_CANDY.old2DItems;
    }

    public static boolean oldItemHolding()
    {
        return CONFIG.isModEnabled && EYE_CANDY.oldItemHolding;
    }

    /* Animation Injection Helpers */

    public static float getCooldownAnimationFloat(AbstractClientPlayer player, float adjustTicks)
    {
        return !ANIMATIONS.shouldCooldown && CONFIG.isModEnabled ? 1.0F : player.getAttackStrengthScale(adjustTicks);
    }

    public static boolean shouldArmSway()
    {
        return !CONFIG.isModEnabled || ANIMATIONS.shouldArmSway;
    }

    public static boolean shouldSweepAttack()
    {
        return !CONFIG.isModEnabled || ANIMATIONS.shouldSweep;
    }

    public static boolean shouldSneakSmooth()
    {
        return !CONFIG.isModEnabled || ANIMATIONS.shouldSneakSmooth;
    }

    public static boolean shouldBobVertical()
    {
        return CONFIG.isModEnabled && ANIMATIONS.shouldBobVertical;
    }

    private static boolean doReequipAnimation(ItemStack from, ItemStack to)
    {
        boolean fromInvalid = from.isEmpty();
        boolean toInvalid = to.isEmpty();

        if (fromInvalid && toInvalid) return false;
        if (fromInvalid || toInvalid) return true;

        return !from.equals(to);
    }

    public static boolean shouldCauseReequipAnimation(ItemStack from, ItemStack to)
    {
        boolean doReequip = doReequipAnimation(from, to);
        if (!CONFIG.isModEnabled || ANIMATIONS.shouldReequip)
            return doReequip;

        AbstractClientPlayer player = Minecraft.getInstance().player;
        if (player != null && player.swinging)
            return false;

        return doReequip;
    }
}
