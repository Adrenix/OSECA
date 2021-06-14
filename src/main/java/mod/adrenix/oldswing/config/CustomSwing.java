package mod.adrenix.oldswing.config;

import com.electronwill.nightconfig.core.Config;
import mod.adrenix.oldswing.OldSwing;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Objects;

public class CustomSwing
{
    public static void add(String item, int speed)
    {
        // For some reason quotations are auto-removed from config entries when the game loads.
        // This is a hot-fix to make item names valid config entries without quotations to prevent custom swing resets.
        item = item.replace('-', ':');

        if (ForgeRegistries.ITEMS.containsKey(ResourceLocation.tryParse(item)))
        {
            try
            {
                if (speed >= ConfigHandler.MIN && speed <= ConfigHandler.MAX)
                {
                    item = item.replace(':', '-');

                    if (ClientConfig.custom.get().contains(item))
                    {
                        ClientConfig.custom.get().set(item, speed);
                        ClientConfig.custom.set(ClientConfig.custom.get());
                        return;
                    }

                    ClientConfig.custom.get().add(item, speed);
                    ClientConfig.custom.set(ClientConfig.custom.get());
                    return;
                }

                final String issue = I18n.get("oldswing.custom.out_of_range",item, ConfigHandler.MIN, ConfigHandler.MAX);
                OldSwing.LOGGER.warn(issue);
            }
            catch (NumberFormatException e)
            {
                OldSwing.LOGGER.warn(I18n.get("oldswing.custom.not_number", item));
            }
        }
        else
            OldSwing.LOGGER.warn(I18n.get("oldswing.custom.not_registered", item));
    }

    public static boolean remove(String item)
    {
        item = item.replace(':', '-');

        if (ClientConfig.custom.get().contains(item))
        {
            ClientConfig.custom.get().remove(item);
            ClientConfig.custom.set(ClientConfig.custom.get());
            return true;
        }

        return false;
    }

    public static boolean isResourceLocation(String key)
    {
        for (ResourceLocation resource : ForgeRegistries.ITEMS.getKeys())
            if (key.equals(resource.toString()))
                return true;

        return false;
    }

    @Nullable
    public static ItemStack getItemStackFromKey(String key)
    {
        for (ResourceLocation resource : ForgeRegistries.ITEMS.getKeys())
        {
            if (key.equals(resource.toString()))
            {
                Item item = ForgeRegistries.ITEMS.getValue(resource);

                if (item != null)
                    return item.getDefaultInstance();
                else
                    return null;
            }
        }

        return null;
    }

    public static String getRegistryName(String key)
    {
        String unknown = I18n.get("argument.id.unknown", key);
        Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(key));
        boolean isResource = isResourceLocation(key);
        return isResource && item != null ? item.getRegistryName() != null ? item.getName(item.getDefaultInstance()).getString() : unknown : unknown;
    }

    @Nullable
    public static Item addFromHand(ClientPlayerEntity entity, int speed)
    {
        ResourceLocation resource = ForgeRegistries.ITEMS.getKey(entity.getMainHandItem().getItem());

        if (resource == null)
            return null;

        CustomSwing.add(resource.toString(), speed);
        return entity.getMainHandItem().getItem();
    }

    public static int get(ResourceLocation source, Config.Entry entry)
    {
        if (source != null && source.toString().replace(':', '-').equals(entry.getKey()))
            return entry.getValue();
        return -1;
    }

    @Nullable
    public static Config.Entry getConfigEntry(Item item)
    {
        for (Config.Entry entry : ConfigHandler.custom_speeds.entrySet())
            if (key(entry).equals(Objects.requireNonNull(item.getRegistryName()).toString()))
                return entry;
        return null;
    }

    public static String key(Config.Entry entry)
    {
        return entry.getKey().replace('-', ':');
    }
}
