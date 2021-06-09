package mod.adrenix.oldswing.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;

public class AnimationConfig
{
    @Name("Prevent Arm Sway")
    @Comment("Prevent the subtle arm sway animation that plays when you look around.")
    public boolean prevent_arm_sway = true;

    @Name("Prevent Reequip Animation")
    @Comment("Prevent the animation that plays when the durability of a tool changes.")
    public boolean prevent_reequip = true;

    @Name("Prevent Cooldown Animation")
    @Comment("Prevent the animation that plays when changing between tools.")
    public boolean prevent_cooldown = true;
}
