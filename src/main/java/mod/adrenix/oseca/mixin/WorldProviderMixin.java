package mod.adrenix.oseca.mixin;

import mod.adrenix.oseca.config.MixinConfig;
import mod.adrenix.oseca.config.ConfigWatcher;
import mod.adrenix.oseca.duck.IMixinWorldProvider;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldProvider.class)
public abstract class WorldProviderMixin implements IMixinWorldProvider
{
    /**
     * Brings back the old brightness table for old lighting.
     * Controlled by the old lighting toggle.
     */
    @Shadow @Final protected float[] lightBrightnessTable;
    @Shadow protected World world;
    @Unique private final float[] vanillaBrightnessTable = new float[16];
    private static final String reloadBrightnessKey = "WorldProviderMixin#reloadBrightnessTable";

    public void reloadBrightnessTable()
    {
        if (MixinConfig.Candy.oldLighting())
        {
            float mod = 0.05F;
            for (int i = 0; i <= 15; i++)
            {
                float brightness = 1.0F - (float) i / 15.0F;
                this.lightBrightnessTable[i] = ((1.0F - brightness) / (brightness * 3F + 1.0F)) * (1.0F - mod) + mod;
            }
        }
        else
            System.arraycopy(this.vanillaBrightnessTable, 0, this.lightBrightnessTable, 0, 16);
    }

    @Inject(method = "generateLightBrightnessTable", at = @At(value = "TAIL"))
    protected void onGenerateLightBrightnessTable(CallbackInfo callback)
    {
        System.arraycopy(this.lightBrightnessTable, 0, this.vanillaBrightnessTable, 0, 16);
        this.reloadBrightnessTable();
        ConfigWatcher.subscribe(WorldProviderMixin.reloadBrightnessKey, this::reloadBrightnessTable);
    }

    /**
     * Brings back the old cloud height level at y = 108.
     * Controlled by the old cloud height toggle.
     */
    @Inject(method = "getCloudHeight", at = @At(value = "HEAD"), cancellable = true)
    protected void onGetCloudHeight(CallbackInfoReturnable<Float> callback)
    {
        if (MixinConfig.Candy.oldCloudHeight() && this.world.provider.isSurfaceWorld())
            callback.setReturnValue(108F);
    }
}
