package mod.adrenix.oldswing.config;

import com.electronwill.nightconfig.core.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class MixinHelper
{
    private static int getSpeedFromItem(Item item)
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
        if (!ConfigHandler.mod_enabled)
            return ConfigHandler.NEW_SPEED;
        if (ConfigHandler.global_speed_enabled)
            return ConfigHandler.global_speed;
        return getSpeedFromItem(player.getMainHandItem().getItem());
    }

    public static float getCooldownAnimationFloat(ClientPlayerEntity player, float adjustTicks)
    {
        return ConfigHandler.prevent_cooldown && ConfigHandler.mod_enabled ? 1.0F : player.getAttackStrengthScale(adjustTicks);
    }

    public static boolean armSwayFuse = false;
    public static boolean shouldArmSway()
    {
        return !ConfigHandler.mod_enabled || !ConfigHandler.prevent_sway;
    }

    public static boolean shouldCauseReequipAnimation(@Nonnull ItemStack from, @Nonnull ItemStack to, int slot)
    {
        boolean forgeReequipState = ForgeHooksClient.shouldCauseReequipAnimation(from, to, slot);
        if (!ConfigHandler.prevent_reequip || !ConfigHandler.mod_enabled)
            return forgeReequipState;

        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null && player.swinging)
            return false;

        return forgeReequipState;
    }
}
