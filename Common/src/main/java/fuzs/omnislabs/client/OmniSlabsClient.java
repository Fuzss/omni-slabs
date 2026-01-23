package fuzs.omnislabs.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import fuzs.omnislabs.OmniSlabs;
import fuzs.omnislabs.client.handler.BlockDestroyingHandler;
import fuzs.omnislabs.client.handler.SlabOutlineHandler;
import fuzs.omnislabs.handler.BlockConversionHandler;
import fuzs.omnislabs.world.level.block.RotatedSlabBlock;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.BlockStateResolverContext;
import fuzs.puzzleslib.api.client.core.v1.context.RenderTypesContext;
import fuzs.puzzleslib.api.client.event.v1.entity.player.ClientPlayerNetworkEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.ExtractBlockOutlineCallback;
import fuzs.puzzleslib.api.client.renderer.v1.model.ModelLoadingHelper;
import fuzs.puzzleslib.api.client.renderer.v1.model.MutableBakedQuad;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerInteractEvents;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.core.Direction;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class OmniSlabsClient implements ClientModConstructor {
    private static final Collection<Direction> VALID_QUAD_FACES = Util.make(new ArrayList<>(Arrays.asList(Direction.values())),
            (ArrayList<Direction> list) -> {
                list.add(null);
            });
    private static final Map<Direction.Axis, Vector3fc> MIN_AXIS_VECTORS = Maps.immutableEnumMap(ImmutableMap.of(
            Direction.Axis.X,
            new Vector3f(0.5F, Float.MAX_VALUE, Float.MAX_VALUE),
            Direction.Axis.Y,
            new Vector3f(Float.MAX_VALUE, 0.5F, Float.MAX_VALUE),
            Direction.Axis.Z,
            new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, 0.5F)));
    private static final Map<Direction.Axis, Vector3fc> MAX_AXIS_VECTORS = Maps.immutableEnumMap(ImmutableMap.of(
            Direction.Axis.X,
            new Vector3f(0.5F, Float.MIN_VALUE, Float.MIN_VALUE),
            Direction.Axis.Y,
            new Vector3f(Float.MIN_VALUE, 0.5F, Float.MIN_VALUE),
            Direction.Axis.Z,
            new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, 0.5F)));

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ExtractBlockOutlineCallback.EVENT.register(SlabOutlineHandler::onExtractBlockOutline);
        PlayerInteractEvents.ATTACK_BLOCK.register(BlockDestroyingHandler::onAttackBlock);
        ClientPlayerNetworkEvents.JOIN.register(BlockDestroyingHandler::onPlayerJoin);
    }

    @Override
    public void onRegisterBlockStateResolver(BlockStateResolverContext context) {
        BlockConversionHandler.getBlockConversions().forEach((Block oldBlock, Block newBlock) -> {
            context.registerBlockStateResolver(newBlock,
                    (ResourceManager resourceManager, Executor executor) -> {
                        return ModelLoadingHelper.loadBlockState(resourceManager, oldBlock, executor);
                    },
                    (BlockStateModelLoader.LoadedModels loadedModels, BiConsumer<BlockState, BlockStateModel.UnbakedRoot> blockStateConsumer) -> {
                        for (BlockState blockState : newBlock.getStateDefinition().getPossibleStates()) {
                            Direction.Axis axis = blockState.getValue(RotatedSlabBlock.AXIS);
                            BlockState oldBlockState;
                            if (axis == Direction.Axis.Y) {
                                oldBlockState = oldBlock.withPropertiesOf(blockState);
                            } else {
                                oldBlockState = oldBlock.withPropertiesOf(blockState)
                                        .setValue(RotatedSlabBlock.TYPE, SlabType.DOUBLE);
                            }

                            BlockStateModel.UnbakedRoot model = loadedModels.models().get(oldBlockState);
                            if (model != null) {
                                if (axis == Direction.Axis.Y) {
                                    blockStateConsumer.accept(blockState, model);
                                } else {
                                    SlabType slabType = blockState.getValue(RotatedSlabBlock.TYPE);
                                    if (slabType == SlabType.DOUBLE) {
                                        blockStateConsumer.accept(blockState, model);
                                    } else {
                                        blockStateConsumer.accept(blockState, new BlockStateModel.UnbakedRoot() {
                                            @Override
                                            public BlockStateModel bake(BlockState state, ModelBaker baker) {
                                                Function<BlockModelPart, BlockModelPart> blockModelPartFunction = Util.memoize(
                                                        (BlockModelPart blockModelPart) -> {
                                                            QuadCollection quadCollection = this.rebakeQuads(
                                                                    blockModelPart,
                                                                    axis,
                                                                    slabType);
                                                            return new SimpleModelWrapper(quadCollection,
                                                                    blockModelPart.useAmbientOcclusion(),
                                                                    blockModelPart.particleIcon());
                                                        });
                                                BlockStateModel blockStateModel = model.bake(state, baker);
                                                return new BlockStateModel() {
                                                    @Override
                                                    public void collectParts(RandomSource random, List<BlockModelPart> output) {
                                                        List<BlockModelPart> tmpList = new ArrayList<>();
                                                        blockStateModel.collectParts(random, tmpList);
                                                        for (BlockModelPart blockModelPart : tmpList) {
                                                            output.add(blockModelPartFunction.apply(blockModelPart));
                                                        }
                                                    }

                                                    @Override
                                                    public TextureAtlasSprite particleIcon() {
                                                        return blockStateModel.particleIcon();
                                                    }
                                                };
                                            }

                                            private static QuadCollection rebakeQuads(BlockModelPart blockModelPart, Direction.Axis axis, SlabType slabType) {
                                                return switch (slabType) {
                                                    case BOTTOM -> rebakeQuads(blockModelPart,
                                                            MIN_AXIS_VECTORS,
                                                            axis,
                                                            slabType);
                                                    case TOP -> rebakeQuads(blockModelPart,
                                                            MAX_AXIS_VECTORS,
                                                            axis,
                                                            slabType);
                                                    default -> QuadCollection.EMPTY;
                                                };
                                            }

                                            private static QuadCollection rebakeQuads(BlockModelPart blockModelPart, Map<Direction.Axis, Vector3fc> axisVectors, Direction.Axis axis, SlabType slabType) {
                                                QuadCollection.Builder builder = new QuadCollection.Builder();
                                                for (Direction direction : VALID_QUAD_FACES) {
                                                    List<BakedQuad> bakedQuads = blockModelPart.getQuads(direction);
                                                    for (BakedQuad bakedQuad : bakedQuads) {
                                                        MutableBakedQuad mutable = MutableBakedQuad.toMutable(bakedQuad);
                                                        Vector3fc vector3fc = axisVectors.get(axis);
                                                        for (int i = 0; i < BakedQuad.VERTEX_COUNT; i++) {
                                                            if (slabType == SlabType.BOTTOM) {
                                                                mutable.position(i,
                                                                        mutable.position(i)
                                                                                .min(vector3fc, new Vector3f()));
                                                            } else if (slabType == SlabType.TOP) {
                                                                mutable.position(i,
                                                                        mutable.position(i)
                                                                                .max(vector3fc, new Vector3f()));
                                                            }
                                                        }

                                                        if (direction != null && (slabType == SlabType.DOUBLE
                                                                || direction != (slabType == SlabType.BOTTOM ?
                                                                axis.getPositive() : axis.getNegative()))) {
                                                            builder.addCulledFace(direction, mutable.toImmutable());
                                                        } else {
                                                            builder.addUnculledFace(mutable.toImmutable());
                                                        }
                                                    }
                                                }

                                                return builder.build();
                                            }

                                            @Override
                                            public Object visualEqualityGroup(BlockState state) {
                                                return model.visualEqualityGroup(state);
                                            }

                                            @Override
                                            public void resolveDependencies(Resolver resolver) {
                                                model.resolveDependencies(resolver);
                                            }
                                        });
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
            context.registerChunkRenderType(entry.getValue(), context.getChunkRenderType(entry.getKey()));
        }
    }
}
