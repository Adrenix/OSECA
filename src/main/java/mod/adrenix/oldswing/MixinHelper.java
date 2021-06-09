package mod.adrenix.oldswing;

import mod.adrenix.oldswing.config.ConfigHandler;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.item.*;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.ForgeHooksClient;

import javax.annotation.Nonnull;

public class MixinHelper
{
    public static int getSwingSpeed(AbstractClientPlayer player)
    {
        if (!ConfigHandler.mod_enabled)
            return ConfigHandler.Speeds.MODERN;

        if (ConfigHandler.global_swing_enabled)
            return ConfigHandler.global_swing_speed;

        Item item = player.getHeldItem(EnumHand.MAIN_HAND).getItem();

        if (item instanceof ItemSword)
            return ConfigHandler.swing_categories.sword_swing_speed;
        else if (item instanceof ItemTool)
            return ConfigHandler.swing_categories.tool_swing_speed;
        else if (item instanceof ItemBlock)
            return ConfigHandler.swing_categories.block_swing_speed;

        return ConfigHandler.Speeds.ALPHA;
    }

    public static float getCooldownAnimationFloat(AbstractClientPlayer player, float adjustTicks)
    {
        return ConfigHandler.animations.prevent_cooldown && ConfigHandler.mod_enabled ? 1.0F : player.getCooledAttackStrength(adjustTicks);
    }

    public static boolean shouldArmSway()
    {
        return !ConfigHandler.mod_enabled || !ConfigHandler.animations.prevent_arm_sway;
    }

    private static int slotMainHand = 0;
    public static boolean shouldCauseReequipAnimation(@Nonnull ItemStack from, @Nonnull ItemStack to, int slot)
    {
        if (!ConfigHandler.animations.prevent_reequip || !ConfigHandler.mod_enabled)
            return ForgeHooksClient.shouldCauseReequipAnimation(from, to, slot);

        if (slot != -1 && slot != slotMainHand)
        {
            slotMainHand = slot;
            return true;
        }

        return !ItemStack.areItemsEqualIgnoreDurability(from, to);
    }
}
