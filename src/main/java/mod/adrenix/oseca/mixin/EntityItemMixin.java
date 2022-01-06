package mod.adrenix.oseca.mixin;

import mod.adrenix.oseca.config.MixinConfig;
import net.minecraft.entity.item.EntityItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityItem.class)
public abstract class EntityItemMixin
{
    /**
     * Disables the ability for items floating on the ground to merge.
     * Controlled by the old item merging toggle.
     */
    @Inject(method = "combineItems", at = @At(value = "HEAD"), cancellable = true)
    protected void onCombineItems(EntityItem item, CallbackInfoReturnable<Boolean> callback)
    {
        if (MixinConfig.Candy.oldItemMerging())
            callback.setReturnValue(false);
    }
}
