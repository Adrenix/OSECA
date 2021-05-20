package mod.adrenix.oldswing.mixin;

import mod.adrenix.oldswing.config.TransformerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<? extends LivingEntity> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Shadow public abstract boolean isPotionActive(Effect potionIn);
    @Shadow public abstract EffectInstance getActivePotionEffect(Effect potionIn);

    /**
     * @author Adrenix
     * @reason Allows for the manipulation of the speed during swinging animation.
     */
    @Overwrite
    public int getArmSwingAnimationEnd() {
        ClientPlayerEntity player = Minecraft.getInstance().player;

        if (player == null)
            return 6;

        int mod = TransformerHelper.swingSpeed(player);

        if (EffectUtils.hasMiningSpeedup(player)) {
            return mod - (1 + EffectUtils.getMiningSpeedup(player));
        } else {
            return this.isPotionActive(Effects.MINING_FATIGUE) ?
                    mod + (1 + this.getActivePotionEffect(Effects.MINING_FATIGUE).getAmplifier()) * 2 :
                    mod;
        }
    }
}
