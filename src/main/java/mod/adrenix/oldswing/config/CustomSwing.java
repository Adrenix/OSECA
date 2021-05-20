package mod.adrenix.oldswing.config;

import com.electronwill.nightconfig.core.Config;
import mod.adrenix.oldswing.OldSwingMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class CustomSwing {
    public static void add(String item, int speed) {
        // For some reason quotations are auto-removed from config entries when the game loads.
        // This is a hot-fix to make item names valid config entries without quotations to prevent custom swing resets.
        item = item.replace('-', ':');

        if (ForgeRegistries.ITEMS.containsKey(ResourceLocation.tryCreate(item))) {
            try {
                if (speed >= ConfigHandler.MIN && speed <= ConfigHandler.MAX) {
                    item = item.replace(':', '-');

                    if (ClientConfig.custom.get().contains(item)) {
                        ClientConfig.custom.get().set(item, speed);
                        ClientConfig.custom.set(ClientConfig.custom.get());

                        OldSwingMod.LOGGER.info(String.format("Replacing %s with new swing speed of %d.", item, speed));
                        return;
                    }

                    ClientConfig.custom.get().add(item, speed);
                    ClientConfig.custom.set(ClientConfig.custom.get());

                    OldSwingMod.LOGGER.info(String.format("Adding %s with custom swing speed of %d.", item, speed));
                    return;
                }

                final String issue = String.format("%s will will not receive a custom speed since the number was out of range (%d ~ %d)",
                        item, ConfigHandler.MIN, ConfigHandler.MAX);

                OldSwingMod.LOGGER.error(issue);
            } catch (NumberFormatException e) {
                OldSwingMod.LOGGER.error(item + " will not receive a custom speed since the speed provided was not a number.");
            }
        } else {
            OldSwingMod.LOGGER.warn(item + " will not receive a custom speed since it was not found in the forge item registry.");
        }
    }

    public static boolean remove(String item) {
        item = item.replace(':', '-');

        if (ClientConfig.custom.get().contains(item)) {
            ClientConfig.custom.get().remove(item);
            ClientConfig.custom.set(ClientConfig.custom.get());
            return true;
        }

        return false;
    }

    public static int get(ResourceLocation source, Config.Entry entry) {
        if (source != null && source.toString().replace(':', '-').equals(entry.getKey())) {
            return entry.getValue();
        }

        return -1;
    }

    public static String key(Config.Entry entry) {
        return entry.getKey().replace('-', ':');
    }
}
