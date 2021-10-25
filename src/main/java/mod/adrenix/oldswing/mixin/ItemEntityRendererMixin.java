package mod.adrenix.oldswing.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import mod.adrenix.oldswing.MixinInjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin
{
    @Shadow
    @Final
    private ItemRenderer itemRenderer;

    @Redirect(
        method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lcom/mojang/math/Quaternion;)V"
        )
    )
    protected void rotationProxy(PoseStack stack, Quaternion q, ItemEntity entity, float f1, float f2)
    {
        if (MixinInjector.oldFloatingItems())
        {
            BakedModel model = this.itemRenderer.getModel(entity.getItem(), null, null, 0);

            if (!model.usesBlockLight())
            {
                stack.mulPose(Vector3f.YP.rotationDegrees(180F - Minecraft.getInstance().gameRenderer.getMainCamera().getYRot()));
                stack.mulPose(Vector3f.XP.rotationDegrees(-Minecraft.getInstance().gameRenderer.getMainCamera().getXRot()));
            }
            else
                stack.mulPose(Vector3f.YP.rotation(entity.getSpin(f2)));
        }
        else
            stack.mulPose(Vector3f.YP.rotation(entity.getSpin(f2)));
    }
}
