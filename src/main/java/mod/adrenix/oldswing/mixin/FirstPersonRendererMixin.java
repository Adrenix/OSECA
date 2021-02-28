package mod.adrenix.oldswing.mixin;

import mod.adrenix.oldswing.config.TransformerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FirstPersonRenderer.class)
public abstract class FirstPersonRendererMixin {
    @Shadow private float prevEquippedProgressMainHand;
    @Shadow private float equippedProgressMainHand;
    @Shadow private float prevEquippedProgressOffHand;
    @Shadow private float equippedProgressOffHand;
    @Shadow private ItemStack itemStackMainHand;
    @Shadow private ItemStack itemStackOffHand;
    @Shadow @Final private Minecraft mc;

    /**
     * @author Adrenix
     * @reason Allows for manipulation of equipment progress animations.
     */
    @Overwrite
    public void tick() {
        this.prevEquippedProgressMainHand = this.equippedProgressMainHand;
        this.prevEquippedProgressOffHand = this.equippedProgressOffHand;

        ClientPlayerEntity player = this.mc.player;
        ItemStack itemStackMain = player.getHeldItemMainhand();
        ItemStack itemStackOff = player.getHeldItemOffhand();

        if (ItemStack.areItemStacksEqual(this.itemStackMainHand, itemStackMain)) {
            this.itemStackMainHand = itemStackMain;
        }

        if (ItemStack.areItemStacksEqual(this.itemStackOffHand, itemStackOff)) {
            this.itemStackOffHand = itemStackOff;
        }

        if (player.isRowingBoat()) {
            this.equippedProgressMainHand = MathHelper.clamp(this.equippedProgressMainHand - 0.4F, 0.0F, 1.0F);
            this.equippedProgressOffHand = MathHelper.clamp(this.equippedProgressOffHand - 0.4F, 0.0F, 1.0F);
        } else {
            float f = TransformerHelper.getCooldownAnimationFloat(player, 1.0F);
            boolean reequipM = TransformerHelper.shouldCauseReequipAnimation(this.itemStackMainHand, itemStackMain, player.inventory.currentItem);
            boolean reequipO = TransformerHelper.shouldCauseReequipAnimation(this.itemStackOffHand, itemStackOff, -1);

            if (!reequipM && this.itemStackMainHand != itemStackMain)
                this.itemStackMainHand = itemStackMain;
            if (!reequipO && this.itemStackOffHand != itemStackOff)
                this.itemStackOffHand = itemStackOff;

            this.equippedProgressMainHand += MathHelper.clamp((!reequipM ? f * f * f : 0.0F) - this.equippedProgressMainHand, -0.4F, 0.4F);
            this.equippedProgressOffHand += MathHelper.clamp((float)(!reequipO ? 1 : 0) - this.equippedProgressOffHand, -0.4F, 0.4F);
        }

        if (this.equippedProgressMainHand < 0.1F) {
            this.itemStackMainHand = itemStackMain;
        }

        if (this.equippedProgressOffHand < 0.1F) {
            this.itemStackOffHand = itemStackOff;
        }
    }
}
