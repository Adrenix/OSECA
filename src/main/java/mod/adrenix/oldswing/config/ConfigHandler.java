package mod.adrenix.oldswing.config;

import com.electronwill.nightconfig.core.Config;
import mod.adrenix.oldswing.OldSwing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = OldSwing.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ConfigHandler
{
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
    public static int global_speed = OLD_SPEED;
    public static boolean global_speed_enabled = false;
    public static boolean prevent_cooldown = true;
    public static boolean prevent_reequip = true;
    public static boolean prevent_sway = true;
    public static boolean mod_enabled = true;

    private static Map<String, Integer> categoricalSwingMap;
    private static Map<String, Boolean> animationStateMap;
    private static boolean undoGlobalState = false;

    static
    {
        final Pair<ClientConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = pair.getRight();
        CLIENT = pair.getLeft();
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent)
    {
        if (configEvent.getConfig().getSpec() == ConfigHandler.CLIENT_SPEC)
            bake();
    }

    public static void bake()
    {
        custom_speeds = ClientConfig.custom.get();
        swing_speed = ClientConfig.swing_speed.get();
        block_speed = ClientConfig.block_speed.get();
        sword_speed = ClientConfig.sword_speed.get();
        tool_speed = ClientConfig.tool_speed.get();
        global_speed = ClientConfig.global_speed.get();
        global_speed_enabled = ClientConfig.global_speed_enabled.get();
        prevent_cooldown = ClientConfig.prevent_cooldown.get();
        prevent_reequip = ClientConfig.prevent_reequip.get();
        prevent_sway = ClientConfig.prevent_sway.get();
        mod_enabled = ClientConfig.mod_enabled.get();
    }

    public static void toggle(ForgeConfigSpec.BooleanValue config, boolean state)
    {
        config.set(state);
        bake();
    }

    public static void storeCategoricalSwingSpeeds()
    {
        categoricalSwingMap = new HashMap<>();
        categoricalSwingMap.put("swing_speed", ClientConfig.swing_speed.get());
        categoricalSwingMap.put("block_speed", ClientConfig.block_speed.get());
        categoricalSwingMap.put("sword_speed", ClientConfig.sword_speed.get());
        categoricalSwingMap.put("tool_speed", ClientConfig.tool_speed.get());
        categoricalSwingMap.put("global_speed", ClientConfig.global_speed.get());
        undoGlobalState = ClientConfig.global_speed_enabled.get();
    }

    public static void storeAnimationValues()
    {
        animationStateMap = new HashMap<>();
        animationStateMap.put("prevent_cooldown", ClientConfig.prevent_cooldown.get());
        animationStateMap.put("prevent_reequip", ClientConfig.prevent_reequip.get());
        animationStateMap.put("prevent_sway", ClientConfig.prevent_sway.get());
    }

    public static void resetCategoricalSwingSpeeds()
    {
        ClientConfig.swing_speed.set(ConfigHandler.OLD_SPEED);
        ClientConfig.block_speed.set(ConfigHandler.OLD_SPEED);
        ClientConfig.sword_speed.set(ConfigHandler.OLD_SPEED);
        ClientConfig.tool_speed.set(ConfigHandler.OLD_SPEED);
        ClientConfig.global_speed.set(ConfigHandler.OLD_SPEED);
        toggle(ClientConfig.global_speed_enabled, false);
    }

    public static void resetAnimationValues()
    {
        ClientConfig.prevent_cooldown.set(true);
        ClientConfig.prevent_reequip.set(true);
        ClientConfig.prevent_sway.set(true);
        bake();
    }

    public static void undoCategoricalSwingReset()
    {
        if (categoricalSwingMap == null || categoricalSwingMap.size() == 0) return;
        ClientConfig.global_speed_enabled.set(undoGlobalState);
        ClientConfig.swing_speed.set(categoricalSwingMap.get("swing_speed"));
        ClientConfig.block_speed.set(categoricalSwingMap.get("block_speed"));
        ClientConfig.sword_speed.set(categoricalSwingMap.get("sword_speed"));
        ClientConfig.tool_speed.set(categoricalSwingMap.get("tool_speed"));
        ClientConfig.global_speed.set(categoricalSwingMap.get("global_speed"));
        animationStateMap = new HashMap<>();
        bake();
    }

    public static void undoAnimationReset()
    {
        if (animationStateMap == null || animationStateMap.size() == 0) return;
        ClientConfig.prevent_cooldown.set(animationStateMap.get("prevent_cooldown"));
        ClientConfig.prevent_reequip.set(animationStateMap.get("prevent_reequip"));
        ClientConfig.prevent_sway.set(animationStateMap.get("prevent_sway"));
        animationStateMap = new HashMap<>();
        bake();
    }

    public static void sendSliderSetters()
    {
        ClientConfig.swing_speed.set(swing_speed);
        ClientConfig.block_speed.set(block_speed);
        ClientConfig.sword_speed.set(sword_speed);
        ClientConfig.tool_speed.set(tool_speed);
        ClientConfig.global_speed.set(global_speed);
    }

    public static int getSwingSpeed() { return swing_speed; }
    public static int getBlockSpeed() { return block_speed; }
    public static int getSwordSpeed() { return sword_speed; }
    public static int getToolSpeed() { return tool_speed; }
    public static int getGlobalSpeed() { return global_speed; }

    private static boolean isWithinRange(int speed) { return speed <= MAX && speed >= MIN; }
    public static void setSwingSpeed(int speed) { swing_speed = isWithinRange(speed) ? speed : MIN; }
    public static void setBlockSpeed(int speed) { block_speed = isWithinRange(speed) ? speed : MIN; }
    public static void setSwordSpeed(int speed) { sword_speed = isWithinRange(speed) ? speed : MIN; }
    public static void setToolSpeed(int speed) { tool_speed = isWithinRange(speed) ? speed : MIN; }
    public static void setGlobalSpeed(int speed) { global_speed = isWithinRange(speed) ? speed : MIN; }
}
