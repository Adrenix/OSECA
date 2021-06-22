package mod.adrenix.oldswing.config;

import com.google.common.collect.Maps;
import me.shedaniel.autoconfig.AutoConfig;
import mod.adrenix.oldswing.OldSwing;
import mod.adrenix.oldswing.config.gui.widget.CustomizedRowList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

import java.util.*;

public abstract class CustomizedSwings
{
    /* Check Validity of Customized Items */

    public static void validateCustomizedSwings()
    {
        boolean isCleaned = false;

        if (OldSwing.config.custom == null)
        {
            OldSwing.config.custom = Maps.newHashMap();
            isCleaned = true;
        }

        for (Map.Entry<String, Integer> entry : OldSwing.config.custom.entrySet())
        {
            if (entry.getValue() < ClientConfig.MIN || entry.getValue() > ClientConfig.MAX)
            {
                OldSwing.log(entry.getKey() + " has invalid swing speed: " + entry.getValue());
                OldSwing.log(entry.getKey() + " has been updated to speed: " + OldSwing.OLD_SPEED);

                entry.setValue(OldSwing.OLD_SPEED);
                isCleaned = true;
            }
        }

        if (isCleaned)
            AutoConfig.getConfigHolder(ClientConfig.class).save();
    }

    /* Sort Customized Items */

    public static List<Map.Entry<String, Integer>> getSortedItems(boolean addTools, boolean addBlocks, boolean addItems)
    {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(OldSwing.config.custom.entrySet());
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>();
        List<Map.Entry<String, Integer>> tools = new ArrayList<>();
        List<Map.Entry<String, Integer>> blocks = new ArrayList<>();
        List<Map.Entry<String, Integer>> items = new ArrayList<>();
        List<Map.Entry<String, Integer>> unknown = new ArrayList<>();
        Map.Entry<String, Integer> added = null;

        for (Map.Entry<String, Integer> entry : entries)
        {
            Item item = getItem(entry);

            if (CustomizedRowList.added != null && entry.getKey().equals(CustomizedRowList.added.getKey()))
                added = entry;
            else if (!isValidEntry(item, entry))
                unknown.add(entry);
            else if (item instanceof DiggerItem || item instanceof SwordItem)
            {
                if (addTools)
                    tools.add(entry);
            }
            else if (item instanceof BlockItem)
            {
                if (addBlocks)
                    blocks.add(entry);
            }
            else
            {
                if (addItems)
                    items.add(entry);
            }
        }

        sorted.addAll(tools);
        sorted.addAll(blocks);
        sorted.addAll(items);
        sorted.addAll(unknown);
        sorted.sort(Comparator.comparing(CustomizedSwings::getLocalizedItem));

        if (added != null)
            sorted.add(0, added);

        return sorted;
    }

    /* Item <-> Config (Helpers) */

    public static boolean isValidEntry(Item item, Map.Entry<String, Integer> entry)
    {
        return getItemKey(item).equals(entry.getKey());
    }

    public static String getItemKey(Item item)
    {
        return Registry.ITEM.getKey(item).toString();
    }

    public static void addItem(Item item)
    {
        OldSwing.config.custom.put(getItemKey(item), OldSwing.OLD_SPEED);
    }

    public static Item getItem(Map.Entry<String, Integer> entry)
    {
        return Registry.ITEM.get(ResourceLocation.tryParse(entry.getKey()));
    }

    public static String getLocalizedItem(Map.Entry<String, Integer> entry)
    {
        String localized = getItem(entry).getDefaultInstance().getHoverName().getString();
        Item item = getItem(entry);
        if (getItemKey(item).equals("minecraft:air"))
            if (isValidEntry(item, entry))
                return new TranslatableComponent("gui.oldswing.customize.hand").getString();
            else
                return new TranslatableComponent("gui.oldswing.customize.unknown").getString();
        return localized;
    }

    public static Map.Entry<String, Integer> getEntryFromItem(Item item)
    {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(OldSwing.config.custom.entrySet());
        for (Map.Entry<String, Integer> entry : entries)
            if (entry.getKey().equals(Registry.ITEM.getKey(item).toString()))
                return entry;
        return null;
    }

    /* Config Tooltips */

    public static List<Component> rangeTooltip()
    {
        List<Component> tooltip = new ArrayList<>();

        Component alpha = new TranslatableComponent("gui.oldswing.customize.range.@Tooltip[0]");
        Component modern = new TranslatableComponent("gui.oldswing.customize.range.@Tooltip[1]");

        String top = ChatFormatting.GREEN + alpha.getString() + ChatFormatting.WHITE + ": " + ChatFormatting.AQUA + OldSwing.OLD_SPEED;
        String bottom = ChatFormatting.GOLD + modern.getString() + ChatFormatting.WHITE + ": " + ChatFormatting.AQUA + OldSwing.NEW_SPEED;

        tooltip.add(new TextComponent(top));
        tooltip.add(new TextComponent(bottom));

        return tooltip;
    }

    public static Component removeTooltip(Map.Entry<String, Integer> entry)
    {
        Component item = new TextComponent(getLocalizedItem(entry)).withStyle(ChatFormatting.WHITE);
        return new TranslatableComponent("gui.oldswing.customize.remove.@Tooltip", item).withStyle(ChatFormatting.RED);
    }

    public static Component undoTooltip(Map.Entry<String, Integer> entry)
    {
        Component item = new TextComponent(getLocalizedItem(entry)).withStyle(ChatFormatting.WHITE);
        return new TranslatableComponent("gui.oldswing.customize.undo.@Tooltip", item).withStyle(ChatFormatting.GREEN);
    }
}
