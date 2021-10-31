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

    /* Swing Speeds */
    public static final int OLD_SPEED = 8;
    public static final int NEW_SPEED = 6;
    public static final int DISABLED = 0;
    public static final int GLOBAL = -1;
    public static final int MIN = DISABLED;
    public static final int MAX = 16;

    public static Config custom_speeds;
    public static int swing_speed = OLD_SPEED;
    public static int block_speed = OLD_SPEED;
    public static int sword_speed = OLD_SPEED;
    public static int tool_speed = OLD_SPEED;
    public static int fatigue_speed = GLOBAL;
    public static int haste_speed = GLOBAL;
    public static int global_speed = GLOBAL;

    /* Animations */

    public static boolean prevent_tool_disintegration = true;
    public static boolean prevent_bob_vertical = false;
    public static boolean prevent_smooth_sneak = true;
    public static boolean prevent_swing_drop = true;
    public static boolean prevent_cooldown = true;
    public static boolean prevent_reequip = true;
    public static boolean prevent_sweep = true;
    public static boolean prevent_sway = true;

    /* Eye Candy */

    public static boolean old_tooltip_boxes = true;
    public static boolean old_light_flicker = true;
    public static boolean old_damage_colors = true;
    public static boolean old_item_holding = true;
    public static boolean old_2d_items = true;

    /* Configuration */

    public static boolean mod_enabled = true;

    private static Map<String, Integer> categoricalSwingMap;
    private static Map<String, Boolean> animationStateMap;
    private static Map<String, Boolean> candyStateMap;

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
        fatigue_speed = ClientConfig.fatigue_speed.get();
        haste_speed = ClientConfig.haste_speed.get();
        global_speed = ClientConfig.global_speed.get();

        prevent_tool_disintegration = ClientConfig.prevent_tool_disintegration.get();
        prevent_bob_vertical = ClientConfig.prevent_bob_vertical.get();
        prevent_smooth_sneak = ClientConfig.prevent_smooth_sneak.get();
        prevent_swing_drop = ClientConfig.prevent_swing_drop.get();
        prevent_cooldown = ClientConfig.prevent_cooldown.get();
        prevent_reequip = ClientConfig.prevent_reequip.get();
        prevent_sweep = ClientConfig.prevent_sweep.get();
        prevent_sway = ClientConfig.prevent_sway.get();

        old_tooltip_boxes = ClientConfig.old_tooltip_boxes.get();
        old_light_flicker = ClientConfig.old_light_flicker.get();
        old_damage_colors = ClientConfig.old_damage_colors.get();
        old_item_holding = ClientConfig.old_item_holding.get();
        old_2d_items = ClientConfig.old_2d_items.get();

        mod_enabled = ClientConfig.mod_enabled.get();
    }

    public static void toggle(ForgeConfigSpec.BooleanValue config, boolean state)
    {
        config.set(state);
        bake();
    }

    public static void storeCategoricalSwingSpeeds()
    {
        sendSliderSetters();
        categoricalSwingMap = new HashMap<>();
        categoricalSwingMap.put("swing_speed", ClientConfig.swing_speed.get());
        categoricalSwingMap.put("block_speed", ClientConfig.block_speed.get());
        categoricalSwingMap.put("sword_speed", ClientConfig.sword_speed.get());
        categoricalSwingMap.put("tool_speed", ClientConfig.tool_speed.get());
        categoricalSwingMap.put("fatigue_speed", ClientConfig.fatigue_speed.get());
        categoricalSwingMap.put("haste_speed", ClientConfig.haste_speed.get());
        categoricalSwingMap.put("global_speed", ClientConfig.global_speed.get());
    }

    public static void storeAnimationValues()
    {
        animationStateMap = new HashMap<>();
        animationStateMap.put("prevent_tool_disintegration", ClientConfig.prevent_tool_disintegration.get());
        animationStateMap.put("prevent_bob_vertical", ClientConfig.prevent_bob_vertical.get());
        animationStateMap.put("prevent_smooth_sneak", ClientConfig.prevent_smooth_sneak.get());
        animationStateMap.put("prevent_swing_drop", ClientConfig.prevent_swing_drop.get());
        animationStateMap.put("prevent_cooldown", ClientConfig.prevent_cooldown.get());
        animationStateMap.put("prevent_reequip", ClientConfig.prevent_reequip.get());
        animationStateMap.put("prevent_sweep", ClientConfig.prevent_sweep.get());
        animationStateMap.put("prevent_sway", ClientConfig.prevent_sway.get());
    }

    public static void storeCandyValues()
    {
        candyStateMap = new HashMap<>();
        candyStateMap.put("old_tooltip_boxes", ClientConfig.old_tooltip_boxes.get());
        candyStateMap.put("old_torch_flicker", ClientConfig.old_light_flicker.get());
        candyStateMap.put("old_damage_colors", ClientConfig.old_damage_colors.get());
        candyStateMap.put("old_item_holding", ClientConfig.old_item_holding.get());
        candyStateMap.put("old_2d_items", ClientConfig.old_2d_items.get());
    }

    public static void resetCategoricalSwingSpeeds()
    {
        ClientConfig.swing_speed.set(OLD_SPEED);
        ClientConfig.block_speed.set(OLD_SPEED);
        ClientConfig.sword_speed.set(OLD_SPEED);
        ClientConfig.tool_speed.set(OLD_SPEED);
        ClientConfig.fatigue_speed.set(GLOBAL);
        ClientConfig.haste_speed.set(GLOBAL);
        ClientConfig.global_speed.set(GLOBAL);
        bake();
    }

    public static void resetAnimationValues()
    {
        ClientConfig.prevent_tool_disintegration.set(true);
        ClientConfig.prevent_bob_vertical.set(false);
        ClientConfig.prevent_smooth_sneak.set(true);
        ClientConfig.prevent_cooldown.set(true);
        ClientConfig.prevent_reequip.set(true);
        ClientConfig.prevent_sweep.set(true);
        ClientConfig.prevent_sway.set(true);
        ClientConfig.prevent_swing_drop.set(true);
        bake();
    }

    public static void resetCandyValues()
    {
        ClientConfig.old_tooltip_boxes.set(true);
        ClientConfig.old_light_flicker.set(true);
        ClientConfig.old_damage_colors.set(true);
        ClientConfig.old_item_holding.set(true);
        ClientConfig.old_2d_items.set(true);
        bake();
    }

    public static void undoCategoricalSwingReset()
    {
        if (categoricalSwingMap == null || categoricalSwingMap.size() == 0) return;
        ClientConfig.swing_speed.set(categoricalSwingMap.get("swing_speed"));
        ClientConfig.block_speed.set(categoricalSwingMap.get("block_speed"));
        ClientConfig.sword_speed.set(categoricalSwingMap.get("sword_speed"));
        ClientConfig.tool_speed.set(categoricalSwingMap.get("tool_speed"));
        ClientConfig.fatigue_speed.set(categoricalSwingMap.get("fatigue_speed"));
        ClientConfig.haste_speed.set(categoricalSwingMap.get("haste_speed"));
        ClientConfig.global_speed.set(categoricalSwingMap.get("global_speed"));
        categoricalSwingMap = new HashMap<>();
        bake();
    }

    public static void undoAnimationReset()
    {
        if (animationStateMap == null || animationStateMap.size() == 0) return;
        ClientConfig.prevent_tool_disintegration.set(animationStateMap.get("prevent_tool_disintegration"));
        ClientConfig.prevent_bob_vertical.set(animationStateMap.get("prevent_bob_vertical"));
        ClientConfig.prevent_smooth_sneak.set(animationStateMap.get("prevent_smooth_sneak"));
        ClientConfig.prevent_swing_drop.set(animationStateMap.get("prevent_swing_drop"));
        ClientConfig.prevent_cooldown.set(animationStateMap.get("prevent_cooldown"));
        ClientConfig.prevent_reequip.set(animationStateMap.get("prevent_reequip"));
        ClientConfig.prevent_sweep.set(animationStateMap.get("prevent_sweep"));
        ClientConfig.prevent_sway.set(animationStateMap.get("prevent_sway"));
        animationStateMap = new HashMap<>();
        bake();
    }

    public static void undoCandyReset()
    {
        if (candyStateMap == null || candyStateMap.size() == 0) return;
        ClientConfig.old_tooltip_boxes.set(candyStateMap.get("old_tooltip_boxes"));
        ClientConfig.old_light_flicker.set(candyStateMap.get("old_torch_flicker"));
        ClientConfig.old_damage_colors.set(candyStateMap.get("old_damage_colors"));
        ClientConfig.old_item_holding.set(candyStateMap.get("old_item_holding"));
        ClientConfig.old_2d_items.set(candyStateMap.get("old_2d_items"));
        candyStateMap = new HashMap<>();
        bake();
    }

    public static void sendSliderSetters()
    {
        ClientConfig.swing_speed.set(swing_speed);
        ClientConfig.block_speed.set(block_speed);
        ClientConfig.sword_speed.set(sword_speed);
        ClientConfig.tool_speed.set(tool_speed);
        ClientConfig.fatigue_speed.set(fatigue_speed);
        ClientConfig.haste_speed.set(haste_speed);
        ClientConfig.global_speed.set(global_speed);
    }

    public static int getSwingSpeed() { return swing_speed; }
    public static int getBlockSpeed() { return block_speed; }
    public static int getSwordSpeed() { return sword_speed; }
    public static int getToolSpeed() { return tool_speed; }
    public static int getFatigueSpeed() { return fatigue_speed; }
    public static int getHasteSpeed() { return haste_speed; }
    public static int getGlobalSpeed() { return global_speed; }

    private static boolean isWithinRange(int speed) { return speed <= MAX && speed >= MIN; }
    private static boolean isWithinGlobalRange(int speed) { return speed <= MAX && speed >= GLOBAL; }
    public static void setSwingSpeed(int speed) { swing_speed = isWithinRange(speed) ? speed : MIN; }
    public static void setBlockSpeed(int speed) { block_speed = isWithinRange(speed) ? speed : MIN; }
    public static void setSwordSpeed(int speed) { sword_speed = isWithinRange(speed) ? speed : MIN; }
    public static void setToolSpeed(int speed) { tool_speed = isWithinRange(speed) ? speed : MIN; }
    public static void setFatigueSpeed(int speed) { fatigue_speed = isWithinGlobalRange(speed) ? speed : GLOBAL; }
    public static void setHasteSpeed(int speed) { haste_speed = isWithinGlobalRange(speed) ? speed : GLOBAL; }
    public static void setGlobalSpeed(int speed) { global_speed = isWithinGlobalRange(speed) ? speed : GLOBAL; }
}
