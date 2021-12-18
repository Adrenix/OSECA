package mod.adrenix.oseca.injector;

/**
 * Camera pitching was removed from living entities in Minecraft 1.13.
 */

public interface ICameraPitch
{
    void setCameraPitch(float cameraPitch);
    void setPrevCameraPitch(float prevCameraPitch);

    float getCameraPitch();
    float getPrevCameraPitch();
}
