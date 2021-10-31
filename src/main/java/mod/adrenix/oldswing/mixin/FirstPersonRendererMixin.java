package mod.adrenix.oldswing.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.adrenix.oldswing.MixinInjector;
import mod.adrenix.oldswing.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonRenderer.class)
public abstract class FirstPersonRendererMixin
{
    @Shadow private float mainHandHeight;
    @Shadow private float offHandHeight;
    @Shadow private ItemStack mainHandItem;
    @Shadow private ItemStack offHandItem;
    @Shadow @Final private Minecraft minecraft;

    @Redirect(method = "renderHandsWithItems", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/matrix/MatrixStack;mulPose(Lnet/minecraft/util/math/vector/Quaternion;)V"))
    protected void armSwayProxy(MatrixStack matrix, Quaternion q, float partialTicks, MatrixStack matrix2, IRenderTypeBuffer.Impl buffer, ClientPlayerEntity player)
    {
        if (MixinInjector.shouldArmSway())
        {
            float f2 = MathHelper.lerp(partialTicks, player.xBobO, player.xBob);
            float f3 = MathHelper.lerp(partialTicks, player.yBobO, player.yBob);
            matrix.mulPose(Vector3f.XP.rotationDegrees((player.getViewXRot(partialTicks) - f2) * 0.1F));
            matrix.mulPose(Vector3f.YP.rotationDegrees((player.getViewYRot(partialTicks) - f3) * 0.1F));
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/ClientPlayerEntity;getAttackStrengthScale(F)F"), cancellable = true)
    protected void onTick(CallbackInfo callback)
    {
        ClientPlayerEntity player = this.minecraft.player;
        if (player == null)
            return;

        ItemStack itemStackMain = player.getMainHandItem();
        ItemStack itemStackOff = player.getOffhandItem();

        float scale = MixinInjector.getCooldownAnimationFloat(player, 1.0F);
        boolean reequipMain = MixinInjector.shouldCauseReequipAnimation(this.mainHandItem, itemStackMain, player.inventory.selected);
        boolean reequipOff = MixinInjector.shouldCauseReequipAnimation(this.offHandItem, itemStackOff, -1);

        if (!reequipMain && this.mainHandItem != itemStackMain)
            this.mainHandItem = itemStackMain;
        if (!reequipOff && this.offHandItem != itemStackOff)
            this.offHandItem = itemStackOff;

        this.mainHandHeight += MathHelper.clamp((!reequipMain ? scale * scale * scale : 0.0F) - this.mainHandHeight, -0.4F, 0.4F);
        this.offHandHeight += MathHelper.clamp((float)(!reequipOff ? 1 : 0) - this.offHandHeight, -0.4F, 0.4F);

        if (this.mainHandHeight < 0.1F)
            this.mainHandItem = itemStackMain;
        if (this.offHandHeight < 0.1F)
            this.offHandItem = itemStackOff;

        callback.cancel();
    }

    @Inject(method = "applyItemArmTransform", at = @At(value = "HEAD"), cancellable = true)
    protected void onApplyItemArmTransform(MatrixStack matrix, HandSide hand, float f, CallbackInfo callback)
    {
        if (!MixinInjector.isModEnabled())
            return;

        f = MixinInjector.getSwingSpeed() == ConfigHandler.DISABLED ? 0 : f;
        int i = hand == HandSide.RIGHT ? 1 : -1;

        matrix.translate((float) i * 0.56F, -0.52F + f * -0.6F, -0.72F);
        callback.cancel();
    }

    @Redirect(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/matrix/MatrixStack;translate(DDD)V", ordinal = 12))
    protected void itemInHandProxy(MatrixStack matrix, double x, double y, double z, AbstractClientPlayerEntity player, float f1, float f2, Hand hand, float swingProgress, ItemStack itemStack, float equipProgress)
    {
        if (MixinInjector.oldItemHolding())
        {
            boolean isMain = hand == Hand.MAIN_HAND;
            HandSide arm = isMain ? player.getMainArm() : player.getMainArm().getOpposite();
            boolean isRight = arm == HandSide.RIGHT;

            float dz = MathHelper.sin(swingProgress * (float) Math.PI);
            float dx = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI);
            int flip = isRight ? 1 : -1;

            matrix.translate(flip * (-dx * 0.4F), MathHelper.sin(MathHelper.sqrt(swingProgress) * (float) Math.PI * 2.0F) * 0.2F, -dz * 0.2F);
            matrix.translate(0.05F, 0.0045F, 0.035F);

            matrix.mulPose(Vector3f.XP.rotationDegrees(-0.5F));
            matrix.mulPose(Vector3f.YP.rotationDegrees(5F));
            matrix.mulPose(Vector3f.ZP.rotationDegrees(-0.8F));
        }
        else
            matrix.translate(x, y, z);
    }
}
