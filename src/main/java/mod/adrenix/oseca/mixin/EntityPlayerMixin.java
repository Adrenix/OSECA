package mod.adrenix.oseca.mixin;

import mod.adrenix.oseca.config.MixinConfig;
import mod.adrenix.oseca.util.SoundRedirect;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin
{
    /**
     * Disables the sweep attack particles that appear when attacking multiple entities at once.
     * Controlled by the sweep attack toggle.
     */
    @Inject(method = "spawnSweepParticles", at = @At(value = "HEAD"), cancellable = true)
    protected void onSpawnSweepParticles(CallbackInfo callback)
    {
        if (!MixinConfig.Animation.shouldSweepAttack() && EnchantmentHelper.getSweepingDamageRatio((EntityPlayer) (Object) this) <= 0.0F)
            callback.cancel();
    }

    /**
     * Disables sounds related to attacking.
     * Controlled by the sound attack toggle.
     */
    @ModifyArg(method = "attackTargetEntityWithCurrentItem", index = 6, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/EntityPlayer;DDDLnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FF)V"))
    protected float onAttackSounds(float volume)
    {
        if (MixinConfig.Sound.shouldAttack())
            return volume;
        return 0.0F;
    }

    /**
     * Redirects the vanilla hurt sound to the old "oof" sound.
     * Controlled by the redirect hurt toggle.
     */
    @Inject(method = "getHurtSound", at = @At(value = "HEAD"), cancellable = true)
    protected void onGetHurtSound(DamageSource damageSource, CallbackInfoReturnable<SoundEvent> callback)
    {
        if (MixinConfig.Sound.shouldRedirectHurt())
            callback.setReturnValue(SoundRedirect.PLAYER_HURT);
    }
}
