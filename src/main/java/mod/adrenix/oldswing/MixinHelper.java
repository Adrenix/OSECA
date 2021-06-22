package mod.adrenix.oldswing;

import mod.adrenix.oldswing.config.ClientConfig;
import mod.adrenix.oldswing.config.CustomizedSwings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.*;

import java.util.Map;

public class MixinHelper
{
    private static final ClientConfig.Animations animations = OldSwing.config.animations;
    private static final ClientConfig.Swings swings = OldSwing.config.swings;
    private static final ClientConfig config = OldSwing.config;

    public static int getSpeedFromItem(Item item)
    {
        Map.Entry<String, Integer> entry = CustomizedSwings.getEntryFromItem(item);

        if (swings.shouldGlobalizeSwings)
            return swings.global;
        else if (entry != null)
            return entry.getValue();
        else if (item instanceof SwordItem)
            return swings.sword;
        else if (item instanceof BlockItem)
            return swings.block;
        else if (item instanceof DiggerItem)
            return swings.tool;
        return swings.item;
    }

    public static int getSwingSpeed(AbstractClientPlayer player)
    {
        if (config.isModEnabled)
            return getSpeedFromItem(player.getMainHandItem().getItem());
        return OldSwing.NEW_SPEED;
    }

    public static float getCooldownAnimationFloat(AbstractClientPlayer player, float adjustTicks)
    {
        return !animations.shouldCooldown && config.isModEnabled ? 1.0F : player.getAttackStrengthScale(adjustTicks);
    }

    public static boolean armSwayFuse = false;
    public static boolean shouldArmSway()
    {
        return !config.isModEnabled || animations.shouldArmSway;
    }

    public static boolean shouldSweepAttack()
    {
        return !config.isModEnabled || animations.shouldSweep;
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
        if (!config.isModEnabled || animations.shouldReequip)
            return doReequip;

        AbstractClientPlayer player = Minecraft.getInstance().player;
        if (player != null && player.swinging)
            return false;

        return doReequip;
    }
}
