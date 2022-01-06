package mod.adrenix.oseca.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;

public class CandyConfig
{
    @Name("Old Main Menu")
    @Comment({
        "Bring back the old main menu screen when the game opens.",
        "This feature will not work if OptiFine is installed."
    })
    public boolean doOldMainMenu = true;

    @Name("No Forge Branding")
    @Comment("Disables forge information that is displayed on the main menu.")
    public boolean noFMLBranding = true;

    @Name("2D Floating Items")
    @Comment({
        "Bring back the old 2D floating items.",
        "This will \"simulate\" items being in 2D to maintain mod compatibility."
    })
    public boolean do2DItems = true;

    @Name("Disable Item Merging")
    @Comment({
        "Disable item entities from merging.",
        "This will not work in multiplayer."
    })
    public boolean noItemMerging = true;

    @Name("Old Damage Colors")
    @Comment("Bring back the old durability bar colors.")
    public boolean doOldDamageBar = true;

    @Name("Version Overlay")
    @Comment("Bring back the old Minecraft version overlay on the HUD.")
    public boolean doVersionOverlay = true;

    @Name("Old Lighting")
    @Comment("Bring back the old lighting that was changed in Minecraft Beta 1.8.")
    public boolean doOldLight = true;

    @Name("Old Fog")
    @Comment("Bring back the old fog rendering.")
    public boolean doOldFog = true;

    @Name("Old Nether Fog")
    @Comment("Bring back the old nether fog rendering.")
    public boolean doOldNetherFog = true;

    @Name("Old Cloud Height")
    @Comment("Brick back the old cloud height.")
    public boolean doOldCloudHeight = true;

    @Name("Old Sunrise at North")
    @Comment("Change the sunrise/sunset to north/south.")
    public boolean doOldSunriseAtNorth = true;

    @Name("Old Tooltip Boxes")
    @Comment("Bring back the old tooltip boxes.")
    public boolean doOldTooltips = true;

    @Name("Fix Light Driven Fog")
    @Comment({
        "Disables the sky and fog from changing colors based on surrounding light level.",
        "Fixes bug MC-31681."
    })
    public boolean noLightDrivenFog = true;

    @Name("Old Item Holding")
    @Comment("Brings back the old first person item holding positions.")
    public boolean doOldItemHolding = true;

    @Name("Old Explosion Particles")
    @Comment("Bring back the old explosion particles.")
    public boolean doOldExplosions = true;
}
