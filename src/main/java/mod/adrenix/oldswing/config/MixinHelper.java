package mod.adrenix.oldswing.config;

import com.electronwill.nightconfig.core.Config;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class MixinHelper {
    public static int swingSpeed(ClientPlayerEntity player) {
        if (!ConfigHandler.mod_enabled)
            return ConfigHandler.NEW_SPEED;

        Item item = player.getMainHandItem().getItem();
        ResourceLocation source = ForgeRegistries.ITEMS.getKey(item);

        if (ConfigHandler.global_speed_enabled)
            return ConfigHandler.global_speed;

        for (Config.Entry entry : ConfigHandler.custom_speeds.entrySet()) {
            int mod = CustomSwing.get(source, entry);
            if (mod != -1) {
                return mod;
            }
        }

        if (item instanceof SwordItem) {
            return ConfigHandler.sword_speed;
        } else if (item instanceof ToolItem) {
            return ConfigHandler.tool_speed;
        } else if (item instanceof BlockItem) {
            return ConfigHandler.block_speed;
        }

        return ConfigHandler.swing_speed;
    }

    public static float getCooldownAnimationFloat(ClientPlayerEntity player, float adjustTicks) {
        return ConfigHandler.prevent_cooldown && ConfigHandler.mod_enabled ? 1.0F : player.getAttackStrengthScale(adjustTicks);
    }

    private static int slotMainHand = 0;
    public static boolean shouldCauseReequipAnimation(@Nonnull ItemStack from, @Nonnull ItemStack to, int slot) {
        if (!ConfigHandler.prevent_reequip || !ConfigHandler.mod_enabled)
            return ForgeHooksClient.shouldCauseReequipAnimation(from, to, slot);

        if (slot != -1 && slot != slotMainHand) {
            slotMainHand = slot;
            return true;
        }

        return !ItemStack.isSameIgnoreDurability(from, to);
    }
}
