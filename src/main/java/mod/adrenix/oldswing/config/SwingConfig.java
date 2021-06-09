package mod.adrenix.oldswing.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.SlidingOption;

public class SwingConfig
{
    @Name("Sword Swing Speed")
    @SlidingOption
    @RangeInt(min = 0, max = 16)
    @Comment({
            "Adjust swing speed of swords.",
            Comments.INFO, Comments.BLANK, Comments.PHOTO, Comments.BLANK, Comments.ALPHA, Comments.MODERN
    })
    public int sword_swing_speed = ConfigHandler.Speeds.ALPHA;

    @Name("Tool Swing Speed")
    @SlidingOption
    @RangeInt(min = 0, max = 16)
    @Comment({
            "Adjust the swing speed of tools.",
            Comments.INFO, Comments.BLANK, Comments.PHOTO, Comments.BLANK, Comments.ALPHA, Comments.MODERN
    })
    public int tool_swing_speed = ConfigHandler.Speeds.ALPHA;

    @Name("Block Swing Speed")
    @SlidingOption
    @RangeInt(min = 0, max = 16)
    @Comment({
            "Adjust the swing speed of blocks.",
            Comments.INFO, Comments.BLANK, Comments.PHOTO, Comments.BLANK, Comments.ALPHA, Comments.MODERN
    })
    public int block_swing_speed = ConfigHandler.Speeds.ALPHA;

    public static class Comments
    {
        protected static final String BLANK = " ";
        protected static final String INFO = "The higher this number, the slower the swinging animation.";
        protected static final String PHOTO = "If you are photosensitive, then set this speed to 0 to stop the animation entirely.";
        protected static final String ALPHA = "Alpha/Beta Minecraft: 8";
        protected static final String MODERN = "Modern Minecraft: 6";
    }
}
