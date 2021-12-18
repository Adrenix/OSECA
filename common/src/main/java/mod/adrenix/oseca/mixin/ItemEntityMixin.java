package mod.adrenix.oseca.mixin;

import mod.adrenix.oseca.MixinInjector;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin
{
    /**
     * Splits up the item stacks dropped by entities.
     * Controlled by the old item merging toggle.
     */
    @Inject(method = "isMergable", at = @At(value = "HEAD"), cancellable = true)
    protected void onIsMergable(CallbackInfoReturnable<Boolean> callback)
    {
        if (MixinInjector.EyeCandy.oldItemMerging())
            callback.setReturnValue(false);
    }
}
