package mod.adrenix.oldswing.mixin;

import mod.adrenix.oldswing.MixinHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin
{
    @Inject(method = "sweepAttack", at = @At(value = "HEAD"), cancellable = true)
    protected void onSweepAttack(CallbackInfo callback)
    {
        if (!MixinHelper.shouldSweepAttack() && EnchantmentHelper.getSweepingDamageRatio((PlayerEntity) (Object) this) <= 0.0F)
            callback.cancel();
    }
}
