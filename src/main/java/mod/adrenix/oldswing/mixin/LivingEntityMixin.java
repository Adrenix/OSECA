package mod.adrenix.oldswing.mixin;

import mod.adrenix.oldswing.config.MixinHelper;
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

    @Shadow public abstract boolean hasEffect(Effect potionIn);
    @Shadow public abstract EffectInstance getEffect(Effect potionIn);

    /**
     * @author Adrenix
     * @reason Allows for the manipulation of the speed during swinging animation.
     */
    @Overwrite
    public int getCurrentSwingDuration() {
        ClientPlayerEntity player = Minecraft.getInstance().player;

        if (player == null)
            return 6;

        int mod = MixinHelper.swingSpeed(player);

        if (EffectUtils.hasDigSpeed(player)) {
            return mod - (1 + EffectUtils.getDigSpeedAmplification(player));
        } else {
            return this.hasEffect(Effects.DIG_SLOWDOWN) ?
                    mod + (1 + this.getEffect(Effects.DIG_SLOWDOWN).getAmplifier()) * 2 :
                    mod;
        }
    }
}
