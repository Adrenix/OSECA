package mod.adrenix.oldswing.mixin;

import mod.adrenix.oldswing.MixinInjector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin
{
    @Inject(method = "sweepAttack", at = @At(value = "HEAD"), cancellable = true)
    protected void onSweepAttack(CallbackInfo callback)
    {
        if (!MixinInjector.shouldSweepAttack() && EnchantmentHelper.getSweepingDamageRatio((Player) (Object) this) <= 0.0F)
            callback.cancel();
    }
}
