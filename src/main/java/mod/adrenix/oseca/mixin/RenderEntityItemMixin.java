package mod.adrenix.oseca.mixin;

import mod.adrenix.oseca.config.MixinConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.entity.item.EntityItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(RenderEntityItem.class)
public abstract class RenderEntityItemMixin
{
    @Shadow @Final private RenderItem itemRenderer;

    /**
     * Forces the item entity's rotation to always face the player.
     * Controlled by the 2D item toggle.
     */
    @Redirect(method = "transformModelCount", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V"))
    protected void onRotateItem(float angle, float x, float y, float z, EntityItem entity, double d1, double d2, double d3, float d4, IBakedModel model)
    {
        if (MixinConfig.Candy.old2DItems() && !model.isGui3d())
            GlStateManager.rotate(180F - Objects.requireNonNull(Minecraft.getMinecraft().getRenderViewEntity()).rotationYaw, x, y, z);
        else
            GlStateManager.rotate(angle, x, y, z);
    }

    /**
     * Rescales the depth of item entities to make them appear as if they are 2D.
     * Controlled by the 2D item toggle.
     */
    @Inject(method = "doRender(Lnet/minecraft/entity/item/EntityItem;DDDFF)V", at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V"))
    protected void onRender(EntityItem entity, double x, double y, double z, float yaw, float partialTicks, CallbackInfo callback)
    {
        if (MixinConfig.Candy.old2DItems())
        {
            IBakedModel model = this.itemRenderer.getItemModelWithOverrides(entity.getItem(), entity.getEntityWorld(), null);
            if (!model.isGui3d())
                GlStateManager.scale(1.0F, 1.0F, 0.0F);
        }
    }

    /**
     * Disables diffused lighting to prevent 2D item entities from being too dark.
     * Diffused lighting gets re-enabled if the model is 3D.
     */
    @Redirect(method = "doRender(Lnet/minecraft/entity/item/EntityItem;DDDFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderHelper;enableStandardItemLighting()V"))
    protected void onStartGLRender(EntityItem entity, double x, double y, double z, float yaw, float partialTicks)
    {
        if (MixinConfig.Candy.old2DItems())
        {
            IBakedModel model = this.itemRenderer.getItemModelWithOverrides(entity.getItem(), entity.getEntityWorld(), null);
            if (!model.isGui3d())
                GlStateManager.disableLighting();
            else
                GlStateManager.enableLighting();
        }
        else
            RenderHelper.enableStandardItemLighting();
    }

    @Inject(method = "doRender(Lnet/minecraft/entity/item/EntityItem;DDDFF)V", at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/client/renderer/GlStateManager;popMatrix()V"))
    protected void onFinishItemRender(EntityItem entity, double x, double y, double z, float yaw, float partialTicks, CallbackInfo callback)
    {
        if (MixinConfig.Candy.old2DItems())
            GlStateManager.enableLighting();
    }
}
