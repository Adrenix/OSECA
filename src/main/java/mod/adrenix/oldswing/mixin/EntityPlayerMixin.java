package mod.adrenix.oldswing.mixin;

import mod.adrenix.oldswing.MixinHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public class EntityPlayerMixin
{
    @Inject(method = "spawnSweepParticles", at = @At(value = "HEAD"), cancellable = true)
    protected void onSpawnSweepParticles(CallbackInfo callback)
    {
        if (!MixinHelper.shouldSweepAttack() && EnchantmentHelper.getSweepingDamageRatio((EntityPlayer) (Object) this) <= 0.0F)
            callback.cancel();
    }
}
