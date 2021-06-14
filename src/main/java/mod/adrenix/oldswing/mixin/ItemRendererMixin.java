package mod.adrenix.oldswing.mixin;

import mod.adrenix.oldswing.MixinHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin
{
    @Shadow private float equippedProgressMainHand;
    @Shadow private float equippedProgressOffHand;
    @Shadow private float prevEquippedProgressMainHand;
    @Shadow private float prevEquippedProgressOffHand;
    @Shadow private ItemStack itemStackMainHand;
    @Shadow private ItemStack itemStackOffHand;
    @Shadow @Final private Minecraft mc;

    @Inject(method = "updateEquippedItem", at = @At(value = "HEAD"), cancellable = true)
    protected void onUpdateEquippedItem(CallbackInfo callback)
    {
        this.prevEquippedProgressMainHand = this.equippedProgressMainHand;
        this.prevEquippedProgressOffHand = this.equippedProgressOffHand;

        EntityPlayerSP player = this.mc.player;
        ItemStack itemMainHand = player.getHeldItemMainhand();
        ItemStack itemOffHand = player.getHeldItemOffhand();

        if (player.isRowingBoat())
        {
            this.equippedProgressMainHand = MathHelper.clamp(this.equippedProgressMainHand - 0.4F, 0.0F, 1.0F);
            this.equippedProgressOffHand = MathHelper.clamp(this.equippedProgressOffHand - 0.4F, 0.0F, 1.0F);
        }
        else
        {
            float scale = MixinHelper.getCooldownAnimationFloat(player, 1.0F);
            boolean reequipMain = MixinHelper.shouldCauseReequipAnimation(this.itemStackMainHand, itemMainHand, player.inventory.currentItem);
            boolean reequipOff = MixinHelper.shouldCauseReequipAnimation(this.itemStackOffHand, itemOffHand, -1);

            if (!reequipMain && !Objects.equals(this.itemStackMainHand, itemMainHand))
                this.itemStackMainHand = itemMainHand;
            if (!reequipMain && !Objects.equals(this.itemStackOffHand, itemOffHand))
                this.itemStackOffHand = itemOffHand;

            this.equippedProgressMainHand += MathHelper.clamp((!reequipMain ? scale * scale * scale : 0.0F) - this.equippedProgressMainHand, -0.4F, 0.4F);
            this.equippedProgressOffHand += MathHelper.clamp((float)(!reequipOff ? 1 : 0) - this.equippedProgressOffHand, -0.4F, 0.4F);
        }

        if (this.equippedProgressMainHand < 0.1F)
            this.itemStackMainHand = itemMainHand;

        if (this.equippedProgressOffHand < 0.1F)
            this.itemStackOffHand = itemOffHand;

        callback.cancel();
    }

    @Inject(method = "rotateArm", at = @At(value = "HEAD"), cancellable = true)
    protected void onRotateArm(float adjust, CallbackInfo callback)
    {
        if (MixinHelper.shouldArmSway())
            return;
        callback.cancel();
    }
}
