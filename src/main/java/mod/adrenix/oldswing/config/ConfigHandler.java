package mod.adrenix.oldswing.config;

import mod.adrenix.oldswing.OldSwing;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.SlidingOption;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = OldSwing.MODID, name = "oldswing-v2")
public class ConfigHandler
{
    @Name("Mod State")
    @Comment("Use this to turn the mod on or off.")
    public static boolean mod_enabled = true;

    @Name("Global Swing State")
    @Comment("Set this to true to override all configured swing speeds.")
    public static boolean global_swing_enabled = false;

    @Name("Global Swing Speed")
    @SlidingOption
    @RangeInt(min = 0, max = 16)
    @Comment({
            "Adjust the global swinging animation modifier.",
            "Global swing has to be enabled for this to take effect.",
            SwingConfig.Comments.INFO, SwingConfig.Comments.BLANK, SwingConfig.Comments.PHOTO, SwingConfig.Comments.BLANK, SwingConfig.Comments.ALPHA, SwingConfig.Comments.MODERN
    })
    public static int global_swing_speed = ConfigHandler.Speeds.ALPHA;

    @Name("Swing Categories")
    @Comment("Change the swing speed of different categories.")
    public static SwingConfig swing_categories = new SwingConfig();

    @Name("Animations")
    @Comment("Turn on or off animation modifiers.")
    public static AnimationConfig animations = new AnimationConfig();

    @Mod.EventBusSubscriber(modid = OldSwing.MODID)
    public static class EventHandler
    {
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if (event.getModID().equals(OldSwing.MODID))
                ConfigManager.sync(OldSwing.MODID, Config.Type.INSTANCE);
        }
    }

    public static class Speeds
    {
        public static final int ALPHA = 8;
        public static final int MODERN = 6;
    }
}
