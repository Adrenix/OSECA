package mod.adrenix.oseca.config;

import java.util.HashMap;

public abstract class ConfigWatcher
{
    private static final HashMap<String, Runnable> SUBSCRIPTIONS = new HashMap<>();

    public static void subscribe(String key, Runnable subscriber)
    {
        ConfigWatcher.SUBSCRIPTIONS.put(key, subscriber);
    }

    public static void sync()
    {
        ConfigWatcher.SUBSCRIPTIONS.forEach((key, subscription) -> subscription.run());
    }
}
