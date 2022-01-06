package mod.adrenix.oseca.mixin;

import mod.adrenix.oseca.config.MixinConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin
{
    @Shadow @Final private Minecraft mc;
    @Shadow @Final private DynamicTexture lightmapTexture;
    @Shadow @Final private int[] lightmapColors;
    @Shadow private boolean lightmapUpdateNeeded;
    @Shadow private float farPlaneDistance;
    @Shadow private float fogColorRed;
    @Shadow private float fogColorGreen;
    @Shadow private float fogColorBlue;
    @Shadow protected abstract float getNightVisionBrightness(EntityLivingBase entityLivingBase, float partialTicks);

    /**
     * Disables the light flickering from light emitting sources.
     * Controlled by the old light flicker toggle.
     */
    @Inject(method = "updateTorchFlicker", at = @At(value = "HEAD"), cancellable = true)
    protected void onUpdateTorchFlicker(CallbackInfo callback)
    {
        if (MixinConfig.Animation.shouldLightFlicker())
            return;

        this.lightmapUpdateNeeded = true;
        callback.cancel();
    }

    /**
     * Removes fog and cloud color changes based on the surrounding light.
     * Fixes bug: MC-31681
     * Can be toggled if desired by player.
     */
    @Redirect(method = "updateRenderer", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/settings/GameSettings;renderDistanceChunks:I"))
    protected int onGetRenderDistance(GameSettings settings)
    {
        if (MixinConfig.Candy.fixLightDrivenFog())
            return 32;
        return settings.renderDistanceChunks;
    }

    /**
     * Brings back the old lighting colors for the lightmap.
     * Controlled by the old lighting toggle.
     */
    @Inject(method = "updateLightmap", at = @At(value = "HEAD"), cancellable = true)
    protected void oldLighting(float partialTicks, CallbackInfo callback)
    {
        World world = this.mc.world;

        if (!MixinConfig.Candy.oldLighting() || !this.lightmapUpdateNeeded || world == null)
            return;

        this.mc.profiler.startSection("lightTex");

        int lightTintRed = 255;
        int lightTintGreen = 255;
        int lightTintBlue = 255;
        float[] lightBrightnessTable = world.provider.getLightBrightnessTable();

        int skylightSubtracted = world.calculateSkylightSubtracted(1.0F);
        int lightmapIndex = 0;

        for (int i = 0; i < 16; i++)
        {
            for (int j = 0; j < 16; j++)
            {
                float lightBrightness = lightBrightnessTable[j];
                int diffSkylight = i - skylightSubtracted;

                if (diffSkylight < 0)
                    diffSkylight = 0;

                float fromSkylight = lightBrightnessTable[diffSkylight];
                if (world.provider.getDimension() == 1)
                    fromSkylight = 0.22F + fromSkylight * 0.75F;

                if (this.mc.player.isPotionActive(MobEffects.NIGHT_VISION))
                {
                    float brightness = this.getNightVisionBrightness(this.mc.player, partialTicks);
                    float shiftBrightness = brightness * 0.7F;
                    float adjustBlockColor = ((1.0F - lightBrightness - 0.5F) * (shiftBrightness * lightBrightness)) + (0.5F * brightness);
                    float skyAdjust = ((1.0F - fromSkylight - 0.5F) * (shiftBrightness * fromSkylight)) + (0.5F * brightness);

                    lightBrightness += adjustBlockColor;
                    fromSkylight += skyAdjust;
                }

                int lightMultiplier = (int) (lightBrightness * 255.0F);
                int skyMultiplier = (int) (fromSkylight * 255.0F);
                float r = 1.0F - (float) lightTintRed / 255.0F;
                float g = 1.0F - (float) lightTintGreen / 255.0F;
                float b = 1.0F - (float) lightTintBlue / 255.0F;
                float brightness = (float) (15 - i) / 15.0F;

                r *= brightness;
                g *= brightness;
                b *= brightness;

                r = 1.0F - r;
                g = 1.0F - g;
                b = 1.0F - b;

                lightMultiplier = (int) ((float) lightMultiplier * (this.mc.gameSettings.gammaSetting + 1.0F));
                if (lightMultiplier > 255)
                    lightMultiplier = 255;

                skyMultiplier = (int) ((float) skyMultiplier * (this.mc.gameSettings.gammaSetting + 1.0F));
                if (skyMultiplier > 255)
                    skyMultiplier = 255;

                if (lightBrightness > fromSkylight)
                    this.lightmapColors[lightmapIndex] = 255 << 24 | (int) ((float) lightMultiplier * r) << 16 | (int) ((float) lightMultiplier * g) << 8 | (int) ((float) lightMultiplier * b);
                else
                    this.lightmapColors[lightmapIndex] = 255 << 24 | (int) ((float) skyMultiplier * r) << 16 | (int) ((float) skyMultiplier * g) << 8 | (int) ((float) skyMultiplier * b);

                lightmapIndex++;
            }
        }

        this.lightmapTexture.updateDynamicTexture();
        this.lightmapUpdateNeeded = false;
        this.mc.profiler.endSection();
        callback.cancel();
    }

    /**
     * Changes the way the sky fog is rendered.
     * The sky fog starts at 0 and goes to 80% of the far plane distance.
     * Controlled by the old fog toggle.
     */
    @Inject(method = "setupFog", at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 2, target = "Lnet/minecraft/client/renderer/GlStateManager;setFogEnd(F)V"))
    protected void onSkyFog(int startCords, float partialTicks, CallbackInfo callback)
    {
        if (MixinConfig.Candy.oldFog())
        {
            GlStateManager.setFogStart(0.0F);
            GlStateManager.setFogEnd(0.8F * (this.farPlaneDistance + (this.farPlaneDistance * 1.46F)));
        }
    }

    /**
     * Renders the fog further away from the player as it was done in the old days.
     * Controlled by the old fog toggle.
     */
    @Inject(method = "setupFog", at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 3, target = "Lnet/minecraft/client/renderer/GlStateManager;setFogEnd(F)V"))
    protected void onTerrainFog(int startCords, float partialTicks, CallbackInfo callback)
    {
        if (MixinConfig.Candy.oldFog())
        {
            float distance = this.farPlaneDistance + (this.farPlaneDistance * 0.24F);
            GlStateManager.setFogStart(0.25F * distance);
            GlStateManager.setFogEnd(distance);
        }
    }

    /**
     * The old fog rendering in the nether starts where the player stands and extends off into the distance.
     * Controlled by the old nether fog toggle.
     */
    @ModifyArg(method = "setupFog", at = @At(value = "INVOKE", ordinal = 4, target = "Lnet/minecraft/client/renderer/GlStateManager;setFogStart(F)V"))
    protected float onStartNetherFog(float vanilla)
    {
        if (MixinConfig.Candy.oldNetherFog() && this.mc.world.provider.isNether())
            return 0.0F;
        return vanilla;
    }

    @Redirect(method = "setupFog", at = @At(value = "INVOKE", ordinal = 4, target = "Lnet/minecraft/client/renderer/GlStateManager;setFogEnd(F)V"))
    protected void onEndNetherFog(float amount)
    {
        if (!MixinConfig.Candy.oldNetherFog() || !this.mc.world.provider.isNether())
            GlStateManager.setFogEnd(Math.min(this.farPlaneDistance, 192.0F) * 0.5F);
    }

    /**
     * Disables the sunrise/sunset colors from influencing the fog color.
     * Controlled by the old fog toggle.
     */
    @Redirect(method = "updateFogColor", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 1, target = "Lnet/minecraft/client/renderer/EntityRenderer;fogColorRed:F"))
    protected void onUpdateRedFogColor(EntityRenderer instance, float vanilla)
    {
        if (!MixinConfig.Candy.oldFog())
            this.fogColorRed = vanilla;
    }

    @Redirect(method = "updateFogColor", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 1, target = "Lnet/minecraft/client/renderer/EntityRenderer;fogColorGreen:F"))
    protected void onUpdateGreenFogColor(EntityRenderer instance, float vanilla)
    {
        if (!MixinConfig.Candy.oldFog())
            this.fogColorGreen = vanilla;
    }

    @Redirect(method = "updateFogColor", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, ordinal = 1, target = "Lnet/minecraft/client/renderer/EntityRenderer;fogColorBlue:F"))
    protected void onUpdateBlueFogColor(EntityRenderer instance, float vanilla)
    {
        if (!MixinConfig.Candy.oldFog())
            this.fogColorBlue = vanilla;
    }

    /**
     * Changes the sunrise/sunset position to be north/south instead of east/west.
     * Controlled by the old sunrise position toggle.
     */
    @ModifyArg(method = "updateFogColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;dotProduct(Lnet/minecraft/util/math/Vec3d;)D"))
    protected Vec3d onGetSunPosition(Vec3d vec)
    {
        if (MixinConfig.Candy.oldSunriseAtNorth())
            return new Vec3d(0.0D, 0.0D, vec.x);
        return vec;
    }
}
