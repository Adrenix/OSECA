package mod.adrenix.oseca.event;

import mod.adrenix.oseca.config.MixinConfig;
import mod.adrenix.oseca.gui.ClassicMainMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class CandyEvents
{
    // Item Merging
    public static void livingDrops(LivingDropsEvent event)
    {
        if (!MixinConfig.Candy.oldItemMerging())
            return;

        for (EntityItem entity : event.getDrops())
        {
            ItemStack item = entity.getItem();
            int count = item.getCount();
            if (count > 1)
            {
                item.setCount(1);
                for (int i = 1; i < count; i++)
                {
                    EntityItem popped = new EntityItem(entity.getEntityWorld(), entity.posX, entity.posY, entity.posZ, item);
                    popped.getItem().setCount(1);
                    popped.setDefaultPickupDelay();

                    entity.getEntityWorld().spawnEntity(popped);
                }
            }
        }
    }

    // Override Main Menu Screen
    public static void openClassicMainMenu(GuiOpenEvent event)
    {
        if (MixinConfig.Candy.oldMainMenu() && event.getGui() != null && event.getGui().getClass() == GuiMainMenu.class && !FMLClientHandler.instance().hasOptifine())
            event.setGui(new ClassicMainMenu());
        else if (!MixinConfig.Candy.oldMainMenu() && event.getGui() != null && event.getGui().getClass() == ClassicMainMenu.class)
            event.setGui(new GuiMainMenu());
    }

    // Overlay Minecraft Version
    public static void renderOverlay(RenderGameOverlayEvent.Post event)
    {
        if (!MixinConfig.Candy.oldVersionOverlay() || event.getType() != RenderGameOverlayEvent.ElementType.TEXT)
            return;

        String version = "Minecraft 1.12.2";
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(version, 2.0F, 2.0F, 0xFFFFFF);
    }

    /* Old Tooltips */

    public static int mouseX;
    public static int mouseY;
    public static int screenWidth;
    public static int screenHeight;
    public static int maxTextWidth;
    public static List<String> textLines;
    public static FontRenderer font;

    public static void removeTooltip(RenderTooltipEvent.Pre event)
    {
        if (!MixinConfig.Candy.oldTooltips())
            return;

        font = event.getFontRenderer();
        mouseX = event.getX();
        mouseY = event.getY();
        screenWidth = event.getScreenWidth();
        screenHeight = event.getScreenHeight();
        textLines = event.getLines();
        maxTextWidth = event.getMaxWidth();
        event.setX(-9999);
        event.setY(-9999);
    }

    public static void overrideTooltip()
    {
        if (!MixinConfig.Candy.oldTooltips())
            return;

        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        int tooltipTextWidth = 0;

        for (String textLine : textLines)
        {
            int textLineWidth = font.getStringWidth(textLine);
            if (textLineWidth > tooltipTextWidth)
                tooltipTextWidth = textLineWidth;
        }

        boolean needsWrap = false;

        int titleLinesCount = 1;
        int tooltipX = mouseX + 12;
        if (tooltipX + tooltipTextWidth + 4 > screenWidth)
        {
            tooltipX = mouseX - 16 - tooltipTextWidth;
            if (tooltipX < 4)
            {
                if (mouseX > screenWidth / 2)
                    tooltipTextWidth = mouseX - 12 - 8;
                else
                    tooltipTextWidth = screenWidth - 16 - mouseX;
                needsWrap = true;
            }
        }

        if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth)
        {
            tooltipTextWidth = maxTextWidth;
            needsWrap = true;
        }

        if (needsWrap)
        {
            int wrappedTooltipWidth = 0;
            List<String> wrappedTextLines = new ArrayList<>();

            for (int i = 0; i < textLines.size(); i++)
            {
                String textLine = textLines.get(i);
                List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);

                if (i == 0)
                    titleLinesCount = wrappedLine.size();

                for (String line : wrappedLine)
                {
                    int lineWidth = font.getStringWidth(line);
                    if (lineWidth > wrappedTooltipWidth)
                        wrappedTooltipWidth = lineWidth;
                    wrappedTextLines.add(line);
                }
            }

            tooltipTextWidth = wrappedTooltipWidth;
            textLines = wrappedTextLines;

            if (mouseX > screenWidth / 2)
                tooltipX = mouseX - 16 - tooltipTextWidth;
            else
                tooltipX = mouseX + 12;
        }

        int tooltipY = mouseY - 12;
        int tooltipHeight = 8;

        if (textLines.size() > 1)
        {
            tooltipHeight += (textLines.size() - 1) * 10;
            if (textLines.size() > titleLinesCount)
                tooltipHeight += 2;
        }

        if (tooltipY < 4)
            tooltipY = 4;
        else if (tooltipY + tooltipHeight + 4 > screenHeight)
            tooltipY = screenHeight - tooltipHeight - 4;

        GuiUtils.drawGradientRect(300, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, 0xc0000000, 0xc0000000);

        for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber)
        {
            String line = textLines.get(lineNumber);
            font.drawStringWithShadow(line, (float)tooltipX, (float)tooltipY, -1);

            if (lineNumber + 1 == titleLinesCount)
                tooltipY += 2;

            tooltipY += 10;
        }

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableRescaleNormal();
    }
}
