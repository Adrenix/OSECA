package mod.adrenix.oldswing.config;

import com.electronwill.nightconfig.core.Config;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class TransformerHelper {
    public static int swingSpeed(ClientPlayerEntity player) {
        if (!ConfigHandler.mod_enabled)
            return ConfigHandler.NEW_SPEED;

        Item item = player.getHeldItemMainhand().getItem();
        ResourceLocation source = ForgeRegistries.ITEMS.getKey(item);

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
        return ConfigHandler.prevent_cooldown && ConfigHandler.mod_enabled ? 1.0F : player.getCooledAttackStrength(adjustTicks);
    }

    public static boolean shouldCauseReequipAnimation(@Nonnull ItemStack from, @Nonnull ItemStack to, int slot) {
        if (!ConfigHandler.prevent_reequip || !ConfigHandler.mod_enabled) {
            return ForgeHooksClient.shouldCauseReequipAnimation(from, to, slot);
        }

        boolean fromInvalid = from.isEmpty();
        boolean toInvalid = to.isEmpty();

        if (fromInvalid && toInvalid) return false;
        if (fromInvalid || toInvalid) return true;

        return !ItemStack.areItemsEqualIgnoreDurability(from, to);
    }

    @SuppressWarnings("unused") /* This method is used by ASM */
    public static void armSway(MatrixStack matrixStackIn, Quaternion quaternion) {
        if (!ConfigHandler.prevent_sway || !ConfigHandler.mod_enabled) {
            matrixStackIn.rotate(quaternion);
            matrixStackIn.rotate(quaternion);
        }
    }
}
