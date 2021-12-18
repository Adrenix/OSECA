package mod.adrenix.oseca.mixin;

import mod.adrenix.oseca.injector.ICameraPitch;
import mod.adrenix.oseca.MixinInjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ICameraPitch
{
    public LivingEntityMixin(EntityType<?> entityType, Level level)
    {
        super(entityType, level);
    }

    /* Camera Pitching Injections */

    public float cameraPitch = 0.0F;
    public float prevCameraPitch = 0.0F;

    public void setCameraPitch(float cameraPitch) { this.cameraPitch = cameraPitch; }
    public void setPrevCameraPitch(float prevCameraPitch) { this.prevCameraPitch = prevCameraPitch; }

    public float getCameraPitch() { return cameraPitch; }
    public float getPrevCameraPitch() { return prevCameraPitch; }

    /* Mixin Injections */

    /**
     * Controls how fast the swinging animation is.
     * Modified by numerous swing speed parameters controlled within the config.
     */
    @Inject(method = "getCurrentSwingDuration", at = @At(value = "HEAD"), cancellable = true)
    protected void onGetCurrentSwingDuration(CallbackInfoReturnable<Integer> callback)
    {
        AbstractClientPlayer player = Minecraft.getInstance().player;

        if (player == null)
            return;
        else if (this.getType() == EntityType.PLAYER)
        {
            Player entity = (Player) this.getType().tryCast(this);
            if (entity == null || !entity.isLocalPlayer())
                return;
        }

        int mod = MixinInjector.Swing.getSwingSpeed(player);

        if (MixinInjector.Swing.isSpeedGlobal())
            callback.setReturnValue(MixinInjector.Swing.getSwingSpeed());
        else if (MixinInjector.Swing.isOverridingHaste() && player.hasEffect(MobEffects.DIG_SPEED))
            callback.setReturnValue(MixinInjector.Swing.getHasteSpeed());
        else if (MixinInjector.Swing.isOverridingFatigue() && player.hasEffect(MobEffects.DIG_SLOWDOWN))
            callback.setReturnValue(MixinInjector.Swing.getFatigueSpeed());
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

    /**
     * Prevents the breaking animation and breaking sound when a tool runs out of durability.
     * Controlled by the tool disintegration toggle.
     */
    @Inject(method = "breakItem", at = @At(value = "HEAD"), cancellable = true)
    protected void onBreakItem(ItemStack itemStack, CallbackInfo callback)
    {
        if (!MixinInjector.Animation.shouldToolsDisintegrate())
            callback.cancel();
    }

    /**
     * Updates the previous camera pitching.
     */
    @Inject(method = "baseTick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;hurtTime:I", ordinal = 0))
    protected void onBaseTick(CallbackInfo callback)
    {
        this.setPrevCameraPitch(this.getCameraPitch());
    }
}