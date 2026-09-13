package fuzs.omnislabs.common.client;

import fuzs.omnislabs.common.OmniSlabs;
import fuzs.omnislabs.common.client.handler.BlockDestroyingHandler;
import fuzs.omnislabs.common.client.handler.SlabOutlineHandler;
import fuzs.omnislabs.common.client.renderer.block.model.SlabBlockStateModel;
import fuzs.omnislabs.common.handler.BlockConversionHandler;
import fuzs.omnislabs.common.init.ModRegistry;
import fuzs.omnislabs.common.world.level.block.RotatedSlabBlock;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.BlockStateResolverContext;
import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import fuzs.puzzleslib.api.client.event.v1.ClientTagsUpdatedCallback;
import fuzs.puzzleslib.api.client.event.v1.entity.player.ClientPlayerNetworkEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.RenderHighlightCallback;
import fuzs.puzzleslib.api.client.renderer.v1.model.ModelLoadingHelper;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerInteractEvents;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

public class OmniSlabsClient implements ClientModConstructor {
    public static final ResourceLocation DISTINCT_SLABS_ID = OmniSlabs.id("distinct_slabs");

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ClientTagsUpdatedCallback.EVENT.register(EventPhase.FIRST,
                BlockConversionHandler.onTagsUpdated(ModRegistry.UNALTERED_SLABS_BLOCK_TAG,
                        OmniSlabs.BLOCK_PREDICATE)::accept);
        RenderHighlightCallback.EVENT.register(SlabOutlineHandler::onRenderHighlight);
        PlayerInteractEvents.ATTACK_BLOCK.register(EventPhase.BEFORE, BlockDestroyingHandler::onAttackBlock);
        ClientPlayerNetworkEvents.LOGGED_IN.register(BlockDestroyingHandler::onPlayerJoin);
    }

    @Override
    public void onRegisterBlockStateResolver(BlockStateResolverContext context) {
        BlockConversionHandler.getBlockConversions().forEach((Block oldBlock, Block newBlock) -> {
            context.registerBlockStateResolver(newBlock,
                    (ResourceManager resourceManager, Executor executor) -> {
                        return CompletableFuture.supplyAsync(() -> resourceManager.getResource(BlockStateModelLoader.BLOCKSTATE_LISTER.idToFile(
                                        BuiltInRegistries.BLOCK.getKey(newBlock))), executor)
                                .thenCompose((Optional<Resource> optional) -> {
                                    return ModelLoadingHelper.loadBlockState(resourceManager,
                                            optional.isPresent() ? newBlock : oldBlock,
                                            executor);
                                });
                    },
                    (Map<BlockState, UnbakedModel> loadedModels, BiConsumer<BlockState, UnbakedModel> blockStateConsumer) -> {
                        for (BlockState blockState : newBlock.getStateDefinition().getPossibleStates()) {
                            Direction.Axis axis = blockState.getValue(RotatedSlabBlock.AXIS);
                            boolean hasBlockModel = loadedModels.containsKey(blockState);
                            boolean keepVanillaModel = axis == Direction.Axis.Y;
                            BlockState oldBlockState;
                            if (hasBlockModel) {
                                oldBlockState = blockState;
                            } else if (keepVanillaModel) {
                                oldBlockState = oldBlock.withPropertiesOf(blockState);
                            } else {
                                oldBlockState = oldBlock.withPropertiesOf(blockState)
                                        .setValue(RotatedSlabBlock.TYPE, SlabType.DOUBLE);
                            }

                            UnbakedModel model = loadedModels.get(oldBlockState);
                            if (model != null) {
                                if (hasBlockModel || keepVanillaModel) {
                                    blockStateConsumer.accept(blockState, model);
                                } else {
                                    SlabType slabType = blockState.getValue(RotatedSlabBlock.TYPE);
                                    if (slabType == SlabType.DOUBLE) {
                                        blockStateConsumer.accept(blockState, model);
                                    } else {
                                        blockStateConsumer.accept(blockState,
                                                new SlabBlockStateModel(model, axis, slabType));
                                    }
                                }
                            } else {
                                OmniSlabs.LOGGER.warn("Missing model for variant: '{}'", blockState);
                                blockStateConsumer.accept(blockState, ModelLoadingHelper.missingModel());
                            }
                        }
                    });
        });
    }

    @Override
    public void onRegisterBlockRenderTypes(RenderTypesContext<Block> context) {
        // this runs deferred by default, so we should have all entries from other mods available to us
        for (Map.Entry<Block, Block> entry : BlockConversionHandler.getBlockConversions().entrySet()) {
            context.registerRenderType(entry.getValue(), context.getRenderType(entry.getKey()));
        }
    }

    @Override
    public void onAddResourcePackFinders(PackRepositorySourcesContext context) {
        context.registerBuiltInPack(DISTINCT_SLABS_ID, Component.literal("Distinct Slabs"), false);
    }
}
