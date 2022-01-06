package mod.adrenix.oseca.mixin;

import mod.adrenix.oseca.config.MixinConfig;
import mod.adrenix.oseca.util.SoundRedirect;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin extends Entity
{
    public EntityLivingBaseMixin(World world)
    {
        super(world);
    }

    @Shadow protected abstract boolean isPlayer();
    @Shadow public abstract boolean isPotionActive(Potion potionIn);
    @Shadow protected abstract SoundEvent getFallSound(int heightIn);
    @Shadow @Nullable public abstract PotionEffect getActivePotionEffect(Potion potionIn);

    /**
     * Controls how fast the swinging animation is.
     * Modified by numerous swing speed parameters controlled within the config.
     */
    @Inject(method = "getArmSwingAnimationEnd", at = @At(value = "HEAD"), cancellable = true)
    protected void onGetArmSwingAnimationEnd(CallbackInfoReturnable<Integer> callback)
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null || !this.isPlayer())
            return;

        int mod = MixinConfig.Swing.getSwingSpeed(player);

        if (MixinConfig.Swing.isSpeedGlobal())
            callback.setReturnValue(MixinConfig.Swing.getSwingSpeed());
        else if (MixinConfig.Swing.isOverridingHaste() && this.isPotionActive(MobEffects.HASTE))
            callback.setReturnValue(MixinConfig.Swing.getHasteSpeed());
        else if (MixinConfig.Swing.isOverridingFatigue() && this.isPotionActive(MobEffects.MINING_FATIGUE))
            callback.setReturnValue(MixinConfig.Swing.getFatigueSpeed());
        else if (this.isPotionActive(MobEffects.HASTE))
            callback.setReturnValue(mod - (1 + Objects.requireNonNull(this.getActivePotionEffect(MobEffects.HASTE)).getAmplifier()));
        else
        {
            callback.setReturnValue(
                this.isPotionActive(MobEffects.MINING_FATIGUE)
                    ? mod + (1 + Objects.requireNonNull(this.getActivePotionEffect(MobEffects.MINING_FATIGUE)).getAmplifier()) * 2
                    : mod
            );
        }
    }

    /**
     * Prevents the breaking sound when a tool runs out of durability.
     * Controlled by the tool explosion toggle.
     */
    @ModifyArg(method = "renderBrokenItemStack", index = 6, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/EntityPlayer;DDDLnet/minecraft/util/SoundEvent;Lnet/minecraft/util/SoundCategory;FF)V"))
    protected float onBrokenItemSound(float volume)
    {
        if (MixinConfig.Animation.shouldToolsExplode())
            return volume;
        return 0.0F;
    }

    /**
     * Prevents the breaking animation and breaking sound when a tool runs out of durability.
     * Controlled by the tool explosion toggle.
     */
    @Redirect(method = "renderBrokenItemStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnParticle(Lnet/minecraft/util/EnumParticleTypes;DDDDDD[I)V"))
    protected void onBrokenItemAnimation(World world, EnumParticleTypes particle, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int[] parameters)
    {
        if (MixinConfig.Animation.shouldToolsExplode())
            world.spawnParticle(particle, x, y, z, xSpeed, ySpeed, zSpeed, parameters);
    }

    /**
     * Redirects the vanilla falling sounds to a blank sound file.
     * Controlled by the redirect fall sounds toggle.
     */
    @Redirect(method = "fall", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getFallSound(I)Lnet/minecraft/util/SoundEvent;"))
    protected SoundEvent onFallSound(EntityLivingBase instance, int heightIn)
    {
        if (MixinConfig.Sound.shouldRedirectFall())
            return SoundRedirect.BLANK;
        else
            return this.getFallSound(heightIn);
    }
}
