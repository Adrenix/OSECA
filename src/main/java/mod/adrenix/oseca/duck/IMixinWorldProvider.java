package mod.adrenix.oseca.duck;

/**
 * Adds a public method to the world provider that allows for dynamic updates of the brightness table.
 */
public interface IMixinWorldProvider
{
    void reloadBrightnessTable();
}
