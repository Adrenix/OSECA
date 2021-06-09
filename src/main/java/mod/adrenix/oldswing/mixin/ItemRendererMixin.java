package mod.adrenix.oldswing.mixin;

import mod.adrenix.oldswing.MixinHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Objects;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin
{
    @Shadow private float prevEquippedProgressMainHand;
    @Shadow private float equippedProgressMainHand;
    @Shadow private float prevEquippedProgressOffHand;
    @Shadow private float equippedProgressOffHand;
    @Shadow private ItemStack itemStackMainHand;
    @Shadow private ItemStack itemStackOffHand;
    @Shadow @Final private Minecraft mc;

    /**
     * @author Adrenix
     * @reason Needed to change cooldown and reequip animations.
     */
    @Overwrite
    public void updateEquippedItem()
    {
        this.prevEquippedProgressMainHand = this.equippedProgressMainHand;
        this.prevEquippedProgressOffHand = this.equippedProgressOffHand;
        EntityPlayerSP playerSP = this.mc.player;
        ItemStack itemMainHand = playerSP.getHeldItemMainhand();
        ItemStack itemOffHand = playerSP.getHeldItemOffhand();

        if (playerSP.isRowingBoat())
        {
            this.equippedProgressMainHand = MathHelper.clamp(this.equippedProgressMainHand - 0.4F, 0.0F, 1.0F);
            this.equippedProgressOffHand = MathHelper.clamp(this.equippedProgressOffHand - 0.4F, 0.0F, 1.0F);
        }
        else
        {
            float f = MixinHelper.getCooldownAnimationFloat(this.mc.player, 1.0F);

            boolean reequipMain = MixinHelper.shouldCauseReequipAnimation(this.itemStackMainHand, itemMainHand, playerSP.inventory.currentItem);
            boolean reequipOff = MixinHelper.shouldCauseReequipAnimation(this.itemStackOffHand, itemOffHand, -1);

            if (!reequipMain && !Objects.equals(this.itemStackMainHand, itemMainHand))
                this.itemStackMainHand = itemMainHand;
            if (!reequipMain && !Objects.equals(this.itemStackOffHand, itemOffHand))
                this.itemStackOffHand = itemOffHand;

            this.equippedProgressMainHand += MathHelper.clamp((!reequipMain ? f * f * f : 0.0F) - this.equippedProgressMainHand, -0.4F, 0.4F);
            this.equippedProgressOffHand += MathHelper.clamp((float)(!reequipOff ? 1 : 0) - this.equippedProgressOffHand, -0.4F, 0.4F);
        }

        if (this.equippedProgressMainHand < 0.1F)
            this.itemStackMainHand = itemMainHand;

        if (this.equippedProgressOffHand < 0.1F)
            this.itemStackOffHand = itemOffHand;
    }

    /**
     * @author Adrenix
     * @reason Needed to prevent the arm sway animation.
     */
    @Overwrite
    public void rotateArm(float adjust)
    {
        if (!MixinHelper.shouldArmSway())
            return;

        EntityPlayerSP playerSP = this.mc.player;
        float f = playerSP.prevRenderArmPitch + (playerSP.renderArmPitch - playerSP.prevRenderArmPitch) * adjust;
        float f1 = playerSP.prevRenderArmYaw + (playerSP.renderArmYaw - playerSP.prevRenderArmYaw) * adjust;
        GlStateManager.rotate((playerSP.rotationPitch - f) * 0.1F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((playerSP.rotationYaw - f1) * 0.1F, 0.0F, 1.0F, 0.0F);
    }
}
