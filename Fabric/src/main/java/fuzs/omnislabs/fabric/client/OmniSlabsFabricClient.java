package fuzs.omnislabs.fabric.client;

import fuzs.omnislabs.OmniSlabs;
import fuzs.omnislabs.client.OmniSlabsClient;
import fuzs.omnislabs.client.handler.BlockDestroyingHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.renderer.state.BlockBreakingRenderState;

public class OmniSlabsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(OmniSlabs.MOD_ID, OmniSlabsClient::new);
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        WorldRenderEvents.END_EXTRACTION.register((WorldExtractionContext context) -> {
            for (BlockBreakingRenderState renderState : context.worldState().blockBreakingRenderStates) {
                renderState.blockState = BlockDestroyingHandler.getBreakingTextureBlockState(renderState.blockState,
                        renderState.blockPos);
            }
        });
    }
}
