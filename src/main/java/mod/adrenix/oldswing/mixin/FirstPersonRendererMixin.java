package mod.adrenix.oldswing.mixin;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.matrix.MatrixStack;
import mod.adrenix.oldswing.config.ConfigHandler;
import mod.adrenix.oldswing.config.MixinHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FirstPersonRenderer.class)
public abstract class FirstPersonRendererMixin {
    @Shadow private float oMainHandHeight;
    @Shadow private float mainHandHeight;
    @Shadow private float oOffHandHeight;
    @Shadow private float offHandHeight;
    @Shadow private ItemStack mainHandItem;
    @Shadow private ItemStack offHandItem;
    @Shadow @Final private Minecraft minecraft;

    /**
     * @author Adrenix
     * @reason Allows manipulation of the subtle arm sway animation.
     */
    @Overwrite
    public void renderHandsWithItems(float attackMul, MatrixStack matrix, IRenderTypeBuffer.Impl buffer, ClientPlayerEntity player, int light) {
        float f = player.getAttackAnim(attackMul);
        Hand hand = MoreObjects.firstNonNull(player.swingingArm, Hand.MAIN_HAND);

        float f1 = MathHelper.lerp(attackMul, player.xRotO, player.xRot);
        boolean flag = true;
        boolean flag1 = true;

        if (player.isUsingItem()) {
            ItemStack itemstack = player.getUseItem();

            if (itemstack.getItem() instanceof net.minecraft.item.ShootableItem) {
                flag = player.getUsedItemHand() == Hand.MAIN_HAND;
                flag1 = !flag;
            }

            Hand hand1 = player.getUsedItemHand();

            if (hand1 == Hand.MAIN_HAND) {
                ItemStack itemstack1 = player.getOffhandItem();

                if (itemstack1.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemstack1)) {
                    flag1 = false;
                }
            }
        } else {
            ItemStack itemstack2 = player.getMainHandItem();
            ItemStack itemstack3 = player.getOffhandItem();

            if (itemstack2.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemstack2)) {
                flag1 = false;
            }

            if (itemstack3.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemstack3)) {
                flag = !itemstack2.isEmpty();
                flag1 = !flag;
            }
        }

        if (!ConfigHandler.prevent_sway || !ConfigHandler.mod_enabled) {
            float f3 = MathHelper.lerp(attackMul, player.xBobO, player.xBob);
            float f4 = MathHelper.lerp(attackMul, player.yBobO, player.yBob);

            matrix.mulPose(Vector3f.XP.rotationDegrees((player.getViewXRot(attackMul) - f3) * 0.1F));
            matrix.mulPose(Vector3f.YP.rotationDegrees((player.getViewYRot(attackMul) - f4) * 0.1F));
        }

        if (flag) {
            float f5 = hand == Hand.MAIN_HAND ? f : 0.0F;
            float f2 = 1.0F - MathHelper.lerp(attackMul, this.oMainHandHeight, this.mainHandHeight);

            if (!net.minecraftforge.client.ForgeHooksClient.renderSpecificFirstPersonHand(Hand.MAIN_HAND, matrix, buffer, light, attackMul, f1, f5, f2, this.mainHandItem)) {
                this.renderArmWithItem(player, attackMul, f1, Hand.MAIN_HAND, f5, this.mainHandItem, f2, matrix, buffer, light);
            }
        }

        if (flag1) {
            float f6 = hand == Hand.OFF_HAND ? f : 0.0F;
            float f7 = 1.0F - MathHelper.lerp(attackMul, this.oOffHandHeight, this.offHandHeight);

            if (!net.minecraftforge.client.ForgeHooksClient.renderSpecificFirstPersonHand(Hand.OFF_HAND, matrix, buffer, light, attackMul, f1, f6, f7, this.offHandItem)) {
                this.renderArmWithItem(player, attackMul, f1, Hand.OFF_HAND, f6, this.offHandItem, f7, matrix, buffer, light);
            }

        }

        buffer.endBatch();
    }

    @Shadow
    private void renderArmWithItem(AbstractClientPlayerEntity player, float f1, float f2, Hand hand, float f3, ItemStack stack, float f4, MatrixStack matrix, IRenderTypeBuffer buffer, int light) {}

    /**
     * @author Adrenix
     * @reason Allows manipulation of equipment progress animations.
     */
    @Overwrite
    public void tick() {
        this.oMainHandHeight = this.mainHandHeight;
        this.oOffHandHeight = this.offHandHeight;

        ClientPlayerEntity player = this.minecraft.player;
        assert player != null;

        ItemStack itemStackMain = player.getMainHandItem();
        ItemStack itemStackOff = player.getOffhandItem();

        if (ItemStack.matches(this.mainHandItem, itemStackMain)) {
            this.mainHandItem = itemStackMain;
        }

        if (ItemStack.matches(this.offHandItem, itemStackOff)) {
            this.offHandItem = itemStackOff;
        }

        if (player.isHandsBusy()) {
            this.mainHandHeight = MathHelper.clamp(this.mainHandHeight - 0.4F, 0.0F, 1.0F);
            this.offHandHeight = MathHelper.clamp(this.offHandHeight - 0.4F, 0.0F, 1.0F);
        } else {
            float f = MixinHelper.getCooldownAnimationFloat(player, 1.0F);
            boolean reequipM = MixinHelper.shouldCauseReequipAnimation(this.mainHandItem, itemStackMain, player.inventory.selected);
            boolean reequipO = MixinHelper.shouldCauseReequipAnimation(this.offHandItem, itemStackOff, -1);

            if (!reequipM && this.mainHandItem != itemStackMain)
                this.mainHandItem = itemStackMain;
            if (!reequipO && this.offHandItem != itemStackOff)
                this.offHandItem = itemStackOff;

            this.mainHandHeight += MathHelper.clamp((!reequipM ? f * f * f : 0.0F) - this.mainHandHeight, -0.4F, 0.4F);
            this.offHandHeight += MathHelper.clamp((float)(!reequipO ? 1 : 0) - this.offHandHeight, -0.4F, 0.4F);
        }

        if (this.mainHandHeight < 0.1F) {
            this.mainHandItem = itemStackMain;
        }

        if (this.offHandHeight < 0.1F) {
            this.offHandItem = itemStackOff;
        }
    }
}
