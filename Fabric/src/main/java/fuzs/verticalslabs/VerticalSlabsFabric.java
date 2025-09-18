package fuzs.verticalslabs;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class VerticalSlabsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(VerticalSlabs.MOD_ID, VerticalSlabs::new);
    }
}
