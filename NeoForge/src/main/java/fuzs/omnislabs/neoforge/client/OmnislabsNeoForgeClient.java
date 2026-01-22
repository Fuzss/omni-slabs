package fuzs.omnislabs.neoforge.client;

import fuzs.omnislabs.Omnislabs;
import fuzs.omnislabs.client.OmnislabsClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = Omnislabs.MOD_ID, dist = Dist.CLIENT)
public class OmnislabsNeoForgeClient {

    public OmnislabsNeoForgeClient() {
        ClientModConstructor.construct(Omnislabs.MOD_ID, OmnislabsClient::new);
    }
}
