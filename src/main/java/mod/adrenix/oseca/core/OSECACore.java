package mod.adrenix.oseca.core;

import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;

import javax.annotation.Nullable;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Map;

public class OSECACore implements IFMLLoadingPlugin
{
    public OSECACore()
    {
        MixinBootstrap.init();
        CodeSource source = this.getClass().getProtectionDomain().getCodeSource();

        if (source != null)
        {
            URL location = source.getLocation();
            try
            {
                File file = new File(location.toURI());
                if (file.isFile())
                    CoreModManager.getReparseableCoremods().remove(file.getName());
            }
            catch (URISyntaxException ignored) {}
        }
    }

    @Nullable @Override public String getSetupClass() { return null; }
    @Override public String[] getASMTransformerClass() { return new String[0]; }
    @Override public String getModContainerClass() { return null; }
    @Override public String getAccessTransformerClass() { return null; }
    @Override public void injectData(Map<String, Object> data) {}
}
