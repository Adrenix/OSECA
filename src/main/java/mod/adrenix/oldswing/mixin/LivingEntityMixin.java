package mod.adrenix.oldswing.mixin;

import mod.adrenix.oldswing.MixinHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Effects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    @Shadow public abstract boolean hasEffect(Effect potionIn);
    @Shadow public abstract EffectInstance getEffect(Effect potionIn);

    @Inject(method = "getCurrentSwingDuration", at = @At(value = "HEAD"), cancellable = true)
    protected void onGetCurrentSwingDuration(CallbackInfoReturnable<Integer> callback)
    {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null)
            return;

        int mod = MixinHelper.getSwingSpeed(player);
        if (EffectUtils.hasDigSpeed(player))
            callback.setReturnValue(mod - (1 + EffectUtils.getDigSpeedAmplification(player)));
        else
            callback.setReturnValue(this.hasEffect(Effects.DIG_SLOWDOWN) ? mod + (1 + this.getEffect(Effects.DIG_SLOWDOWN).getAmplifier()) * 2 : mod);
    }
}
