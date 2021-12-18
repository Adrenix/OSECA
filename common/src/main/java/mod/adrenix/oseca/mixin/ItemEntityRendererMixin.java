package mod.adrenix.oseca.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import mod.adrenix.oseca.MixinInjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin
{
    @Shadow @Final private ItemRenderer itemRenderer;

    /**
     * Forces the item entity's rotation to always face the player.
     * Controlled by the old floating item toggle.
     */
    @Redirect(
        method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lcom/mojang/math/Quaternion;)V"
        )
    )
    protected void rotationProxy(PoseStack mStack, Quaternion quaternion, ItemEntity itemEntity, float f1, float f2)
    {
        if (MixinInjector.EyeCandy.oldFloatingItems())
        {
            BakedModel model = this.itemRenderer.getModel(itemEntity.getItem(), null, null, 0);

            if (!model.usesBlockLight())
                mStack.mulPose(Vector3f.YP.rotationDegrees(180F - Minecraft.getInstance().gameRenderer.getMainCamera().getYRot()));
            else
                mStack.mulPose(Vector3f.YP.rotation(itemEntity.getSpin(f2)));
        }
        else
            mStack.mulPose(Vector3f.YP.rotation(itemEntity.getSpin(f2)));
    }

    /**
     * Rescales the depth of item entities to make them appear as if they are 2D.
     * Controlled by the old floating items toggle.
     *
     * Known issues with this:
     * - Enchantment glint renders weird.
     * - Lighting is inconsistent when looking around the float item.
     */
    @Inject(
        method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"
        )
    )
    protected void onRender(ItemEntity itemStack, float f1, float f2, PoseStack mStack, MultiBufferSource buffer, int combinedLight, CallbackInfo callback)
    {
        if (MixinInjector.EyeCandy.oldFloatingItems())
        {
            BakedModel model = this.itemRenderer.getModel(itemStack.getItem(), null, null, 0);
            if (!model.usesBlockLight())
                mStack.last().pose().multiply(Matrix4f.createScaleMatrix(1.0F, 1.0F, 0.0F));
        }
    }
}