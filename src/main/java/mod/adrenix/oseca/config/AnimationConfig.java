package mod.adrenix.oseca.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;

public class AnimationConfig
{
    @Name("Old Reequipping Animations")
    @Comment({
        "Disable the reequip animation from playing when the durability of a tool changes.",
        "This will also bring back the classic reequipping logic."
    })
    public boolean noReequip = true;

    @Name("Prevent Cooldown Animation")
    @Comment("Disable the animation modifier that controls how fast an item will appear after a swing or change in slot.")
    public boolean noCooldown = true;

    @Name("Prevent Arm Sway")
    @Comment("Prevent the subtle arm sway animation that plays when you look around.")
    public boolean noArmSway = true;

    @Name("Prevent Sweep Particles")
    @Comment("Prevent the sweep particles that spawn when attacking an entity.")
    public boolean noSweepParticles = true;

    @Name("Prevent Tool Explosion")
    @Comment("An explosion animation and breaking sound that happens when a tool runs out of durability.")
    public boolean noToolExplosion = true;

    @Name("Prevent Light Flickering")
    @Comment({
        "Disable the flickering of light from light emitting sources.",
        "It is recommended to combine this feature with an old lighting resource pack."
    })
    public boolean noLightFlicker = true;

    @Name("Prevent Zombie Arm Raise")
    @Comment("Prevent the up and down arm movement animations for zombies.")
    public boolean noZombieArmRaise = true;
}
