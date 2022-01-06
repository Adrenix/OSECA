package mod.adrenix.oseca.util;

import mod.adrenix.oseca.OSECA;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public abstract class SoundRedirect
{
    public static SoundEvent PLAYER_HURT;
    public static SoundEvent BLANK;

    public static void registerSounds()
    {
        PLAYER_HURT = register("entity.player.hurt");
        BLANK = register("blank");
    }

    private static SoundEvent register(String name)
    {
        ResourceLocation location = new ResourceLocation(OSECA.MODID, name);
        SoundEvent event = new SoundEvent(location);

        event.setRegistryName(name);
        ForgeRegistries.SOUND_EVENTS.register(event);

        return event;
    }
}
