package mod.adrenix.oseca.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import mod.adrenix.oseca.MixinInjector;
import mod.adrenix.oseca.config.DefaultConfig;
import mod.adrenix.oseca.injector.IReequipSlot;
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
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin
{
    @Shadow private float mainHandHeight;
    @Shadow private ItemStack mainHandItem;
    @Shadow private ItemStack offHandItem;
    @Shadow @Final private Minecraft minecraft;

    /**
     * Blocks the rotation of the hand renderer when arm sway is disabled.
     */
    @Redirect(method = "renderHandsWithItems", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lcom/mojang/math/Quaternion;)V"))
    protected void armSwayProxy(PoseStack matrices, Quaternion q, float partialTicks, PoseStack ps2, MultiBufferSource.BufferSource b, LocalPlayer player)
    {
        if (MixinInjector.Animation.shouldArmSway())
        {
            float f2 = Mth.lerp(partialTicks, player.xBobO, player.xBob);
            float f3 = Mth.lerp(partialTicks, player.yBobO, player.yBob);
            matrices.mulPose(Vector3f.XP.rotationDegrees((player.getViewXRot(partialTicks) - f2) * 0.1F));
            matrices.mulPose(Vector3f.YP.rotationDegrees((player.getViewYRot(partialTicks) - f3) * 0.1F));
        }
    }

    /**
     * Forces the attack strength to be 1.0F when cooldown animation is disabled.
     */
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F"))
    protected float onGetStrength(LocalPlayer player, float f)
    {
        if (MixinInjector.Animation.shouldCooldown())
            return player.getAttackStrengthScale(f);
        return 1.0F;
    }

    /**
     * Prevents the off hand from reequipping when only the stack size changes.
     * Controlled by reequipping toggle.
     */
    @ModifyArg(method = "tick", index = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F", ordinal = 3))
    protected float onOffHandTick(float current)
    {
        LocalPlayer player = this.minecraft.player;
        if (MixinInjector.Animation.shouldReequip() || player == null)
            return current;

        ItemStack offStack = player.getOffhandItem();

        if (this.offHandItem.is(offStack.getItem()) && this.offHandItem.getCount() != offStack.getCount())
            return 0.0F;
        return current;
    }

    /**
     * Blocks the addition assignment operator from incrementing the hand height when unsolicited reequipping is disabled.
     * Controlled by reequip toggle.
     */
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F", ordinal = 2))
    protected float onTickIncreaseMain(float current, float min, float max)
    {
        if (MixinInjector.Animation.shouldReequip())
            return Mth.clamp(current, min, max);
        return 0.0F;
    }

    /**
     * Forces the item matching to return false on the main hand, so we can track what the last held item was.
     * This prevents reequip animation issues when going from an item in the main hand to air.
     * Controlled by reequip toggle.
     */
    @Redirect(method = "tick", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/world/item/ItemStack;matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    protected boolean onMainItemTick(ItemStack from, ItemStack to)
    {
        if (MixinInjector.Animation.shouldReequip())
            return ItemStack.matches(from, to);
        return false;
    }

    /**
     * Prevents visual bug from flashing the previously held item when pulling an item out of the main hand.
     * Controlled by reequip toggle.
     */
    @ModifyArg(method = "renderHandsWithItems", index = 5, at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    protected ItemStack onRenderItem(AbstractClientPlayer player, float f1, float f2, InteractionHand hand, float f3, ItemStack itemStack, float f4, PoseStack matrix, MultiBufferSource buffer, int i)
    {
        return MixinInjector.Inject.getLastItem(itemStack, this.mainHandItem, player.getMainHandItem(), (IReequipSlot) player);
    }

    /**
     * Simulate old reequip logic and animation.
     * Controlled by reequip and cooldown animation toggles.
     */
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F", ordinal = 3))
    protected void onTick(CallbackInfo callback)
    {
        LocalPlayer player = this.minecraft.player;
        if (MixinInjector.Animation.shouldReequip() || player == null)
            return;

        IReequipSlot injector = (IReequipSlot) player;
        ItemStack main = player.getMainHandItem();
        int slot = player.getInventory().selected;

        if (main.isEmpty() && this.mainHandItem.isEmpty() && !injector.getReequip())
            injector.setReequip(false);
        else if (slot != injector.getLastSlot())
        {
            injector.setLastSlot(slot);
            injector.setReequip(true);
        }

        // Needed to fix weird bug that occurs when pulling an item out from the main hand while in an inventory.
        boolean isUnequipped = this.mainHandItem.toString().equals("0 air") && main.toString().equals("1 air");
        boolean isItemChanged = !this.mainHandItem.is(main.getItem());
        boolean isSlotUpdated = slot == injector.getLastSlot() && isItemChanged && !injector.getReequip();
        boolean isHandChanged = isUnequipped && !injector.getLastItem().isEmpty() && !injector.getReequip();

        if (isSlotUpdated || isHandChanged)
            injector.setReequip(true);

        if (isUnequipped)
            this.mainHandItem = injector.getLastItem();

        if (slot == injector.getLastSlot() && !injector.getReequip())
            this.mainHandItem = player.getMainHandItem();

        if (MixinInjector.Animation.shouldCooldown())
        {
            float scale = player.getAttackStrengthScale(1.0F);
            this.mainHandHeight += Mth.clamp((!injector.getReequip() ? scale * scale * scale : 0.0f) - this.mainHandHeight, -0.4F, 0.4F);
        }
        else
            this.mainHandHeight = Mth.clamp(this.mainHandHeight + (injector.getReequip() ? -0.4F : 0.4F), 0.0F, 1.0F);

        if (this.mainHandHeight < 0.1F)
            injector.setReequip(false);
    }

    /**
     * Enhances photosensitivity mode by completely disabling any hand movement when placing or interacting.
     * Only checks for global photosensitivity since this will break reequip animations if checking by item.
     */
    @Inject(method = "applyItemArmTransform", at = @At(value = "HEAD"), cancellable = true)
    protected void onApplyItemArmTransform(PoseStack matrices, HumanoidArm arm, float f, CallbackInfo callback)
    {
        if (!MixinInjector.isModEnabled())
            return;

        f = MixinInjector.Swing.getGlobalSpeed() == DefaultConfig.Swings.DISABLED ? 0 : f;
        int i = arm == HumanoidArm.RIGHT ? 1 : -1;

        matrices.translate((float) i * 0.56F, -0.52F + f * -0.6F, -0.72F);
        callback.cancel();
    }

    /**
     * Simulates old item holding positions. (Not sure about the values here...)
     */
    @Redirect(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V", ordinal = 12))
    protected void itemInHandProxy(PoseStack matrices, double x, double y, double z, AbstractClientPlayer player, float f1, float f2, InteractionHand hand, float swingProgress, ItemStack itemStack, float equipProgress)
    {
        if (MixinInjector.EyeCandy.oldItemHolding())
        {
            boolean isMain = hand == InteractionHand.MAIN_HAND;
            HumanoidArm arm = isMain ? player.getMainArm() : player.getMainArm().getOpposite();
            boolean isRight = arm == HumanoidArm.RIGHT;

            float dz = Mth.sin(swingProgress * (float) Math.PI);
            float dx = Mth.sin(Mth.sqrt(swingProgress) * (float) Math.PI);
            int flip = isRight ? 1 : -1;

            matrices.translate(flip * (-dx * 0.4F), Mth.sin(Mth.sqrt(swingProgress) * (float) Math.PI * 2.0F) * 0.2F, -dz * 0.2F);
            matrices.translate(0.05F, 0.0045F, 0.035F);

            matrices.mulPose(Vector3f.XP.rotationDegrees(-0.5F));
            matrices.mulPose(Vector3f.YP.rotationDegrees(5F));
            matrices.mulPose(Vector3f.ZP.rotationDegrees(-0.8F));
        }
        else
            matrices.translate(x, y, z);
    }
}