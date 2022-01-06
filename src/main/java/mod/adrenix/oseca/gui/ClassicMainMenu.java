package mod.adrenix.oseca.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import mod.adrenix.oseca.OSECA;
import mod.adrenix.oseca.config.MixinConfig;
import mod.adrenix.oseca.mixin.IMixinGuiMainMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.glu.Project;

import java.util.List;
import java.util.Random;

public class ClassicMainMenu extends GuiMainMenu
{
    /* Fields */

    private static final String[] MINECRAFT = {
        " *   * * *   * *** *** *** *** *** ***",
        " ** ** * **  * *   *   * * * * *    * ",
        " * * * * * * * **  *   **  *** **   * ",
        " *   * * *  ** *   *   * * * * *    * ",
        " *   * * *   * *** *** * * * * *    * "
    };

    public Random getRand() { return RAND; }
    private LogoEffectRandomizer[][] logoEffects;
    private final float updateCounter;
    private static final Random RAND = new Random();
    private static final ResourceLocation BLACK_RESOURCE = new ResourceLocation(OSECA.MODID + ":textures/black.png");

    /* Constructor */

    public ClassicMainMenu()
    {
        this.updateCounter = this.getRand().nextFloat();
    }

    /* Main Menu Overrides */

    @Override
    public void updateScreen()
    {
        if (this.logoEffects != null)
            for (LogoEffectRandomizer[] logoEffect : this.logoEffects)
                for (LogoEffectRandomizer logoEffectRandomizer : logoEffect)
                    logoEffectRandomizer.run();
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        IMixinGuiMainMenu accessor = (IMixinGuiMainMenu) this;

        this.drawDefaultBackground();
        this.drawLogo(partialTicks);
        accessor.setSplashText(ForgeHooksClient.renderMainMenu(this, this.fontRenderer, this.width, this.height, accessor.getSplashText()));

        GlStateManager.pushMatrix();
        GlStateManager.translate((float)(this.width / 2 + 90), 70.0F, 0.0F);
        GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);

        float f = 1.8F - MathHelper.abs(MathHelper.sin((float) (Minecraft.getSystemTime() % 1000L) / 1000.0F * ((float) Math.PI * 2F)) * 0.1F);
        f = f * 100.0F / (float) (this.fontRenderer.getStringWidth(accessor.getSplashText()) + 32);

        GlStateManager.scale(f, f, f);
        this.drawCenteredString(this.fontRenderer, accessor.getSplashText(), 0, -8, -256);
        GlStateManager.popMatrix();

        String version = "Minecraft 1.12.2";

        if (this.mc.isDemo())
            version = version + " Demo";
        else
            version = version + ("release".equalsIgnoreCase(this.mc.getVersionType()) ? "" : "/" + this.mc.getVersionType());

        this.drawString(this.fontRenderer, version, 2, 2, 5263440);

        if (!MixinConfig.Candy.noFMLBranding())
        {
            List<String> brands = Lists.reverse(FMLCommonHandler.instance().getBrandings(false));
            for (int line = 0; line < brands.size(); line++)
            {
                String brand = brands.get(line);
                if (!Strings.isNullOrEmpty(brand))
                    this.drawString(this.fontRenderer, brand, 2, this.height - ( 10 + line * (this.fontRenderer.FONT_HEIGHT + 1)), 16777215);
            }
        }

        this.drawString(this.fontRenderer, "Copyright Mojang AB. Do not distribute!", accessor.getWidthCopyrightRest(), this.height - 10, -1);

        if (mouseX > accessor.getWidthCopyrightRest() && mouseX < accessor.getWidthCopyrightRest() + accessor.getWidthCopyright() && mouseY > this.height - 10 && mouseY < this.height && Mouse.isInsideWindow())
            drawRect(accessor.getWidthCopyrightRest(), this.height - 1, accessor.getWidthCopyrightRest() + accessor.getWidthCopyright(), this.height, -1);

        if (accessor.getOpenGLWarning1() != null && !accessor.getOpenGLWarning1().isEmpty())
        {
            drawRect(accessor.getOpenGLWarningX1() - 2, accessor.getOpenGLWarningY1() - 2, accessor.getOpenGLWarningX2() + 2, accessor.getOpenGLWarningY2() - 1, 1428160512);
            this.drawString(this.fontRenderer, accessor.getOpenGLWarning1(), accessor.getOpenGLWarningX1(), accessor.getOpenGLWarningY1(), -1);
            this.drawString(this.fontRenderer, accessor.getOpenGLWarning2(), (this.width - accessor.getOpenGLWarning2Width()) / 2, (this.buttonList.get(0)).y - 12, -1);
        }

        for (GuiButton guiButton : this.buttonList) guiButton.drawButton(this.mc, mouseX, mouseY, partialTicks);
        for (GuiLabel guiLabel : this.labelList) guiLabel.drawLabel(this.mc, mouseX, mouseY);

