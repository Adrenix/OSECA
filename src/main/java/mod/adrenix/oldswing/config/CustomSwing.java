package mod.adrenix.oldswing.config;

import mod.adrenix.oldswing.OldSwingMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class CustomSwing {
    public static void add(String item, int speed) {
        if (ForgeRegistries.ITEMS.containsKey(ResourceLocation.tryCreate(item))) {
            try {
                if (speed >= ConfigHandler.MIN && speed <= ConfigHandler.MAX) {
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
}
