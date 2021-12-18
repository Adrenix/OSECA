package mod.adrenix.oseca.mixin;

import mod.adrenix.oseca.MixinInjector;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin
{
    @Shadow private Entity entity;
    @Shadow private float eyeHeightOld;
    @Shadow private float eyeHeight;

    /**
     * Disables the smooth sneaking.
     */
    @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
    protected void onTick(CallbackInfo callback)
    {
        if (MixinInjector.Animation.shouldSneakSmooth())
            return;

        if (this.entity != null)
        {
            this.eyeHeightOld = this.eyeHeight;
            this.eyeHeight = this.entity.getEyeHeight();
        }

        callback.cancel();
    }
}