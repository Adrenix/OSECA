package mod.adrenix.oldswing.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import mod.adrenix.oldswing.MixinInjector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public abstract class ScreenMixin extends GuiComponent
{
    @Shadow protected Font font;
    @Shadow public int width;
    @Shadow public int height;

    /* Don't call the 9 fill gradient methods. Instead, just call the one in the injection below this redirect. */
    @Redirect(method = "renderTooltipInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;fillGradient(Lcom/mojang/math/Matrix4f;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIII)V"))
    protected void renderTooltipProxy(Matrix4f matrix, BufferBuilder buffer, int i1, int i2, int i3, int i4, int i5, int i6, int i7)
    {
        if (MixinInjector.oldTooltips())
            fillGradient(matrix, buffer, i1, i2, i3, i4, i5, i6, i7);
    }

    @Inject(method = "renderTooltipInternal", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack$Pose;pose()Lcom/mojang/math/Matrix4f;"))
    protected void onRenderTooltipInternal(PoseStack pose, List<ClientTooltipComponent> clientTooltip, int par2, int par3, CallbackInfo callback)
    {
        if (MixinInjector.oldTooltips())
            return;

        int i = 0;

        for (ClientTooltipComponent tooltipComponent : clientTooltip) {
            int l = tooltipComponent.getWidth(this.font);
            if (l > i) {
                i = l;
            }
        }

        int j = par2 + 12;
        int k = par3 - 12;
        int i1 = i;
        int j1 = 8;

        if (clientTooltip.size() > 1) {
            j1 += 2 + (clientTooltip.size() - 1) * 10;
        }

        if (j + i > this.width) {
            j -= 28 + i;
        }

        if (k + j1 + 6 > this.height) {
            k = this.height - j1 - 6;
        }

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        Matrix4f matrix4f = pose.last().pose();

        fillGradient(matrix4f, buffer, j - 3, k - 3, j + i1 + 3, k + j1 + 3, 400, 0xc0000000, 0xc0000000);
    }
}