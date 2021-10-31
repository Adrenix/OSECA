package mod.adrenix.oldswing.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.adrenix.oldswing.MixinInjector;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.vector.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractGui
{
    @Shadow protected FontRenderer font;
    @Shadow public int width;
    @Shadow public int height;

    /* Don't call the 9 fill gradient methods. Instead, just call the one in the injection below this redirect. */
    @Redirect(method = "renderToolTip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;fillGradient(Lnet/minecraft/util/math/vector/Matrix4f;Lnet/minecraft/client/renderer/BufferBuilder;IIIIIII)V"))
    protected void renderTooltipProxy(Matrix4f matrix, BufferBuilder buffer, int i1, int i2, int i3, int i4, int i5, int i6, int i7)
    {
        if (MixinInjector.oldTooltips())
            fillGradient(matrix, buffer, i1, i2, i3, i4, i5, i6, i7);
    }

    @Inject(method = "renderToolTip", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/matrix/MatrixStack$Entry;pose()Lnet/minecraft/util/math/vector/Matrix4f;"))
    protected void onPose(MatrixStack matrixStack, List<? extends IReorderingProcessor> processor, int par2, int par3, FontRenderer font, CallbackInfo callback)
    {
        if (MixinInjector.oldTooltips())
            return;

        int i = 0;

        for(IReorderingProcessor ireorderingprocessor : processor) {
            int l = this.font.width(ireorderingprocessor);
            if (l > i) {
                i = l;
            }
        }

        int j = par2 + 12;
        int k = par3 - 12;
        int i1 = i;
        int j1 = 8;

        if (processor.size() > 1) {
            j1 += 2 + (processor.size() - 1) * 10;
        }

        if (j + i > this.width) {
            j -= 28 + i;
        }

        if (k + j1 + 6 > this.height) {
            k = this.height - j1 - 6;
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        Matrix4f matrix4f = matrixStack.last().pose();

        fillGradient(matrix4f, buffer, j - 3, k - 3, j + i1 + 3, k + j1 + 3, 400, 0xc0000000, 0xc0000000);
    }
}
