package fuzs.omnislabs.neoforge.client;

import fuzs.omnislabs.OmniSlabs;
import fuzs.omnislabs.client.OmniSlabsClient;
import fuzs.omnislabs.client.handler.BlockDestroyingHandler;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.minecraft.client.renderer.state.BlockBreakingRenderState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = OmniSlabs.MOD_ID, dist = Dist.CLIENT)
public class OmniSlabsNeoForgeClient {

    public OmniSlabsNeoForgeClient() {
        ClientModConstructor.construct(OmniSlabs.MOD_ID, OmniSlabsClient::new);
        registerEventHandlers(NeoForge.EVENT_BUS);
    }

    private static void registerEventHandlers(IEventBus eventBus) {
        eventBus.addListener((final ExtractLevelRenderStateEvent event) -> {
            for (BlockBreakingRenderState renderState : event.getRenderState().blockBreakingRenderStates) {
                renderState.blockState = BlockDestroyingHandler.getBreakingTextureBlockState(renderState.blockState,
                        renderState.blockPos);
            }
        });
    }
}
