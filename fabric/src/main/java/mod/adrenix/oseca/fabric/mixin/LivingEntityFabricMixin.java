package mod.adrenix.oseca.fabric.mixin;

import mod.adrenix.oseca.MixinInjector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityFabricMixin
{
    /**
     * Separates items from a clumped item entity into multiple item entities when a mob is killed.
     * Controlled by the item merging toggle.
     */
    @ModifyArg(method = "dropFromLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V"))
    protected Consumer<ItemStack> onDropFromLootTable(Consumer<ItemStack> consumer)
    {
        return MixinInjector.Inject.explodeStack(consumer);
    }
}
