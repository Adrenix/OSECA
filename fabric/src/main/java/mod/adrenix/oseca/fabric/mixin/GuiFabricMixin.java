package mod.adrenix.oseca.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.adrenix.oseca.MixinInjector;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiFabricMixin
{
    @Shadow @Final private Minecraft minecraft;

    /**
     * Renders the current game version to the top left of the HUD.
     * Controlled by the old version overlay toggle.
     */
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderEffects(Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    protected void onRender(PoseStack matrix, float f, CallbackInfo callback)
    {
        if (MixinInjector.EyeCandy.oldVersionOverlay() && !this.minecraft.options.renderDebug)
        {
            String version = "Minecraft " + SharedConstants.getCurrentVersion().getName();
            this.minecraft.font.drawShadow(matrix, version, 2.0F, 2.0F, 0xFFFFFF);
        }
    }
}
