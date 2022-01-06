package mod.adrenix.oseca.mixin;

import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.gui.NotificationModUpdateScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiMainMenu.class)
public interface IMixinGuiMainMenu
{
    @Accessor void setSplashText(String splashText);
    @Accessor String getSplashText();
    @Accessor String getOpenGLWarning1();
    @Accessor String getOpenGLWarning2();
    @Accessor int getOpenGLWarningX1();
    @Accessor int getOpenGLWarningY1();
    @Accessor int getOpenGLWarningX2();
    @Accessor int getOpenGLWarningY2();
    @Accessor int getOpenGLWarning2Width();
    @Accessor int getWidthCopyrightRest();
    @Accessor int getWidthCopyright();
    @Accessor GuiScreen getRealmsNotification();
    @Accessor NotificationModUpdateScreen getModUpdateNotification();
    @Invoker("areRealmsNotificationsEnabled") boolean getAreRealmsNotificationsEnabled();
}
