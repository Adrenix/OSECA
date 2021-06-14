package mod.adrenix.oldswing.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.adrenix.oldswing.config.MixinHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonRenderer.class)
public abstract class FirstPersonRendererMixin
{
    @Shadow private float mainHandHeight;
    @Shadow private float offHandHeight;
    @Shadow private ItemStack mainHandItem;
    @Shadow private ItemStack offHandItem;
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "renderHandsWithItems", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/matrix/MatrixStack;mulPose(Lnet/minecraft/util/math/vector/Quaternion;)V"))
    protected void onRenderHandsWithItems(float attackMul, MatrixStack matrix, IRenderTypeBuffer.Impl buffer, ClientPlayerEntity player, int light, CallbackInfo callback)
    {
        if (MixinHelper.shouldArmSway())
            return;

        MixinHelper.armSwayFuse = true;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/ClientPlayerEntity;getAttackStrengthScale(F)F"), cancellable = true)
    protected void onTick(CallbackInfo callback)
    {
        ClientPlayerEntity player = this.minecraft.player;
        if (player == null)
            return;

        ItemStack itemStackMain = player.getMainHandItem();
        ItemStack itemStackOff = player.getOffhandItem();

        float scale = MixinHelper.getCooldownAnimationFloat(player, 1.0F);
        boolean reequipMain = MixinHelper.shouldCauseReequipAnimation(this.mainHandItem, itemStackMain, player.inventory.selected);
        boolean reequipOff = MixinHelper.shouldCauseReequipAnimation(this.offHandItem, itemStackOff, -1);

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
}
