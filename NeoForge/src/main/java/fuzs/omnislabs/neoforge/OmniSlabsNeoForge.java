package fuzs.omnislabs.neoforge;

import fuzs.omnislabs.common.OmniSlabs;
import fuzs.omnislabs.common.data.tags.ModBlockTagsProvider;
import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v3.core.DataProviderBuilder;
import net.neoforged.fml.common.Mod;

@Mod(OmniSlabs.MOD_ID)
public class OmniSlabsNeoForge {

    public OmniSlabsNeoForge() {
        ModConstructor.construct(OmniSlabs.MOD_ID, OmniSlabs::new);
        DataProviderBuilder.of(OmniSlabs.MOD_ID).addProvider(ModBlockTagsProvider::new);
    }
}
