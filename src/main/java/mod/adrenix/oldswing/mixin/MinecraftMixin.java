package mod.adrenix.oldswing.mixin;

import mod.adrenix.oldswing.MixinInjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    @Shadow @Nullable public ClientPlayerEntity player;

    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/player/ClientPlayerEntity;swing(Lnet/minecraft/util/Hand;)V"))
    protected void itemDroppingProxy(ClientPlayerEntity player, Hand hand)
    {
        if (MixinInjector.shouldSwingDrop())
        {
            if (this.player != null && !this.player.isSpectator() && this.player.drop(Screen.hasControlDown()))
                this.player.swing(Hand.MAIN_HAND);
        }
    }
}
