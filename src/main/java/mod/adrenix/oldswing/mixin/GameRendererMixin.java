package mod.adrenix.oldswing.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.adrenix.oldswing.MixinInjector;
import mod.adrenix.oldswing.interfaces.CameraPitch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin
{
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "bobView", at = @At(value = "HEAD"), cancellable = true)
    protected void onBobView(MatrixStack matrix, float partialTicks, CallbackInfo callback)
    {
        if (MixinInjector.shouldBobVertical() && this.minecraft.getCameraEntity() instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) this.minecraft.getCameraEntity();
            CameraPitch injector = (CameraPitch) player;

            float f = player.walkDist - player.walkDistO;
            float f1 = -(player.walkDist + f * partialTicks);
            float f2 = MathHelper.lerp(partialTicks, player.oBob, player.bob);
            float f3 = MathHelper.lerp(partialTicks, injector.getPrevCameraPitch(), injector.getCameraPitch());

            matrix.translate(MathHelper.sin(f1 * (float) Math.PI) * f2 * 0.5F, -Math.abs(MathHelper.cos(f1 * (float) Math.PI) * f2), 0.0F);
            matrix.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.sin(f1 * (float) Math.PI) * f2 * 3.0F));
            matrix.mulPose(Vector3f.XP.rotationDegrees(Math.abs(MathHelper.cos(f1 * (float) Math.PI - 0.2F) * f2) * 5.0F));
            matrix.mulPose(Vector3f.XP.rotationDegrees(f3));
            callback.cancel();
        }
    }
}
