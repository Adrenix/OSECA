package mod.adrenix.oldswing.mixin;

import mod.adrenix.oldswing.MixinHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin
{
    @Shadow public abstract boolean isPotionActive(Potion potionIn);
    @Shadow @Nullable public abstract PotionEffect getActivePotionEffect(Potion potionIn);

    @Inject(method = "getArmSwingAnimationEnd", at = @At(value = "HEAD"), cancellable = true)
    protected void onGetArmSwingAnimationEnd(CallbackInfoReturnable<Integer> callback)
    {
        AbstractClientPlayer player = Minecraft.getMinecraft().player;
        if (player == null)
            return;

        int mod = MixinHelper.getSwingSpeed(player);
        if (this.isPotionActive(MobEffects.HASTE))
            callback.setReturnValue(mod - (1 + Objects.requireNonNull(this.getActivePotionEffect(MobEffects.HASTE)).getAmplifier()));
        else
            callback.setReturnValue(this.isPotionActive(MobEffects.MINING_FATIGUE) ? mod + (1 + Objects.requireNonNull(this.getActivePotionEffect(MobEffects.MINING_FATIGUE)).getAmplifier()) * 2 : mod);
    }
}
