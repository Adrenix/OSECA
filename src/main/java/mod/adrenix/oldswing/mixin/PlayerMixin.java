package mod.adrenix.oldswing.mixin;

import mod.adrenix.oldswing.interfaces.CameraPitch;
import mod.adrenix.oldswing.MixinInjector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements CameraPitch
{
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level)
    {
        super(entityType, level);
    }

    @Inject(method = "sweepAttack", at = @At(value = "HEAD"), cancellable = true)
    protected void onSweepAttack(CallbackInfo callback)
    {
        if (!MixinInjector.shouldSweepAttack() && EnchantmentHelper.getSweepingDamageRatio((Player) (Object) this) <= 0.0F)
            callback.cancel();
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setSpeed(F)V"))
    protected void onAiStep(CallbackInfo callback)
    {
        float f = (float) (Math.atan(-this.getDeltaMovement().y * 0.20000000298023224D) * 15.0D);

        if (this.onGround || this.getHealth() <= 0.0F)
            f = 0.0F;

        this.setCameraPitch(this.getCameraPitch() + (f - this.getCameraPitch()) * 0.8F);
    }
}
