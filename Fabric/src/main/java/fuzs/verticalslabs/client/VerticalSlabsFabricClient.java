package fuzs.verticalslabs.client;

import fuzs.verticalslabs.VerticalSlabs;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class VerticalSlabsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(VerticalSlabs.MOD_ID, VerticalSlabsClient::new);
    }
}
