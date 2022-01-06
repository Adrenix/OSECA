package mod.adrenix.oseca.mixin;

import mod.adrenix.oseca.config.MixinConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin
{
    @Shadow @Final private Minecraft mc;
    @Shadow private ItemStack itemStackOffHand;
    @Shadow private ItemStack itemStackMainHand;
    @Unique private int slotTracker = 0;

    /**
     * Modifies rotation and translation arguments to simulate old arm rendering.
     * Controlled by old arm rendering toggle.
     */
    @Inject(method = "renderItemSide", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V"))
    protected void onTranslateArm(EntityLivingBase entity, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded, CallbackInfo callback)
    {
        if (MixinConfig.Candy.oldItemHolding())
        {
            GlStateManager.rotate(5F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.01F, -0.01F, -0.015F);
        }
    }

    /**
     * Blocks the rotation in the hand renderer when arm sway is disabled.
     * Controlled by arm sway animation toggle.
     */
    @Redirect(method = "rotateArm", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V"))
    protected void onRotateArm(float angle, float x, float y, float z)
    {
        if (MixinConfig.Animation.shouldArmSway())
            GlStateManager.rotate(angle, x, y, z);
    }

    /**
     * Forces the attack strength to be 1.0F when the cooldown animation is disabled.
     * Controlled by the cooldown animation toggle.
     */
    @Redirect(method = "updateEquippedItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;getCooledAttackStrength(F)F"))
    protected float onGetCooldown(EntityPlayerSP player, float adjustTicks)
    {
        if (MixinConfig.Animation.shouldCooldown())
            return player.getCooledAttackStrength(adjustTicks);
        return 1.0F;
    }

    /**
     * Forces the item matching to return false on the main hand, so we can track what the last held item was.
     * This prevents reequip animation issues when going from an item in the main hand to air.
     * Controlled by reequipping toggle.
     */
    @Redirect(method = "updateEquippedItem", at = @At(value = "INVOKE", ordinal = 0, target = "Ljava/util/Objects;equals(Ljava/lang/Object;Ljava/lang/Object;)Z"))
    protected boolean onMainItemStackUpdate(Object from, Object to)
    {
        if (MixinConfig.Animation.shouldReequip())
            return Objects.equals(from, to);
        return true;
    }

    /**
     * Keeps track of when the hotbar slot changes. Needed for proper reequip animation logic.
     * Not controlled by any toggle since this unique field tracker needs constantly updated.
     */
    @Inject(method = "updateEquippedItem", at = @At(value = "FIELD", ordinal = 1, opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/renderer/ItemRenderer;itemStackMainHand:Lnet/minecraft/item/ItemStack;"))
    protected void onFinishReequip(CallbackInfo callback)
    {
        slotTracker = this.mc.player.inventory.currentItem;
    }

    /**
     * Simulate old reequip logic and animation.
     * Controlled by reequip animation toggle.
     *
     * Comes with photosensitivity mode check by completely disabling any hand movement when placing or interacting.
     * Only checks for global photosensitivity since this will break reequip animations if checking by item.
     */
    @Redirect(method = "updateEquippedItem", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraftforge/client/ForgeHooksClient;shouldCauseReequipAnimation(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;I)Z"))
    protected boolean onReequipMain(ItemStack from, ItemStack to, int slot)
    {
        if (MixinConfig.Swing.isSpeedPhotosensitive())
        {
            this.itemStackMainHand = to;
            return false;
        }

        boolean forgeReequip = ForgeHooksClient.shouldCauseReequipAnimation(from, to, slot);
        if (MixinConfig.Animation.shouldReequip() || slot == -1)
            return forgeReequip;

        Item cachedFromItem = ((IMixinItemStack)(Object) from).getCachedItem();
        Item cachedToItem = ((IMixinItemStack)(Object) to).getCachedItem();

        if (from.isEmpty() && cachedFromItem != null && cachedFromItem != Items.AIR)
        {
            this.itemStackMainHand = cachedFromItem.getDefaultInstance();
            return true;
        }

        if (slot != slotTracker && !from.isEmpty() && !to.isEmpty())
            return true;
        else if (cachedFromItem == cachedToItem)
        {
            if (slot == slotTracker)
                this.itemStackMainHand = to;
            return false;
        }

        return forgeReequip;
    }

    /**
     * Prevents the offhand from reequipping when only the stack size changes.
     * Controlled by reequipping toggle.
     *
     * Comes with photosensitivity mode check by completely disabling any hand movement when placing or interacting.
     * Only checks for global photosensitivity since this will break reequip animations if checking by item.
     */
    @Redirect(method = "updateEquippedItem", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraftforge/client/ForgeHooksClient;shouldCauseReequipAnimation(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;I)Z"))
    protected boolean onReequipOff(ItemStack from, ItemStack to, int slot)
    {
        if (MixinConfig.Swing.isSpeedPhotosensitive())
        {
            this.itemStackOffHand = to;
            return false;
        }

        boolean forgeReequip = ForgeHooksClient.shouldCauseReequipAnimation(from, to, slot);

        if (MixinConfig.Animation.shouldReequip())
            return forgeReequip;

        ItemStack offStack = this.mc.player.getHeldItemOffhand();

        if (this.itemStackOffHand.isItemEqual(offStack) && this.itemStackOffHand.getCount() != offStack.getCount())
            return false;
        return forgeReequip;
    }

    /**
     * Enhances photosensitivity mode by completely disabling any hand movement when placing or interacting.
     * Only checks for global photosensitivity since this will break reequip animations if checking by item.
     */
    @ModifyArg(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", index = 1, at = @At(value = "INVOKE", ordinal = 4, target = "Lnet/minecraft/client/renderer/ItemRenderer;transformSideFirstPerson(Lnet/minecraft/util/EnumHandSide;F)V"))
    protected float onTransformFirstPerson(float yDiff)
    {
        if (MixinConfig.Swing.isSpeedPhotosensitive())
            return 0.0F;
        return yDiff;
    }
}
