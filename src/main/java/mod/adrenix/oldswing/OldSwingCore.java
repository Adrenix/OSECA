package mod.adrenix.oldswing;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions("mod.adrenix.oldswing")
public class OldSwingCore implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"mod.adrenix.oldswing.OldSwingTransformer"};
    }

    @Override
    public String getModContainerClass() {
        return "mod.adrenix.oldswing.OldSwingModContainer";
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
