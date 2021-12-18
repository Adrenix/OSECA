package mod.adrenix.oseca.fabric;

import mod.adrenix.oseca.Oseca;
import net.fabricmc.api.DedicatedServerModInitializer;

public class OsecaServerFabric implements DedicatedServerModInitializer
{
    @Override
    public void onInitializeServer()
    {
        // Let the server know this mod isn't going to do anything in this version of the mod.
        Oseca.LOGGER.warn("OSECA mod is currently client-side only. No changes will be made to the server.");
    }
}
