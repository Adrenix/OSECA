package mod.adrenix.oseca.forge;

import mod.adrenix.oseca.Oseca;
import mod.adrenix.oseca.forge.config.ForgeRegistry;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.NetworkConstants;

@Mod(Oseca.MOD_ID)
public class OsecaForge
{
    public OsecaForge()
    {
        // Let everyone know this is currently a client-side mod
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        // Let the server know this mod isn't going to do anything in this version of the mod.
        if (FMLLoader.getDist().isDedicatedServer())
            Oseca.LOGGER.warn("OSECA mod is currently client-side only. No changes will be made to the server.");
        else
            // Set up the config on client startup.
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ForgeRegistry::setup);
    }
}
