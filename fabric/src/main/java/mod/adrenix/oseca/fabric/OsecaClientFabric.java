package mod.adrenix.oseca.fabric;

import mod.adrenix.oseca.Oseca;
import mod.adrenix.oseca.fabric.config.FabricRegistry;
import net.fabricmc.api.ClientModInitializer;

public class OsecaClientFabric implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        // Initialize Mod
        Oseca.init();

        // Subscribe configuration key
        FabricRegistry.registerConfigurationKey();
    }
}
