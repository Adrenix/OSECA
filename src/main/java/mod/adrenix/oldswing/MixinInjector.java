package mod.adrenix.oldswing;

import com.electronwill.nightconfig.core.Config;
import mod.adrenix.oldswing.config.ConfigHandler;
import mod.adrenix.oldswing.config.CustomSwing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public abstract class MixinInjector
{
    public static boolean isModEnabled()
    {
        return ConfigHandler.mod_enabled;
    }

    /* Swinging Injections */

    public static boolean isSpeedGlobal()
    {
        return ConfigHandler.global_speed != ConfigHandler.GLOBAL;
    }

    public static int getSpeedFromItem(Item item)
    {
        ResourceLocation source = ForgeRegistries.ITEMS.getKey(item);

        for (Config.Entry entry : ConfigHandler.custom_speeds.entrySet())
        {
            int mod = CustomSwing.get(source, entry);
            if (mod != -1)
                return mod;
        }

        if (item instanceof SwordItem)
            return ConfigHandler.sword_speed;
        else if (item instanceof ToolItem)
            return ConfigHandler.tool_speed;
        else if (item instanceof BlockItem)
            return ConfigHandler.block_speed;
        return ConfigHandler.swing_speed;
    }

    public static int getSwingSpeed(ClientPlayerEntity player)
    {
        if (!isModEnabled())
            return ConfigHandler.NEW_SPEED;
        if (isSpeedGlobal())
            return ConfigHandler.global_speed;
        return getSpeedFromItem(player.getMainHandItem().getItem());
    }

    public static int getSwingSpeed()
    {
        return getSwingSpeed(Minecraft.getInstance().player);
    }

    public static int getFatigueSpeed()
    {
        return isSpeedGlobal() ? ConfigHandler.global_speed : ConfigHandler.fatigue_speed;
    }

    public static int getHasteSpeed()
    {
        return isSpeedGlobal() ? ConfigHandler.global_speed : ConfigHandler.haste_speed;
    }

    public static boolean isOverridingFatigue()
    {
        return isModEnabled() && ConfigHandler.fatigue_speed != ConfigHandler.GLOBAL;
    }

    public static boolean isOverridingHaste()
    {
        return isModEnabled() && ConfigHandler.haste_speed != ConfigHandler.GLOBAL;
    }

    /* Eye Candy Injections */

    public static boolean oldDamageColors()
    {
        return isModEnabled() && ConfigHandler.old_damage_colors;
    }

    public static boolean oldFloatingItems()
    {
        return isModEnabled() && ConfigHandler.old_2d_items;
    }

    public static boolean oldItemHolding()
    {
        return isModEnabled() && ConfigHandler.old_item_holding;
    }

    public static boolean oldTooltips()
    {
        return !isModEnabled() || !ConfigHandler.old_tooltip_boxes;
    }

    public static boolean oldLightFlicker()
    {
        return isModEnabled() && ConfigHandler.old_light_flicker;
    }

    /* Animation Injections */

    public static float getCooldownAnimationFloat(ClientPlayerEntity player, float adjustTicks)
    {
        return ConfigHandler.prevent_cooldown && isModEnabled() ? 1.0F : player.getAttackStrengthScale(adjustTicks);
    }

    public static boolean shouldArmSway()
    {
        return !isModEnabled() || !ConfigHandler.prevent_sway;
    }

    public static boolean shouldSweepAttack()
    {
        return !isModEnabled() || !ConfigHandler.prevent_sweep;
    }

    public static boolean shouldSneakSmooth()
    {
        return !isModEnabled() || !ConfigHandler.prevent_smooth_sneak;
    }

    public static boolean shouldBobVertical()
    {
        return isModEnabled() && !ConfigHandler.prevent_bob_vertical;
    }

    public static boolean shouldSwingDrop()
    {
        return !isModEnabled() || !ConfigHandler.prevent_swing_drop;
    }

    public static boolean shouldToolsDisintegrate()
    {
        return !isModEnabled() || !ConfigHandler.prevent_tool_disintegration;
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

    public static boolean shouldCauseReequipAnimation(@Nonnull ItemStack from, @Nonnull ItemStack to, int slot)
    {
        boolean doReequip = doReequipAnimation(from, to, slot);
        if (!isModEnabled() || !ConfigHandler.prevent_reequip)
            return ForgeHooksClient.shouldCauseReequipAnimation(from, to, slot);

        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null && player.swinging)
            return false;

        return doReequip;
    }
}
