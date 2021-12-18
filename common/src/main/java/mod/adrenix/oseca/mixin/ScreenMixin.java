package mod.adrenix.oseca.mixin;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import mod.adrenix.oseca.MixinInjector;
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
        if (MixinInjector.EyeCandy.oldTooltips())
            fillGradient(matrix, buffer, i1, i2, i3, i4, i5, i6, i7);
    }

    /**
     * Renders the old school tooltips from Minecraft Beta 1.7 and before.
     * Controlled by the old tooltips toggle.
     */
    @Inject(method = "renderTooltipInternal", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack$Pose;pose()Lcom/mojang/math/Matrix4f;"))
    protected void onRenderTooltipInternal(PoseStack matrices, List<ClientTooltipComponent> clientTooltip, int x0, int y0, CallbackInfo callback)
    {
        if (MixinInjector.EyeCandy.oldTooltips())
            return;

        int maxWidth = 0;

        for (ClientTooltipComponent tooltipComponent : clientTooltip) {
            int width = tooltipComponent.getWidth(this.font);
            if (width > maxWidth) {
                maxWidth = width;
            }
        }

        int x1 = x0 + 12;
        int y1 = y0 - 12;
        int x2 = maxWidth;
        int y2 = 8;

        if (clientTooltip.size() > 1) {
            y2 += 2 + (clientTooltip.size() - 1) * 10;
        }

        if (x1 + maxWidth > this.width) {
            x1 -= 28 + maxWidth;
        }

        if (y1 + y2 + 6 > this.height) {
            y1 = this.height - y2 - 6;
        }

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        Matrix4f matrix4f = matrices.last().pose();

        fillGradient(matrix4f, buffer, x1 - 3, y1 - 3, x1 + x2 + 3, y1 + y2 + 3, 400, 0xc0000000, 0xc0000000);
    }
}