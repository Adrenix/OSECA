package mod.adrenix.oldswing.mixin;

import mod.adrenix.oldswing.interfaces.CameraPitch;
import mod.adrenix.oldswing.MixinInjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements CameraPitch
{
    /* Camera Pitching */

    public float cameraPitch = 0.0F;
    public float prevCameraPitch = 0.0F;

    public void setCameraPitch(float cameraPitch)
    {
        this.cameraPitch = cameraPitch;
    }

    public void setPrevCameraPitch(float prevCameraPitch)
    {
        this.prevCameraPitch = prevCameraPitch;
    }

    public float getCameraPitch()
    {
        return cameraPitch;
    }

    public float getPrevCameraPitch()
    {
        return prevCameraPitch;
    }

    /* Mixin Injections */

    @Inject(method = "getCurrentSwingDuration", at = @At(value = "HEAD"), cancellable = true)
    protected void onGetCurrentSwingDuration(CallbackInfoReturnable<Integer> callback)
    {
        AbstractClientPlayer player = Minecraft.getInstance().player;
        if (player == null)
            return;

        int mod = MixinInjector.getSwingSpeed(player);

        if (MixinInjector.isSpeedGlobal())
            callback.setReturnValue(MixinInjector.getSwingSpeed());
        else if (MixinInjector.isOverridingHaste() && player.hasEffect(MobEffects.DIG_SPEED))
            callback.setReturnValue(MixinInjector.getHasteSpeed());
        else if (MixinInjector.isOverridingFatigue() && player.hasEffect(MobEffects.DIG_SLOWDOWN))
            callback.setReturnValue(MixinInjector.getFatigueSpeed());
        else if (MobEffectUtil.hasDigSpeed(player))
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

    @Inject(method = "breakItem", at = @At(value = "HEAD"), cancellable = true)
    protected void onBreakItem(ItemStack itemStack, CallbackInfo callback)
    {
        if (!MixinInjector.shouldToolsDisintegrate())
            callback.cancel();
    }

    @Inject(method = "baseTick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;hurtTime:I", ordinal = 0))
    protected void onBaseTick(CallbackInfo callback)
    {
        this.setPrevCameraPitch(this.getCameraPitch());
    }
}
