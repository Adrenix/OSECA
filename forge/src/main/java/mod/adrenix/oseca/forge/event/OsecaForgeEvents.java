package mod.adrenix.oseca.forge.event;

import mod.adrenix.oseca.MixinInjector;
import mod.adrenix.oseca.Oseca;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Oseca.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public abstract class OsecaForgeEvents
{
    /**
     * Renders the current game version to the top left of the HUD.
     * Controlled by the old version overlay toggle.
     */
    @SubscribeEvent
    public static void versionOverlay(RenderGameOverlayEvent.PreLayer event)
    {
        if (MixinInjector.EyeCandy.oldVersionOverlay())
        {
            String version = "Minecraft " + SharedConstants.getCurrentVersion().getName();
            Minecraft.getInstance().font.drawShadow(event.getMatrixStack(), version, 2.0F, 2.0F, 0xFFFFFF);
        }
    }
}
