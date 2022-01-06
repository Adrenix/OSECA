package mod.adrenix.oseca.mixin;

import mod.adrenix.oseca.config.MixinConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderGlobal.class)
public abstract class RenderGlobalMixin
{
    /**
     * Rotates the sun to be oriented in the north/south direction.
     * Controlled by the old sun position toggle.
     */
    @Redirect(method = "renderSky(FI)V", at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V"))
    protected void onRenderSunZ(float angle, float x, float y, float z)
    {
        if (!MixinConfig.Candy.oldSunriseAtNorth())
            GlStateManager.rotate(angle, x, y, z);
    }

    @Redirect(method = "renderSky(FI)V", at = @At(value = "INVOKE", ordinal = 3, target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V"))
    protected void onRenderSunY(float angle, float x, float y, float z)
    {
        if (MixinConfig.Candy.oldSunriseAtNorth())
            GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
        else
            GlStateManager.rotate(angle, x, y, z);
    }
}
