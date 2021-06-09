package mod.adrenix.oldswing.mixin;

import mod.adrenix.oldswing.MixinHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin
{
    @Shadow public abstract boolean isPotionActive(Potion potionIn);
    @Shadow @Nullable public abstract PotionEffect getActivePotionEffect(Potion potionIn);

    /**
     * @author Adrenix
     * @reason Needed to change swing speed animation.
     */
    @Overwrite
    public int getArmSwingAnimationEnd()
    {
        AbstractClientPlayer player = Minecraft.getMinecraft().player;

        if (player == null)
            return 6;

        int mod = MixinHelper.getSwingSpeed(player);

        if (this.isPotionActive(MobEffects.HASTE))
            return mod - (1 + Objects.requireNonNull(this.getActivePotionEffect(MobEffects.HASTE)).getAmplifier());
        else
            return this.isPotionActive(MobEffects.MINING_FATIGUE) ? mod + (1 + Objects.requireNonNull(this.getActivePotionEffect(MobEffects.MINING_FATIGUE)).getAmplifier()) * 2 : mod;
    }
}
