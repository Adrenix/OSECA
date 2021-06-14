package mod.adrenix.oldswing.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.adrenix.oldswing.config.MixinHelper;
import net.minecraft.util.math.vector.Quaternion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MatrixStack.class)
public class MatrixStackMixin
{
    @Inject(method = "mulPose", at = @At(value = "HEAD"), cancellable = true)
    protected void onMulPose(Quaternion quaternion, CallbackInfo callback)
    {
        if (MixinHelper.armSwayFuse)
        {
            MixinHelper.armSwayFuse = false;
            callback.cancel();
        }
    }
}
