package mod.adrenix.oldswing.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.oldswing.MixinInjector;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin
{
    @Shadow protected abstract void fillRect(BufferBuilder builder, int x, int y, int w, int h, int r, int g, int b, int alpha);

    @Inject(method = "renderGuiItemDecorations(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "RETURN"))
    protected void onRenderGuiItemDecorations(FontRenderer font, ItemStack stack, int x, int y, String s, CallbackInfo callback)
    {
        if (MixinInjector.oldDamageColors() && stack.isDamaged())
        {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableBlend();

            Tessellator tesselator = Tessellator.getInstance();
            BufferBuilder builder = tesselator.getBuilder();

            double health = (double) stack.getDamageValue() / (double) stack.getMaxDamage();
            int width = Math.round(13.0F - (float) health * 13.0F);
            int damage = (int) Math.round(255D - ((double) stack.getDamageValue() * 255D) / (double) stack.getMaxDamage());

            int rgb_fg = 255 - damage << 16 | damage << 8;
            int rgb_bg = (255 - damage) / 4 << 16 | 0x3F00;

            this.fillRect(builder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
            this.fillRect(builder, x + 2, y + 13, 12, 1, rgb_bg >> 16 & 0x0FF, rgb_bg >> 8 & 0x0FF, rgb_bg & 0x0FF, 255);
            this.fillRect(builder, x + 2, y + 13, width, 1, rgb_fg >> 16 & 0x0FF, rgb_fg >> 8 & 0x0FF, rgb_fg & 0x0FF, 255);

            RenderSystem.enableBlend();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }
    }
}
