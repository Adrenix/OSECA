package mod.adrenix.oseca.mixin;

import mod.adrenix.oseca.MixinInjector;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin
{
    @Shadow protected boolean isHovered;
    @Shadow public abstract boolean isActive();

    /**
     * Renders old school style buttons by rendering yellow text on hover.
     */
    @ModifyArg(method = "renderButton", index = 5, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractWidget;drawCenteredString(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"))
    protected int onRenderButton(int current)
    {
        if (!MixinInjector.EyeCandy.oldButtonHover())
            return current;
        if (this.isHovered && this.isActive())
            return 16777120;
        return current;
    }

    /**
     * Redirects the vanilla widget textures to the mod's for a full old school button hover effect.
     */
    @ModifyArg(method = "renderButton", index = 1, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V"))
    protected ResourceLocation onGetTexture(int i, ResourceLocation vanilla)
    {
        if (MixinInjector.EyeCandy.oldButtonHover())
            return MixinInjector.EyeCandy.WIDGETS_LOCATION;
        return vanilla;
    }
}
