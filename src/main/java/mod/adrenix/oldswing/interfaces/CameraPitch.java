package mod.adrenix.oldswing.interfaces;

/**
 * Camera pitching was removed from living entities in Minecraft 1.13.
 */

public interface CameraPitch
{
    void setCameraPitch(float cameraPitch);
    void setPrevCameraPitch(float prevCameraPitch);

    float getCameraPitch();
    float getPrevCameraPitch();
}
