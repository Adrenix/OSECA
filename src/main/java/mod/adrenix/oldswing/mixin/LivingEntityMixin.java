package mod.adrenix.oldswing.mixin;

import mod.adrenix.oldswing.MixinInjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    @Inject(method = "getCurrentSwingDuration", at = @At(value = "HEAD"), cancellable = true)
    protected void onGetCurrentSwingDuration(CallbackInfoReturnable<Integer> callback)
    {
        AbstractClientPlayer player = Minecraft.getInstance().player;
        if (player == null)
            return;

        int mod = MixinInjector.getSwingSpeed(player);

        if (MobEffectUtil.hasDigSpeed(player))
            callback.setReturnValue(mod - (1 + MobEffectUtil.getDigSpeedAmplification(player)));
        else
        {
            callback.setReturnValue(
                player.hasEffect(MobEffects.DIG_SLOWDOWN) ?
                    mod + (1 + Objects.requireNonNull(player.getEffect(MobEffects.DIG_SLOWDOWN)).getAmplifier()) * 2 :
                    mod
            );
        }
    }
}
