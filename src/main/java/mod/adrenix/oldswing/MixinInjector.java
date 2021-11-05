package mod.adrenix.oldswing;

import mod.adrenix.oldswing.config.ClientConfig;
import mod.adrenix.oldswing.config.ConfigRegistry;
import mod.adrenix.oldswing.config.CustomizedSwings;
import mod.adrenix.oldswing.config.DefaultConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.*;

import java.util.Map;

public abstract class MixinInjector
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

    public static boolean isSpeedGlobal()
    {
        return SWINGS.global != DefaultConfig.Swings.GLOBAL;
    }

    public static int getSpeedFromItem(Item item)
    {
        Map.Entry<String, Integer> entry = CustomizedSwings.getEntryFromItem(item);

        if (isSpeedGlobal())
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
        if (isModEnabled())
            return getSpeedFromItem(player.getMainHandItem().getItem());
        return DefaultConfig.NEW_SPEED;
    }

    public static int getSwingSpeed()
    {
        return getSwingSpeed(Minecraft.getInstance().player);
    }

    public static int getHasteSpeed()
    {
        return isSpeedGlobal() ? SWINGS.global : SWINGS.haste;
    }

    public static int getFatigueSpeed()
    {
        return isSpeedGlobal() ? SWINGS.global : SWINGS.miningFatigue;
    }

    public static boolean isOverridingHaste()
    {
        return isModEnabled() && SWINGS.haste != DefaultConfig.Swings.GLOBAL;
    }

    public static boolean isOverridingFatigue()
    {
        return isModEnabled() && SWINGS.miningFatigue != DefaultConfig.Swings.GLOBAL;
    }

    /* Eye Candy Injection Helpers */

    public static boolean oldDamageColors()
    {
        return isModEnabled() && EYE_CANDY.oldDamageColors;
    }

    public static boolean oldFloatingItems()
    {
        return isModEnabled() && EYE_CANDY.old2DItems;
    }

    public static boolean oldItemHolding()
    {
        return isModEnabled() && EYE_CANDY.oldItemHolding;
    }

    public static boolean oldTooltips()
    {
        return !isModEnabled() || !EYE_CANDY.oldTooltipBoxes;
    }

    public static boolean oldLightFlicker()
    {
        return isModEnabled() && EYE_CANDY.oldLightFlicker;
    }

    /* Animation Injection Helpers */

    public static float getCooldownAnimationFloat(AbstractClientPlayer player, float adjustTicks)
    {
        return !ANIMATIONS.shouldCooldown && isModEnabled() ? 1.0F : player.getAttackStrengthScale(adjustTicks);
    }

    public static boolean shouldArmSway()
    {
        return !isModEnabled() || ANIMATIONS.shouldArmSway;
    }

    public static boolean shouldSweepAttack()
    {
        return !isModEnabled() || ANIMATIONS.shouldSweep;
    }

    public static boolean shouldSneakSmooth()
    {
        return !isModEnabled() || ANIMATIONS.shouldSneakSmooth;
    }

    public static boolean shouldBobVertical()
    {
        return isModEnabled() && ANIMATIONS.shouldBobVertical;
    }

    public static boolean shouldSwingDrop()
    {
        return !isModEnabled() || ANIMATIONS.shouldSwingDrop;
    }

    public static boolean shouldToolsDisintegrate()
    {
        return !isModEnabled() || ANIMATIONS.shouldToolDisintegrate;
    }

    private static int slotMainHand = 0;
    private static boolean doReequipAnimation(ItemStack from, ItemStack to, int slot)
    {
        boolean fromInvalid = from.isEmpty();
        boolean toInvalid = to.isEmpty();
        boolean changed = false;

        if (slot != -1)
        {
            changed = slot != slotMainHand;
            slotMainHand = slot;
        }

        if (fromInvalid && toInvalid) return false;
        if (fromInvalid || toInvalid) return true;
        if (changed)
            return true;
        return !from.sameItemStackIgnoreDurability(to);
    }

    public static boolean shouldCauseReequipAnimation(ItemStack from, ItemStack to, int slot)
    {
        boolean doReequip = doReequipAnimation(from, to, slot);
        if (!isModEnabled() || ANIMATIONS.shouldReequip)
            return doReequip; // How is this used in vanilla?

        AbstractClientPlayer player = Minecraft.getInstance().player;
        if (player != null && player.swinging)
            return false;

        return doReequip;
    }
}
