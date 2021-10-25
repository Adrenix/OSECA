package mod.adrenix.oldswing.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import mod.adrenix.oldswing.MixinInjector;
import mod.adrenix.oldswing.config.DefaultConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin
{
    @Shadow private float mainHandHeight;
    @Shadow private float offHandHeight;
    @Shadow private ItemStack mainHandItem;
    @Shadow private ItemStack offHandItem;
    @Shadow @Final private Minecraft minecraft;

    @Redirect(method = "renderHandsWithItems", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lcom/mojang/math/Quaternion;)V"))
    protected void armSwayProxy(PoseStack stack, Quaternion q, float partialTicks, PoseStack ps2, MultiBufferSource.BufferSource b, LocalPlayer player)
    {
        if (MixinInjector.shouldArmSway())
        {
            float f2 = Mth.lerp(partialTicks, player.xBobO, player.xBob);
            float f3 = Mth.lerp(partialTicks, player.yBobO, player.yBob);
            stack.mulPose(Vector3f.XP.rotationDegrees((player.getViewXRot(partialTicks) - f2) * 0.1F));
            stack.mulPose(Vector3f.YP.rotationDegrees((player.getViewYRot(partialTicks) - f3) * 0.1F));
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"), cancellable = true)
    protected void onTick(CallbackInfo callback)
    {
        AbstractClientPlayer player = this.minecraft.player;
        if (player == null)
            return;

        ItemStack itemStackMain = player.getMainHandItem();
        ItemStack itemStackOff = player.getOffhandItem();

        float scale = MixinInjector.getCooldownAnimationFloat(player, 1.0F);
        boolean reequipMain = MixinInjector.shouldCauseReequipAnimation(this.mainHandItem, itemStackMain);
        boolean reequipOff = MixinInjector.shouldCauseReequipAnimation(this.offHandItem, itemStackOff);

        if (!reequipMain && this.mainHandItem != itemStackMain)
            this.mainHandItem = itemStackMain;
        if (!reequipOff && this.offHandItem != itemStackOff)
            this.offHandItem = itemStackOff;

        this.mainHandHeight += Mth.clamp((!reequipMain ? scale * scale * scale : 0.0F) - this.mainHandHeight, -0.4F, 0.4F);
        this.offHandHeight += Mth.clamp((float) (!reequipOff ? 1 : 0) - this.offHandHeight, -0.4F, 0.4F);

        if (this.mainHandHeight < 0.1F)
            this.mainHandItem = itemStackMain;
        if (this.offHandHeight < 0.1F)
            this.offHandItem = itemStackOff;

        callback.cancel();
    }

    @Inject(method = "applyItemArmTransform", at = @At(value = "HEAD"), cancellable = true)
    protected void onApplyItemArmTransform(PoseStack stack, HumanoidArm arm, float f, CallbackInfo callback)
    {
        if (!MixinInjector.isModEnabled())
            return;

        f = MixinInjector.getSwingSpeed() == DefaultConfig.Swings.DISABLED ? 0 : f;
        int i = arm == HumanoidArm.RIGHT ? 1 : -1;

        stack.translate((float) i * 0.56F, -0.52F + f * -0.6F, -0.72F);
        callback.cancel();
    }

    @Redirect(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V", ordinal = 12))
    protected void itemInHandProxy(PoseStack stack, double x, double y, double z, AbstractClientPlayer player, float f1, float f2, InteractionHand hand, float swingProgress, ItemStack itemStack, float equipProgress)
    {
        if (MixinInjector.oldItemHolding())
        {
            float dz = Mth.sin(swingProgress * (float) Math.PI);
            float dx = Mth.sin(Mth.sqrt(swingProgress) * (float) Math.PI);

            stack.translate(-dx * 0.4F, Mth.sin(Mth.sqrt(swingProgress) * (float) Math.PI * 2.0F) * 0.2F, -dz * 0.2F);
            stack.translate(0.05F, 0.0045F, 0.035F);

            stack.mulPose(Vector3f.XP.rotationDegrees(-0.5F));
            stack.mulPose(Vector3f.YP.rotationDegrees(5F));
            stack.mulPose(Vector3f.ZP.rotationDegrees(-0.8F));
        }
        else
            stack.translate(x, y, z);
    }
}
