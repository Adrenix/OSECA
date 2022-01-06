package mod.adrenix.oseca.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;

public class SoundConfig
{
    @Name("Old Oof Sounds")
    @Comment("Bring back the old \"oof\" sounds for just player entities only.")
    public boolean doOldOof = true;

    @Name("No Attack Sounds")
    @Comment("Disable all attack sounds like sweeping and critical hits.")
    public boolean noAttack = true;

    @Name("No Falling Sounds")
    @Comment("Disable the falling sounds when an entity falls to the ground.")
    public boolean noFallSounds = true;

    @Name("No Unique Mob Steps")
    @Comment("Replace unique mob footstep sounds with the block they're walking on.")
    public boolean noUniqueSteps = true;
}
