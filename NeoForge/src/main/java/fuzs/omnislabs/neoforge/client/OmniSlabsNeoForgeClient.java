package fuzs.omnislabs.neoforge.client;

import fuzs.omnislabs.OmniSlabs;
import fuzs.omnislabs.client.OmniSlabsClient;
import fuzs.omnislabs.client.handler.BlockDestroyingHandler;
import fuzs.omnislabs.data.client.ModModelProvider;
import fuzs.omnislabs.mixin.client.accessor.BlockBreakingRenderStateAccessor;
import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.minecraft.client.renderer.state.level.BlockBreakingRenderState;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.state.BlockState;
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
        DataProviderHelper.registerDataProviders(OmniSlabs.MOD_ID, ModModelProvider::new);
        DataProviderHelper.registerDataProviders(OmniSlabsClient.DISTINCT_SLABS_ID,
                PackType.CLIENT_RESOURCES,
                ModModelProvider.DistinctSlabs::new);
    }

    private static void registerEventHandlers(IEventBus eventBus) {
        eventBus.addListener((final ExtractLevelRenderStateEvent event) -> {
            for (BlockBreakingRenderState state : event.getRenderState().blockBreakingRenderStates) {
                BlockState blockState = BlockDestroyingHandler.getBreakingTextureBlockState(state.blockState(),
                        state.blockPos());
                BlockBreakingRenderStateAccessor.class.cast(state).omnislabs$setBlockState(blockState);
            }
        });
    }
}
