package fuzs.omnislabs.neoforge;

import fuzs.omnislabs.OmniSlabs;
import fuzs.omnislabs.data.tags.ModBlockTagsProvider;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.neoforged.fml.common.Mod;

@Mod(OmniSlabs.MOD_ID)
public class OmniSlabsNeoForge {

    public OmniSlabsNeoForge() {
        ModConstructor.construct(OmniSlabs.MOD_ID, OmniSlabs::new);
        DataProviderHelper.registerDataProviders(OmniSlabs.MOD_ID, ModBlockTagsProvider::new);
    }
}
