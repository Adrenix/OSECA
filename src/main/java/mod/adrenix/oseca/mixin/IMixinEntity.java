package mod.adrenix.oseca.mixin;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface IMixinEntity
{
    @Invoker("playStepSound") void invokeStepSound(BlockPos pos, Block blockIn);
}
