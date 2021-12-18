package mod.adrenix.oseca.mixin;

import mod.adrenix.oseca.MixinInjector;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightTexture.class)
public abstract class LightTextureMixin
{
    @Shadow private boolean updateLightTexture;

    /**
     * Disables the light flickering from light emitting sources.
     * Controlled by the old light flicker toggle.
     */
    @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
    protected void onTick(CallbackInfo callback)
    {
        if (MixinInjector.EyeCandy.oldLightFlicker())
        {
            this.updateLightTexture = true;
            callback.cancel();
        }
    }
}