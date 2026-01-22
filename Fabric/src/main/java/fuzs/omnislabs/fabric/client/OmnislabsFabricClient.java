package fuzs.omnislabs.fabric.client;

import fuzs.omnislabs.Omnislabs;
import fuzs.omnislabs.client.OmnislabsClient;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class OmnislabsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(Omnislabs.MOD_ID, OmnislabsClient::new);
    }
}
