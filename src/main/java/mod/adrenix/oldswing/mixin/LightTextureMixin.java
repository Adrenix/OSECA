package mod.adrenix.oldswing.mixin;

import mod.adrenix.oldswing.MixinInjector;
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

    @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
    protected void onTick(CallbackInfo callback)
    {
        if (MixinInjector.oldLightFlicker())
        {
            this.updateLightTexture = true;
            callback.cancel();
        }
    }
}