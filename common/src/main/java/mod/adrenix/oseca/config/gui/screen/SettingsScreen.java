package mod.adrenix.oseca.config.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import me.shedaniel.autoconfig.AutoConfig;
import mod.adrenix.oseca.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class SettingsScreen extends Screen
{
    // Widget Constants
    protected static final int BUTTON_HEIGHT = 20;
    protected static final int DONE_BUTTON_TOP_OFFSET = 26;
    protected static final int LARGE_WIDTH = 204;
    protected static final int SMALL_WIDTH = 98;

    // Setting Fields
    public static final String BACKGROUND = "minecraft:textures/block/deepslate_tiles.png";
    public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation(BACKGROUND);
    protected final Minecraft minecraft;
    private final Screen parent;

    // Constructors
    public SettingsScreen(Screen parent, TranslatableComponent title)
    {
        super(title);
        this.parent = parent;
        this.minecraft = Minecraft.getInstance();
    }

    public SettingsScreen(Screen parent)
    {
        this(parent, new TranslatableComponent("gui.oseca.settings.title"));
    }

    // Init & Closing
    @Override
    protected void init()
    {
        // Adjust Widget Positions
        TranslatableComponent title = new TranslatableComponent("text.autoconfig.oseca.title");
        TranslatableComponent customize = new TranslatableComponent("gui.oseca.settings.customize");
        int titleWidth = this.minecraft.font.width(title);
        int customizeWidth = this.minecraft.font.width(customize);

        int standardX = this.width / 2 - 102;
        int standardY = this.height / 4 + 24 - 16;
        int finishedY = this.height / 4 + 48 - 16;
        int customizeX = this.width / 2 + 4;
        int customizeY = standardY;

        if (titleWidth > SMALL_WIDTH || customizeWidth > SMALL_WIDTH)
        {
            customizeX = standardX;
            customizeY = finishedY;
            finishedY = this.height / 4 + 72 - 16;
            titleWidth = LARGE_WIDTH;
            customizeWidth = LARGE_WIDTH;
        }
        else
        {
            titleWidth = SMALL_WIDTH;
            customizeWidth = SMALL_WIDTH;
        }

        // General Settings
        this.addRenderableWidget(new Button(
            standardX,
            standardY,
            titleWidth,
            BUTTON_HEIGHT,
            title,
            (button) -> this.minecraft.setScreen(AutoConfig.getConfigScreen(ClientConfig.class, this).get())
        ));

        // Custom Swing Speeds
        this.addRenderableWidget(new Button(
            customizeX,
            customizeY,
            customizeWidth,
            BUTTON_HEIGHT,
            customize,
            (button) -> this.minecraft.setScreen(new CustomizeScreen(this))
        ));

        // Done Button
        this.addRenderableWidget(new Button(
            standardX,
            finishedY,
            LARGE_WIDTH,
            BUTTON_HEIGHT,
            new TranslatableComponent("gui.done"),
            (button) -> this.onClose()
        ));
    }

    @Override
    public void onClose()
    {
        AutoConfig.getConfigHolder(ClientConfig.class).save();
        this.minecraft.setScreen(parent);
    }

    // Rendering
    @Override
    public void renderDirtBackground(int i)
    {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        builder.vertex(0.0D, this.height, 0.0D).uv(0.0F, (float) this.height / 32.0F + (float) i).color(64, 64, 64, 255).endVertex();
        builder.vertex(this.width, this.height, 0.0D).uv((float) this.width / 32.0F, (float) this.height / 32.0F + (float) i).color(64, 64, 64, 255).endVertex();
        builder.vertex(this.width, 0.0D, 0.0D).uv((float) this.width / 32.0F, (float) i).color(64, 64, 64, 255).endVertex();
        builder.vertex(0.0D, 0.0D, 0.0D).uv(0.0F, (float) i).color(64, 64, 64, 255).endVertex();
        tesselator.end();
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float ticks)
    {
        this.renderCustomizedBackground(stack, true);
        this.renderScreenTitle(stack, 40);
        super.render(stack, mouseX, mouseY, ticks);
    }

    protected void renderCustomizedBackground(PoseStack stack, boolean isHome)
    {
        if (this.minecraft.level != null && isHome)
            this.fillGradient(stack, 0, 0, this.width, this.height, -1072689136, -804253680);
        else
            this.renderDirtBackground(0);
    }

    protected void renderScreenTitle(PoseStack stack, int height)
    {
        drawCenteredString(stack, this.font, this.title.getString(), this.width / 2, height, 0xFFFFFF);
    }
}