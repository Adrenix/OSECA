package mod.adrenix.oldswing.mixin;

import mod.adrenix.oldswing.MixinInjector;
import mod.adrenix.oldswing.interfaces.CameraPitch;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements CameraPitch
{
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world)
    {
        super(entityType, world);
    }

    @Inject(method = "sweepAttack", at = @At(value = "HEAD"), cancellable = true)
    protected void onSweepAttack(CallbackInfo callback)
    {
        if (!MixinInjector.shouldSweepAttack() && EnchantmentHelper.getSweepingDamageRatio((PlayerEntity) (Object) this) <= 0.0F)
            callback.cancel();
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSpeed(F)V"))
    protected void onAiStep(CallbackInfo callback)
    {
        float f = (float) (Math.atan(-this.getDeltaMovement().y * 0.20000000298023224D) * 15.0D);

        if (this.onGround || this.getHealth() <= 0.0F)
            f = 0.0F;

        this.setCameraPitch(this.getCameraPitch() + (f - this.getCameraPitch()) * 0.8F);
    }

    @Redirect(method = "drop(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/item/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;swing(Lnet/minecraft/util/Hand;)V"))
    protected void itemDroppingProxy(PlayerEntity player, Hand hand)
    {
        if (MixinInjector.shouldSwingDrop())
            this.swing(Hand.MAIN_HAND);
    }
}
