package mod.adrenix.oseca.mixin;

import mod.adrenix.oseca.config.MixinConfig;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Explosion.class)
public abstract class ExplosionMixin
{
    /**
     * Prevent the creation of modern explosion particles.
     * Controlled by the old explosion particles toggle.
     */
    @Redirect(method = "doExplosionB", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnParticle(Lnet/minecraft/util/EnumParticleTypes;DDDDDD[I)V"))
    protected void onSpawnParticle(World world, EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int[] parameters)
    {
        if (MixinConfig.Candy.oldExplosionParticles() && particleType != EnumParticleTypes.EXPLOSION_NORMAL)
            return;
        world.spawnParticle(particleType, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }
}
