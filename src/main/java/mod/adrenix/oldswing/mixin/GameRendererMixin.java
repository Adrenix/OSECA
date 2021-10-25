package mod.adrenix.oldswing.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import mod.adrenix.oldswing.interfaces.CameraPitch;
import mod.adrenix.oldswing.MixinInjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
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
    protected void onBobView(PoseStack stack, float partialTicks, CallbackInfo callback)
    {
        if (MixinInjector.shouldBobVertical() && this.minecraft.getCameraEntity() instanceof Player player)
        {
            CameraPitch injector = (CameraPitch) player;
            float f = player.walkDist - player.walkDistO;
            float f1 = -(player.walkDist + f * partialTicks);
            float f2 = Mth.lerp(partialTicks, player.oBob, player.bob);
            float f3 = Mth.lerp(partialTicks, injector.getPrevCameraPitch(), injector.getCameraPitch());

            stack.translate(Mth.sin(f1 * (float) Math.PI) * f2 * 0.5F, -Math.abs(Mth.cos(f1 * (float) Math.PI) * f2), 0.0F);
            stack.mulPose(Vector3f.ZP.rotationDegrees(Mth.sin(f1 * (float) Math.PI) * f2 * 3.0F));
            stack.mulPose(Vector3f.XP.rotationDegrees(Math.abs(Mth.cos(f1 * (float) Math.PI - 0.2F) * f2) * 5.0F));
            stack.mulPose(Vector3f.XP.rotationDegrees(f3));
            callback.cancel();
        }
    }
}
