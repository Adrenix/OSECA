package mod.adrenix.oldswing.events;

import mod.adrenix.oldswing.MixinInjector;
import mod.adrenix.oldswing.OldSwing;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = OldSwing.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RenderTooltip
{
    public static int tooltipX;
    public static int tooltipY;
    public static int mouseX;
    public static int mouseY;
    public static int screenWidth;
    public static int screenHeight;
    public static int maxTextWidth;
    public static int titleLinesCount;
    public static List<? extends ITextProperties> textLines;
    public static FontRenderer font;
    public static RenderTooltipEvent.Pre cache;

    @SubscribeEvent
    public static void overrideWidth(RenderTooltipEvent.Pre event)
    {
        if (MixinInjector.oldTooltips())
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
        cache = event;
    }

    @SubscribeEvent
    public static void overrideColors(RenderTooltipEvent.Color event)
    {
        if (MixinInjector.oldTooltips())
            return;

        event.setBackground(0xc0000000);
        event.setBorderStart(0x0ff0000);
        event.setBorderEnd(0x0ff0000);
    }

    @SubscribeEvent
    public static void overrideBox(RenderTooltipEvent.PostBackground event)
    {
        if (MixinInjector.oldTooltips())
            return;

        int tooltipTextWidth = 0;

        for (ITextProperties textLine : textLines)
        {
            int textLineWidth = font.width(textLine);
            if (textLineWidth > tooltipTextWidth)
                tooltipTextWidth = textLineWidth;
        }

        boolean needsWrap = false;

        titleLinesCount = 1;
        tooltipX = mouseX + 12;
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
            List<ITextProperties> wrappedTextLines = new ArrayList<>();
            for (int i = 0; i < textLines.size(); i++)
            {
                ITextProperties textLine = textLines.get(i);
                List<ITextProperties> wrappedLine = font.getSplitter().splitLines(textLine, tooltipTextWidth, Style.EMPTY);
                if (i == 0)
                    titleLinesCount = wrappedLine.size();

                for (ITextProperties line : wrappedLine)
                {
                    int lineWidth = font.width(line);
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

        tooltipY = mouseY - 12;
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

        GuiUtils.drawGradientRect(event.getMatrixStack().last().pose(), 400,  tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, 0xc0000000, 0xc0000000);

        IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
        event.getMatrixStack().translate(0.0D, 0.0D, 400);

        for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber)
        {
            ITextProperties line = textLines.get(lineNumber);
            if (line != null)
                font.drawInBatch(LanguageMap.getInstance().getVisualOrder(line), (float) tooltipX, (float) tooltipY, -1, true, event.getMatrixStack().last().pose(), renderType, false, 0, 15728880);

            if (lineNumber + 1 == titleLinesCount)
                tooltipY += 2;

            tooltipY += 10;
        }

        renderType.endBatch();
    }
}
