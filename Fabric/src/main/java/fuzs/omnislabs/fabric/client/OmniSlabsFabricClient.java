package fuzs.omnislabs.fabric.client;

import fuzs.omnislabs.common.OmniSlabs;
import fuzs.omnislabs.common.client.OmniSlabsClient;
import fuzs.omnislabs.common.client.handler.BlockDestroyingHandler;
import fuzs.omnislabs.common.mixin.client.accessor.BlockBreakingRenderStateAccessor;
import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.renderer.state.level.BlockBreakingRenderState;
import net.minecraft.world.level.block.state.BlockState;

public class OmniSlabsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(OmniSlabs.MOD_ID, OmniSlabsClient::new);
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        LevelRenderEvents.END_EXTRACTION.register((LevelExtractionContext context) -> {
            for (BlockBreakingRenderState state : context.levelState().blockBreakingRenderStates) {
                BlockState blockState = BlockDestroyingHandler.getBreakingTextureBlockState(state.blockState(),
                        state.blockPos());
                BlockBreakingRenderStateAccessor.class.cast(state).omnislabs$setBlockState(blockState);
            }
        });
    }
}
