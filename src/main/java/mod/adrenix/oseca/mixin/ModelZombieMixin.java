package mod.adrenix.oseca.mixin;

import mod.adrenix.oseca.config.MixinConfig;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.entity.monster.EntityZombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

/* IMPORTANT */

/**
 * Any changes made here should also be reflected in the
 * ModelZombieVillagerMixin.
 */

@Mixin(ModelZombie.class)
public abstract class ModelZombieMixin
{
    /**
     * Disables the arm swing animations for zombies.
     * Controlled by the zombie arm raise ╥toggle.
     */
    @Redirect(method = "setRotationAngles", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/monster/EntityZombie;isArmsRaised()Z"))
    protected boolean onSetRotationAngles(EntityZombie instance)
    {
        if (MixinConfig.Animation.shouldZombieArmRaise())
            return instance.isArmsRaised();
        return false;
    }

    /**
     * Prevents zombie arm from swinging after attacking the player.
     * Controlled by the zombie arm raise toggle.
     */
    @ModifyArg(method = "setRotationAngles", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;sin(F)F"))
    protected float onGetSwingProgress(float vanilla)
    {
        if (MixinConfig.Animation.shouldZombieArmRaise())
            return vanilla;
        return 0.0F;
    }
}
