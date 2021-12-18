package mod.adrenix.oseca.mixin;

import mod.adrenix.oseca.MixinInjector;
import mod.adrenix.oseca.injector.IReequipSlot;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements IReequipSlot
{
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level)
    {
        super(entityType, level);
    }

    /* Reequip and Slot Tracking Injections */

    @Shadow private ItemStack lastItemInMainHand;
    public int lastSlot = -1;
    public boolean reequip = false;

    public void setLastSlot(int slot) { this.lastSlot = slot; }
    public void setReequip(boolean state) { this.reequip = state; }

    public int getLastSlot() { return lastSlot; }
    public boolean getReequip() { return reequip; }
    public ItemStack getLastItem() { return this.lastItemInMainHand; }


    /* Mixin Injections */

    /**
     * Disables on sounds when attacking.
     * Controlled by the sound attack toggle.
     */
    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    protected void onAttack(Level level, Player player, double x, double y, double z, SoundEvent event, SoundSource sound, float volume, float pitch)
    {
        if (MixinInjector.Sound.attack())
            level.playSound(null, x, y, z, event, sound, volume, pitch);
    }

    /**
     * Disables the sweep attack particles that appears when attacking multiple entities at once.
     * Controlled by the sweep attack toggle.
     */
    @Inject(method = "sweepAttack", at = @At(value = "HEAD"), cancellable = true)
    protected void onSweepAttack(CallbackInfo callback)
    {
        if (!MixinInjector.Animation.shouldSweepAttack() && EnchantmentHelper.getSweepingDamageRatio((Player) (Object) this) <= 0.0F)
            callback.cancel();
    }

    /**
     * Updates the camera pitching when the player moves up and down.
     */
    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setSpeed(F)V"))
    protected void onAiStep(CallbackInfo callback)
    {
        double deltaY = this.getDeltaMovement().y;
        float rotation = (float) (Math.atan(-deltaY * 0.20000000298023224D) * 15.0D);
        float current = this.getCameraPitch();

        // Fixes weird bug that occurs when standing on a slime block.
        boolean isGrounded = deltaY < -0.07 && deltaY > -0.08;
        if (isGrounded || this.onGround || this.getHealth() <= 0.0F)
            rotation = 0.0F;

        this.setCameraPitch(current + (rotation - current) * 0.8F);
    }

    /**
     * Disables the bobbing animation when the player provides input for movement but cannot move further.
     * Controlled by the bob on collision toggle.
     */
    @ModifyArg(method = "aiStep", index = 1, at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(FF)F"))
    protected float onAiBobbing(float min, float current)
    {
        if (MixinInjector.Animation.shouldCollideBob())
            return current;
        else if (this.walkDist == this.walkDistO)
            return 0.0F;
        return current;
    }

    /**
     * Prevents the swinging animation when dropping an item from the hand or within an inventory screen.
     * Controlled by the swing drop toggle.
     */
    @Redirect(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;swing(Lnet/minecraft/world/InteractionHand;)V"))
    protected void itemDroppingProxy(Player player, InteractionHand hand)
    {
        if (MixinInjector.Animation.shouldSwingDrop())
            player.swing(InteractionHand.MAIN_HAND);
    }
}