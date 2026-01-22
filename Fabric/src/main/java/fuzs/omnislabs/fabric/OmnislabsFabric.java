package fuzs.omnislabs.fabric;

import fuzs.omnislabs.Omnislabs;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class OmnislabsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(Omnislabs.MOD_ID, Omnislabs::new);
    }
}
