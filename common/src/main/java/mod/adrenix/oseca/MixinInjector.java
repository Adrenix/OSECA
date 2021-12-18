package mod.adrenix.oseca;

import mod.adrenix.oseca.config.ClientConfig;
import mod.adrenix.oseca.config.CommonRegistry;
import mod.adrenix.oseca.config.CustomSwings;
import mod.adrenix.oseca.config.DefaultConfig;
import mod.adrenix.oseca.injector.IReequipSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

import java.util.Map;
import java.util.function.Consumer;

public abstract class MixinInjector
{
    /* Configuration Injection References */

    private static final ClientConfig.EyeCandy EYE_CANDY = CommonRegistry.cache.eyeCandy;
    private static final ClientConfig.Animations ANIMATIONS = CommonRegistry.cache.animations;
    private static final ClientConfig.Sounds SOUNDS = CommonRegistry.cache.sounds;
    private static final ClientConfig.Swings SWINGS = CommonRegistry.cache.swings;
    private static final ClientConfig CONFIG = CommonRegistry.cache;
    public static boolean isModEnabled() { return CONFIG.isModEnabled; }

    /* Common Injection Helpers */

    public static class Inject
    {
        public static Consumer<ItemStack> explodeStack(Consumer<ItemStack> consumer)
        {
            if (!EyeCandy.oldItemMerging())
                return consumer;
            return stack -> {
                ItemStack instance = stack.copy();
                instance.setCount(1);

                for (int i = 0; i < stack.getCount(); i++)
                    consumer.accept(instance);
            };
        }

        public static ItemStack getLastItem(ItemStack originalItemStack, ItemStack rendererItemStack, ItemStack playerItemStack, IReequipSlot player)
        {
            // Item from main hand turns to air as soon as the player pulls it out. When this happens, the following strings appear in each property respectively.
            boolean isUnequipped = rendererItemStack.toString().equals("0 air") && playerItemStack.toString().equals("1 air");
            if (Animation.shouldReequip() || !isUnequipped)
                return originalItemStack;

            return player.getLastItem();
        }
    }

    /* Swing Speed Injection Helpers */

    public static class Swing
    {
        public static int getSpeedFromItem(Item item)
        {
            Map.Entry<String, Integer> entry = CustomSwings.getEntryFromItem(item);

            if (isSpeedGlobal())
                return SWINGS.global;
            else if (entry != null)
                return entry.getValue();
            else if (item instanceof SwordItem)
                return SWINGS.sword;
            else if (item instanceof BlockItem)
                return SWINGS.block;
            else if (item instanceof DiggerItem)
                return SWINGS.tool;
            return SWINGS.item;
        }

        public static int getSwingSpeed(AbstractClientPlayer player)
        {
            if (isModEnabled())
                return getSpeedFromItem(player.getMainHandItem().getItem());
            return DefaultConfig.Swings.NEW_SPEED;
        }

        public static int getGlobalSpeed() { return SWINGS.global; }
        public static int getSwingSpeed() { return getSwingSpeed(Minecraft.getInstance().player); }
        public static int getHasteSpeed() { return isSpeedGlobal() ? SWINGS.global : SWINGS.haste; }
        public static int getFatigueSpeed() { return isSpeedGlobal() ? SWINGS.global : SWINGS.miningFatigue; }
        public static boolean isSpeedGlobal() { return SWINGS.global != DefaultConfig.Swings.GLOBAL; }
        public static boolean isOverridingHaste() { return isModEnabled() && SWINGS.haste != DefaultConfig.Swings.GLOBAL; }
        public static boolean isOverridingFatigue() { return isModEnabled() && SWINGS.miningFatigue != DefaultConfig.Swings.GLOBAL; }
    }

    /* Sound Injection Helpers */

    public static class Sound
    {
        public static boolean attack() { return !isModEnabled() || SOUNDS.attack; }
    }

    /* Eye Candy Injection Helpers */

    public static class EyeCandy
    {
        public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(Oseca.MOD_ID + ":textures/gui/widgets.png");
        public static boolean oldVersionOverlay() { return isModEnabled() && EYE_CANDY.oldVersionOverlay; }
        public static boolean oldButtonHover() { return isModEnabled() && EYE_CANDY.oldButtonHover; }
        public static boolean oldDamageColors() { return isModEnabled() && EYE_CANDY.oldDamageColors; }
        public static boolean oldFloatingItems() { return isModEnabled() && EYE_CANDY.old2DItems; }
        public static boolean oldItemHolding() { return isModEnabled() && EYE_CANDY.oldItemHolding; }
        public static boolean oldItemMerging() { return isModEnabled() && EYE_CANDY.oldItemMerging; }
        public static boolean oldTooltips() { return !isModEnabled() || !EYE_CANDY.oldTooltipBoxes; }
        public static boolean oldLightFlicker() { return isModEnabled() && EYE_CANDY.oldLightFlicker; }
    }

    /* Animation Injection Helpers */

    public static class Animation
    {
        public static boolean shouldCooldown() { return !isModEnabled() || ANIMATIONS.shouldCooldown; }
        public static boolean shouldArmSway() { return !isModEnabled() || ANIMATIONS.shouldArmSway; }
        public static boolean shouldSweepAttack() { return !isModEnabled() || ANIMATIONS.shouldSweep; }
        public static boolean shouldSneakSmooth() { return !isModEnabled() || ANIMATIONS.shouldSneakSmooth; }
        public static boolean shouldCollideBob() { return !isModEnabled() || ANIMATIONS.shouldCollideBob; }
        public static boolean shouldBobVertical() { return isModEnabled() && ANIMATIONS.shouldBobVertical; }
        public static boolean shouldSwingDrop() { return !isModEnabled() || ANIMATIONS.shouldSwingDrop; }
        public static boolean shouldToolsDisintegrate() { return !isModEnabled() || ANIMATIONS.shouldToolDisintegrate; }
        public static boolean shouldReequip() { return !isModEnabled() || ANIMATIONS.shouldReequip; }
    }
}
