package fuzs.omnislabs.common.client;

import fuzs.omnislabs.common.OmniSlabs;
import fuzs.omnislabs.common.client.handler.BlockDestroyingHandler;
import fuzs.omnislabs.common.client.handler.SlabOutlineHandler;
import fuzs.omnislabs.common.client.renderer.block.model.SlabBlockStateModel;
import fuzs.omnislabs.common.handler.BlockConversionHandler;
import fuzs.omnislabs.common.world.level.block.RotatedSlabBlock;
import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.common.api.client.core.v1.context.BlockStateResolverContext;
import fuzs.puzzleslib.common.api.client.event.v1.entity.player.ClientPlayerNetworkEvents;
import fuzs.puzzleslib.common.api.client.event.v1.renderer.ExtractBlockOutlineCallback;
import fuzs.puzzleslib.common.api.client.renderer.v1.model.ModelLoadingHelper;
import fuzs.puzzleslib.common.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.common.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.common.api.event.v1.entity.player.PlayerInteractEvents;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

public class OmniSlabsClient implements ClientModConstructor {
    public static final Identifier DISTINCT_SLABS_ID = OmniSlabs.id("distinct_slabs");

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ExtractBlockOutlineCallback.EVENT.register(SlabOutlineHandler::onExtractBlockOutline);
        PlayerInteractEvents.ATTACK_BLOCK.register(EventPhase.BEFORE, BlockDestroyingHandler::onAttackBlock);
        ClientPlayerNetworkEvents.JOIN.register(BlockDestroyingHandler::onPlayerJoin);
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
                    (BlockStateModelLoader.LoadedModels loadedModels, BiConsumer<BlockState, BlockStateModel.UnbakedRoot> blockStateConsumer) -> {
                        for (BlockState blockState : newBlock.getStateDefinition().getPossibleStates()) {
                            Direction.Axis axis = blockState.getValue(RotatedSlabBlock.AXIS);
                            boolean hasBlockModel = loadedModels.models().containsKey(blockState);
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

                            BlockStateModel.UnbakedRoot model = loadedModels.models().get(oldBlockState);
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
    public void onAddResourcePackFinders(PackRepositorySourcesContext context) {
        context.registerBuiltInPack(DISTINCT_SLABS_ID, Component.literal("Distinct Slabs"), false);
    }
}
