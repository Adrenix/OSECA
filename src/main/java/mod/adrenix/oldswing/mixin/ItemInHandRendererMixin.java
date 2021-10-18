package mod.adrenix.oldswing.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.oldswing.MixinInjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin
{
    @Shadow private float mainHandHeight;
    @Shadow private float offHandHeight;
    @Shadow private ItemStack mainHandItem;
    @Shadow private ItemStack offHandItem;
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "renderHandsWithItems", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lcom/mojang/math/Quaternion;)V"))
    protected void onRenderHandsWithItems(float f, PoseStack p, MultiBufferSource.BufferSource b, LocalPlayer l, int i, CallbackInfo callback)
    {
        if (MixinInjector.shouldArmSway())
            return;
        MixinInjector.armSwayFuse = true;
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
}
