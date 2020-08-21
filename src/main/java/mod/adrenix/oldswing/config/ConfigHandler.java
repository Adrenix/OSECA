package mod.adrenix.oldswing.config;

import mod.adrenix.oldswing.OldSwing;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = OldSwing.MODID, name = "oldswing")
public class ConfigHandler {
    @Config.Name("Swing Animation Modifier")
    @Config.Comment({
            "Adjust swinging animation modifier. The higher this number, the slower the swinging animation.",
            "Alpha/Beta Minecraft: 8",
            "Modern Minecraft: 6"
    })
    @Config.RangeInt(min = 4, max = 16)
    public static int swingModifier = 8;

    @Mod.EventBusSubscriber(modid = OldSwing.MODID)
    public static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(OldSwing.MODID)) {
                ConfigManager.sync(OldSwing.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
