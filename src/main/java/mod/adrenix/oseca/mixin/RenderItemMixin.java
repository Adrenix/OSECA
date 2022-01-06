package mod.adrenix.oseca.mixin;

import mod.adrenix.oseca.config.MixinConfig;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItem.class)
public abstract class RenderItemMixin
{
    /**
     * Draws the old durability bar colors.
     * Controlled by the old damage colors toggle.
     */
    @Shadow protected abstract void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha);

    @Inject(method = "renderItemOverlayIntoGUI", at = @At(value = "RETURN"))
    protected void onRenderDamage(FontRenderer font, ItemStack itemStack, int x, int y, String text, CallbackInfo callback)
    {
        if (!MixinConfig.Candy.oldDamageColors() || !itemStack.isItemDamaged())
            return;

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();

        double health = (double) itemStack.getItemDamage() / (double) itemStack.getMaxDamage();
        int width = Math.round(13.0F - (float) health * 13.0F);
        int damage = (int) Math.round(255D - ((double) itemStack.getItemDamage() * 255D) / (double) itemStack.getMaxDamage());

        int rgb_fg = 255 - damage << 16 | damage << 8;
        int rgb_bg = (255 - damage) / 4 << 16 | 0x3F00;

        this.draw(builder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
        this.draw(builder, x + 2, y + 13, 12, 1, rgb_bg >> 16 & 0x0FF, rgb_bg >> 8 & 0x0FF, rgb_bg & 0x0FF, 255);
        this.draw(builder, x + 2, y + 13, width, 1, rgb_fg >> 16 & 0x0FF, rgb_fg >> 8 & 0x0FF, rgb_fg & 0x0FF, 255);

        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }
}
