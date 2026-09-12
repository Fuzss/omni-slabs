package fuzs.omnislabs.fabric.client;

import fuzs.omnislabs.common.OmniSlabs;
import fuzs.omnislabs.common.client.OmniSlabsClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class OmniSlabsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(OmniSlabs.MOD_ID, OmniSlabsClient::new);
    }
}
