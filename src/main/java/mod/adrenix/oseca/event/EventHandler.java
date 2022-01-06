package mod.adrenix.oseca.event;

import mod.adrenix.oseca.OSECA;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = OSECA.MODID, value = Side.CLIENT)
public abstract class EventHandler
{
    /* Candy Event Subscribers */

    /**
     * Separates items from a clumped item entity into multiple item entities when a mob is killed.
     * Controlled by the item merging toggle.
     */
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) { CandyEvents.livingDrops(event); }

    /**
     * Renders the current game version to the top left of the HUD.
     * Controlled by the old version overlay toggle.
     */
    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) { CandyEvents.renderOverlay(event); }

    /**
     * Removes the current tooltip, so it can be redrawn with the old style.
     * Controlled by the old tooltips toggle.
     */
    @SubscribeEvent
    public static void onPreTooltip(RenderTooltipEvent.Pre event) { CandyEvents.removeTooltip(event); }

    /**
     * Redraws the removed tooltip with the old style boxes.
     * Controlled by the old tooltips toggle.
     */
    @SubscribeEvent
    public static void onPostTooltip(RenderTooltipEvent.PostBackground event) { CandyEvents.overrideTooltip(); }

    /**
     * Redirects the main menu to open the mod's old main menu.
     * Controlled by the old main menu toggle.
     */
    @SubscribeEvent
    public static void onOpenGui(GuiOpenEvent event) { CandyEvents.openClassicMainMenu(event); }
}
