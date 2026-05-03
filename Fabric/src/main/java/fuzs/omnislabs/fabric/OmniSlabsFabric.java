package fuzs.omnislabs.fabric;

import fuzs.omnislabs.common.OmniSlabs;
import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class OmniSlabsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(OmniSlabs.MOD_ID, OmniSlabs::new);
    }
}
