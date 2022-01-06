package mod.adrenix.oseca.mixin;

import mod.adrenix.oseca.config.MixinConfig;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.init.Blocks;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    @Shadow public World world;
    @Shadow public abstract void playSound(SoundEvent soundIn, float volume, float pitch);

    /**
     * Prevents any unique mob stepping sounds from playing.
     * Controlled by the unique step sounds toggle.
     */
    @SuppressWarnings("deprecation")
    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;playStepSound(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/Block;)V"))
    protected void onMoveSound(Entity instance, BlockPos pos, Block blockIn)
    {
        if (MixinConfig.Sound.shouldStepUnique())
        {
            IMixinEntity accessor = (IMixinEntity) instance;
            accessor.invokeStepSound(pos, blockIn);
        }
        else
        {
            if (instance instanceof EntitySpider)
                return;

            SoundType sound = blockIn.getSoundType(this.world.getBlockState(pos), this.world, pos, instance);

            if (this.world.getBlockState(pos.up()).getBlock() == Blocks.SNOW_LAYER)
            {
                sound = Blocks.SNOW_LAYER.getSoundType();
                this.playSound(sound.getStepSound(), sound.getVolume() * 0.15F, sound.getPitch());
            }
            else if (!blockIn.getDefaultState().getMaterial().isLiquid())
                this.playSound(sound.getStepSound(), sound.getVolume() * 0.15F, sound.getPitch());
        }
    }
}
