package mod.adrenix.oldswing;

import mod.adrenix.oldswing.config.ConfigRegistry;
import net.fabricmc.api.ClientModInitializer;

public class OldSwingClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        // Subscribe configuration key
        ConfigRegistry.registerConfigurationKey();
    }
}
