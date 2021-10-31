package mod.adrenix.oldswing.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.adrenix.oldswing.MixinInjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemRenderer.class)
public abstract class ItemEntityRendererMixin
{
    @Shadow @Final private net.minecraft.client.renderer.ItemRenderer itemRenderer;

    @Redirect(
        method = "render(Lnet/minecraft/entity/item/ItemEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/matrix/MatrixStack;mulPose(Lnet/minecraft/util/math/vector/Quaternion;)V"
        )
    )
    protected void rotationProxy(MatrixStack matrix, Quaternion q, ItemEntity entity, float f1, float f2)
    {
        if (MixinInjector.oldFloatingItems())
        {
            IBakedModel model = this.itemRenderer.getModel(entity.getItem(), entity.level, null);

            if (!model.usesBlockLight())
            {
                matrix.mulPose(Vector3f.YP.rotationDegrees(180F - Minecraft.getInstance().gameRenderer.getMainCamera().getYRot()));
                matrix.mulPose(Vector3f.XP.rotationDegrees(-Minecraft.getInstance().gameRenderer.getMainCamera().getXRot()));
            }
            else
                matrix.mulPose(Vector3f.YP.rotation(entity.getSpin(f2)));
        }
        else
            matrix.mulPose(Vector3f.YP.rotation(entity.getSpin(f2)));
    }
}
