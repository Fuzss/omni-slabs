package fuzs.verticalslabs.client;

import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.ClientPlayerEvents;
import fuzs.puzzleslib.api.client.event.v1.ModelEvents;
import fuzs.puzzleslib.api.client.event.v1.RenderHighlightCallback;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.api.event.v1.LoadCompleteCallback;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerInteractEvents;
import fuzs.puzzleslib.api.resources.v1.DynamicPackResources;
import fuzs.puzzleslib.api.resources.v1.PackResourcesHelper;
import fuzs.verticalslabs.VerticalSlabs;
import fuzs.verticalslabs.client.handler.BlockDestroyingHandler;
import fuzs.verticalslabs.client.handler.DiagonalModelHandler;
import fuzs.verticalslabs.client.handler.SlabOutlineHandler;
import fuzs.verticalslabs.data.client.DynamicModelProvider;
import fuzs.verticalslabs.handler.DiagonalBlockHandler;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.Map;

public class VerticalSlabsClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ModelEvents.MODIFY_UNBAKED_MODEL.register(DiagonalModelHandler::onModifyUnbakedModel);
        ModelEvents.AFTER_MODEL_LOADING.register(DiagonalModelHandler::onAfterModelLoading);
        LoadCompleteCallback.EVENT.register(() -> {
            // run a custom implementation here, the appropriate method in client mod constructor runs together with other mods, so we might miss some entries
            for (Map.Entry<Block, Block> entry : DiagonalBlockHandler.BLOCK_CONVERSIONS.entrySet()) {
                RenderType renderType = ClientAbstractions.INSTANCE.getRenderType(entry.getKey());
                ClientAbstractions.INSTANCE.registerRenderType(entry.getValue(), renderType);
            }
        });
        RenderHighlightCallback.EVENT.register(SlabOutlineHandler::onRenderHighlight);
        PlayerInteractEvents.ATTACK_BLOCK_V2.register(BlockDestroyingHandler::onAttackBlock);
        ClientPlayerEvents.LOGGED_IN.register(BlockDestroyingHandler::onLoggedIn);
    }

    @Override
    public void onAddResourcePackFinders(PackRepositorySourcesContext context) {
        context.addRepositorySource(PackResourcesHelper.buildClientPack(VerticalSlabs.id("default_block_models"), DynamicPackResources.create(DynamicModelProvider::new), Component.literal("this"), Component.literal("pack"), false, false, false));
    }
}
