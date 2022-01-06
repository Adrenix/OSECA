package mod.adrenix.oseca.config;

import mod.adrenix.oseca.OSECA;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = OSECA.MODID, name = OSECA.MODID)
public class ConfigHandler
{
    @Name("Mod Enabled")
    @Comment("Use this to turn the mod on or off.")
    public static boolean isModEnabled = true;

    @Name("Swing Categories")
    @Comment("Change the swing speed of different categories.")
    public static final SwingConfig SWING = new SwingConfig();

    @Name("Animations")
    @Comment("Turn on or off animation modifiers.")
    public static final AnimationConfig ANIMATION = new AnimationConfig();

    @Name("Eye Candy")
    @Comment({
        "Turn on or off eye candy features.",
        "It is recommended to combine these settings with an old textures resource pack of your choice."
    })
    public static final CandyConfig CANDY = new CandyConfig();

    @Name("Sound")
    @Comment({
        "Turn on or off sound modifications.",
        "It is recommended to combine these settings with an old sounds resource pack of your choice."
    })
    public static final SoundConfig SOUND = new SoundConfig();

    @Mod.EventBusSubscriber(modid = OSECA.MODID)
    public static class EventHandler
    {
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if (event.getModID().equals(OSECA.MODID))
            {
                ConfigManager.sync(OSECA.MODID, Config.Type.INSTANCE);
                ConfigWatcher.sync();
            }
        }
    }
}