        if (accessor.getAreRealmsNotificationsEnabled())
            accessor.getRealmsNotification().drawScreen(mouseX, mouseY, partialTicks);
        accessor.getModUpdateNotification().drawScreen(mouseX, mouseY, partialTicks);
    }

    /* Class Specific Methods */

    private void drawLogo(float partialTicks)
    {
        if (this.logoEffects == null)
        {
            this.logoEffects = new LogoEffectRandomizer[MINECRAFT[0].length()][MINECRAFT.length];
            for (int x = 0; x < this.logoEffects.length; x++)
                for (int y = 0; y < this.logoEffects[x].length; y++)
                    logoEffects[x][y] = new LogoEffectRandomizer(this, x, y);
        }

        GlStateManager.enableDepth();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();

        int scaleHeight = 120 * new ScaledResolution(this.mc).getScaleFactor();

        Project.gluPerspective(70F, (float) this.mc.displayWidth / (float) scaleHeight, 0.05F, 100F);
        GlStateManager.viewport(0, this.mc.displayHeight - scaleHeight, this.mc.displayWidth, scaleHeight);

        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.disableCull();
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.depthMask(true);

        for (int row = 0; row < 3; row++)
        {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.4F, 0.6F, -13F);

            if (row == 0)
            {
                GlStateManager.clear(256);
                GlStateManager.translate(0.0F, -0.4F, 0.0F);
                GlStateManager.scale(0.98F, 1.0F, 1.0F);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
            }

            if (row == 1)
            {
                GlStateManager.disableBlend();
                GlStateManager.clear(256);
            }

            if (row == 2)
            {
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(768, 1);
            }

            GlStateManager.scale(1.0F, -1.0F, 1.0F);
            GlStateManager.rotate(15.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(0.89F, 1.0F, 0.4F);
            GlStateManager.translate((float) (-MINECRAFT[0].length()) * 0.5F, (float) (-MINECRAFT.length) * 0.5F, 0.0F);

            this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            if (row == 0)
                this.mc.getTextureManager().bindTexture(BLACK_RESOURCE);

            for (int horizontal = 0; horizontal < MINECRAFT.length; horizontal++)
            {
                for (int vertical = 0; vertical < MINECRAFT[horizontal].length(); vertical++)
                {
                    char symbol = MINECRAFT[horizontal].charAt(vertical);
                    if (horizontal == 2 && ((double) this.updateCounter < 0.0001D))
                        symbol = MINECRAFT[horizontal].charAt(vertical == 20 ? vertical - 1 : (vertical == 16 ? vertical + 1 : vertical));

                    if (symbol == ' ')
                        continue;

                    GlStateManager.pushMatrix();
                    LogoEffectRandomizer logo = logoEffects[vertical][horizontal];

                    float z = (float) (logo.y + (logo.x - logo.y) * (double) partialTicks);
                    float scale = 1.0F;
                    float alpha = 1.0F;

                    if (row == 0)
                    {
                        scale = z * 0.04F + 1.0F;
                        alpha = 1.0F / scale;
                        z = 0.0F;
                    }

                    GlStateManager.translate(vertical, horizontal, z);
                    GlStateManager.scale(scale, scale, scale);

                    renderBlock(alpha);

                    GlStateManager.popMatrix();
                }
            }

            GlStateManager.popMatrix();
        }

        GlStateManager.disableBlend();
        GlStateManager.matrixMode(5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GlStateManager.enableCull();
    }

    private static int getColorFromRGBA(float red, float green, float blue, float alpha)
    {
        return (int) (alpha * 255.0F) << 24 | (int) (red * 255.0F) << 16 | (int) (green * 255.0F) << 8 | (int) (blue * 255.0F);
    }

    private static int getColorFromBrightness(float brightness, float alpha)
    {
        return getColorFromRGBA(brightness, brightness, brightness, alpha);
    }

    private void renderBlock(float alpha)
    {
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        ItemStack stoneStack = Item.getItemFromBlock(Blocks.STONE).getDefaultInstance();
        IBakedModel model = this.itemRender.getItemModelWithOverrides(stoneStack, null, null);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);

        for (EnumFacing face : EnumFacing.values())
        {
            int color = -1;

            switch (face)
            {
                case DOWN: color = getColorFromBrightness(1.0F, alpha); break;
                case UP: color = getColorFromBrightness(0.5F, alpha); break;
                case NORTH: color = getColorFromBrightness(0.0F, alpha); break;
                case SOUTH: color = getColorFromBrightness(0.8F, alpha); break;
                case WEST: case EAST: color = getColorFromBrightness(0.6F, alpha); break;
            }

            this.itemRender.renderQuads(bufferbuilder, model.getQuads(null, face, 0L), color, stoneStack);
        }

        tessellator.draw();
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
    }

    /* Old Stone Logo Randomizer */

    protected static class LogoEffectRandomizer
    {
        public double x;
        public double y;
        public double z;

        public LogoEffectRandomizer(ClassicMainMenu gui, int x, int y)
        {
            this.x = this.y = (double) (10 + y) + gui.getRand().nextDouble() * 32D + (double) x;
        }

        public void run()
        {
            this.y = this.x;

            if (this.x > 0.0D)
                this.z -= 0.59999999999999998D;

            this.x += this.z;
            this.z *= 0.90000000000000002D;

            if (this.x < 0.0D)
            {
                this.x = 0.0D;
                this.z = 0.0D;
            }
        }
    }
}
