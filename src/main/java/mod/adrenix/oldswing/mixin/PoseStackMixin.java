package mod.adrenix.oldswing.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import mod.adrenix.oldswing.MixinInjector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PoseStack.class)
public abstract class PoseStackMixin
{
    @Inject(method = "mulPose", at = @At(value = "HEAD"), cancellable = true)
    protected void onMulPose(Quaternion quaternion, CallbackInfo callback)
    {
        if (MixinInjector.armSwayFuse)
        {
            MixinInjector.armSwayFuse = false;
            callback.cancel();
        }
    }
}
