package mod.adrenix.oseca.config;

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
    public int sword = Speeds.ALPHA;

    @Name("Tool Swing Speed")
    @SlidingOption
    @RangeInt(min = 0, max = 16)
    @Comment({
        "Adjust the swing speed of tools.",
        Comments.INFO, Comments.BLANK, Comments.PHOTO, Comments.BLANK, Comments.ALPHA, Comments.MODERN
    })
    public int tool = Speeds.ALPHA;

    @Name("Block Swing Speed")
    @SlidingOption
    @RangeInt(min = 0, max = 16)
    @Comment({
        "Adjust the swing speed of blocks.",
        Comments.INFO, Comments.BLANK, Comments.PHOTO, Comments.BLANK, Comments.ALPHA, Comments.MODERN
    })
    public int block = Speeds.ALPHA;

    @Name("Global Swing Speed")
    @SlidingOption
    @RangeInt(min = -1, max = 16)
    @Comment({
        "This value will override any swing speed that does not match the given number.",
        "Set this to -1 to disable.",
        SwingConfig.Comments.INFO, SwingConfig.Comments.BLANK,
        SwingConfig.Comments.PHOTO, SwingConfig.Comments.BLANK,
        SwingConfig.Comments.ALPHA, SwingConfig.Comments.MODERN
    })
    public int global = Speeds.DISABLED;

    @Name("Haste Swing Speed")
    @SlidingOption
    @RangeInt(min = -1, max = 16)
    @Comment({
        "Override swing speed when haste is applied.",
        "Set this to -1 to disable.",
        SwingConfig.Comments.INFO, SwingConfig.Comments.BLANK,
        SwingConfig.Comments.PHOTO, SwingConfig.Comments.BLANK,
        SwingConfig.Comments.ALPHA, SwingConfig.Comments.MODERN
    })
    public int haste = Speeds.DISABLED;

    @Name("Mining Fatigue Swing Speed")
    @SlidingOption
    @RangeInt(min = -1, max = 16)
    @Comment({
        "Override swing speed when mining fatigue is applied.",
        "Set this to -1 to disable.",
        SwingConfig.Comments.INFO, SwingConfig.Comments.BLANK,
        SwingConfig.Comments.PHOTO, SwingConfig.Comments.BLANK,
        SwingConfig.Comments.ALPHA, SwingConfig.Comments.MODERN
    })
    public int fatigue = Speeds.DISABLED;

    public static class Speeds
    {
        public static final int ALPHA = 8;
        public static final int MODERN = 6;
        public static final int DISABLED = -1;
        public static final int PHOTOSENSITIVE = 0;
    }

    private static class Comments
    {
        private static final String BLANK = " ";
        private static final String INFO = "The higher this number, the slower the swinging animation.";
        private static final String PHOTO = "If you are photosensitive, then set this speed to 0 to stop the animation entirely.";
        private static final String ALPHA = "Alpha/Beta Minecraft: 8";
        private static final String MODERN = "Modern Minecraft: 6";
    }
}
