package mod.adrenix.oseca.mixin;

import mod.adrenix.oseca.MixinInjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    /**
     * Prevents the hand swing animation when dropping an item.
     * Controlled the swing drop toggle.
     */
    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;swing(Lnet/minecraft/world/InteractionHand;)V"))
    protected void itemDroppingProxy(LocalPlayer player, InteractionHand hand)
    {
        if (MixinInjector.Animation.shouldSwingDrop())
            player.swing(InteractionHand.MAIN_HAND);
    }
}