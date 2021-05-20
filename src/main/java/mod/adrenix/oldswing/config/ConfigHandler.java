package mod.adrenix.oldswing.config;

import com.electronwill.nightconfig.core.Config;
import mod.adrenix.oldswing.OldSwingMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = OldSwingMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ConfigHandler {
    public static final ClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final int OLD_SPEED = 8;
    public static final int NEW_SPEED = 6;
    public static final int MIN = 0;
    public static final int MAX = 16;

    public static Config custom_speeds;
    public static int swing_speed = OLD_SPEED;
    public static int block_speed = OLD_SPEED;
    public static int sword_speed = OLD_SPEED;
    public static int tool_speed = OLD_SPEED;
    public static boolean prevent_cooldown = true;
    public static boolean prevent_reequip = true;
    public static boolean prevent_sway = true;
    public static boolean mod_enabled = true;

    static {
        final Pair<ClientConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = pair.getRight();
        CLIENT = pair.getLeft();
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == ConfigHandler.CLIENT_SPEC) {
            bake();
        }
    }

    public static void bake() {
        custom_speeds = ClientConfig.custom.get();
        swing_speed = ClientConfig.swing_speed.get();
        block_speed = ClientConfig.block_speed.get();
        sword_speed = ClientConfig.sword_speed.get();
        tool_speed = ClientConfig.tool_speed.get();
        prevent_cooldown = ClientConfig.prevent_cooldown.get();
        prevent_reequip = ClientConfig.prevent_reequip.get();
        prevent_sway = ClientConfig.prevent_sway.get();
        mod_enabled = ClientConfig.mod_enabled.get();
    }
}
