package fuzs.omnislabs.neoforge;

import fuzs.omnislabs.Omnislabs;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.neoforged.fml.common.Mod;

@Mod(Omnislabs.MOD_ID)
public class OmnislabsNeoForge {

    public OmnislabsNeoForge() {
        ModConstructor.construct(Omnislabs.MOD_ID, Omnislabs::new);
    }
}
