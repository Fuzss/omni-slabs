package fuzs.omnislabs.client.renderer.block.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.client.renderer.v1.model.MutableBakedQuad;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.*;
import java.util.function.Function;

public record SlabBlockStateModel(BlockStateModel.UnbakedRoot model,
                                  Direction.Axis axis,
                                  SlabType slabType) implements BlockStateModel.UnbakedRoot {
    private static final Collection<Direction> VALID_QUAD_FACES = Util.make(new ArrayList<>(Arrays.asList(Direction.values())),
            (List<Direction> list) -> {
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
    public BlockStateModel bake(BlockState blockState, ModelBaker modelBaker) {
        Function<BlockModelPart, BlockModelPart> blockModelPartFunction = Util.memoize((BlockModelPart blockModelPart) -> {
            QuadCollection quadCollection = rebakeQuads(blockModelPart, this.axis, this.slabType);
            return new SimpleModelWrapper(quadCollection,
                    blockModelPart.useAmbientOcclusion(),
                    blockModelPart.particleIcon());
        });
        BlockStateModel blockStateModel = this.model.bake(blockState, modelBaker);
        return new BlockStateModel() {
            @Override
            public void collectParts(RandomSource randomSource, List<BlockModelPart> output) {
                List<BlockModelPart> tmpList = new ArrayList<>();
                blockStateModel.collectParts(randomSource, tmpList);
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
            case BOTTOM -> rebakeQuads(blockModelPart, MIN_AXIS_VECTORS, axis, slabType);
            case TOP -> rebakeQuads(blockModelPart, MAX_AXIS_VECTORS, axis, slabType);
            default -> QuadCollection.EMPTY;
        };
    }

    private static QuadCollection rebakeQuads(BlockModelPart blockModelPart, Map<Direction.Axis, Vector3fc> axisVectors, Direction.Axis axis, SlabType slabType) {
        QuadCollection.Builder builder = new QuadCollection.Builder();
        for (Direction direction : VALID_QUAD_FACES) {
            List<BakedQuad> bakedQuads = blockModelPart.getQuads(direction);
            for (BakedQuad bakedQuad : bakedQuads) {
                MutableBakedQuad mutable = MutableBakedQuad.toMutable(bakedQuad);
                rebakeQuadPositions(mutable, axisVectors, axis, slabType);
                rebakeQuadUVs(mutable, slabType, axis, direction);
                if (direction != null && (slabType == SlabType.DOUBLE || direction != (slabType == SlabType.BOTTOM ?
                        axis.getPositive() : axis.getNegative()))) {
                    builder.addCulledFace(direction, mutable.toImmutable());
                } else {
                    builder.addUnculledFace(mutable.toImmutable());
                }
            }
        }

        return builder.build();
    }

    private static void rebakeQuadPositions(MutableBakedQuad bakedQuad, Map<Direction.Axis, Vector3fc> axisVectors, Direction.Axis axis, SlabType slabType) {
        Vector3fc vector3fc = axisVectors.get(axis);
        for (int i = 0; i < BakedQuad.VERTEX_COUNT; i++) {
            if (slabType == SlabType.BOTTOM) {
                bakedQuad.position(i, bakedQuad.position(i).min(vector3fc, new Vector3f()));
            } else if (slabType == SlabType.TOP) {
                bakedQuad.position(i, bakedQuad.position(i).max(vector3fc, new Vector3f()));
            }
        }

        bakedQuad.computeQuadNormals();
    }

    private static void rebakeQuadUVs(MutableBakedQuad bakedQuad, SlabType slabType, Direction.Axis axis, Direction direction) {
        if (direction != null && direction.getAxis() != axis) {
            long minUV = bakedQuad.packedUV0();
            long maxUV = bakedQuad.packedUV2();
            BlockElementFace.UVs uvs = new BlockElementFace.UVs(UVPair.unpackU(minUV),
                    UVPair.unpackV(minUV),
                    UVPair.unpackU(maxUV),
                    UVPair.unpackV(maxUV));
            BlockElementFace.UVs newUvs = computeSlabUVs(uvs, slabType, axis, direction);
            for (int i = 0; i < BakedQuad.VERTEX_COUNT; i++) {
                bakedQuad.packedUV(i, UVPair.pack(newUvs.getVertexU(i), newUvs.getVertexV(i)));
            }
        }
    }

    private static BlockElementFace.UVs computeSlabUVs(BlockElementFace.UVs uvs, SlabType slabType, Direction.Axis axis, Direction direction) {
        boolean isMirrored = isDirectionMirrored(direction, axis);
        boolean isMinUVMirrored = slabType == SlabType.TOP && !isMirrored || slabType == SlabType.BOTTOM && isMirrored;
        boolean isMaxUVMirrored = slabType == SlabType.BOTTOM && !isMirrored || slabType == SlabType.TOP && isMirrored;
        if (axis.isHorizontal() && (axis != Direction.Axis.Z || direction.getAxis() != Direction.Axis.Y)) {
            float textureWidth = (uvs.maxU() - uvs.minU()) / 2.0F;
            return new BlockElementFace.UVs(isMinUVMirrored ? uvs.minU() + textureWidth : uvs.minU(),
                    uvs.minV(),
                    isMaxUVMirrored ? uvs.maxU() - textureWidth : uvs.maxU(),
                    uvs.maxV());
        } else {
            float textureHeight = (uvs.maxV() - uvs.minV()) / 2.0F;
            return new BlockElementFace.UVs(uvs.minU(),
                    isMinUVMirrored ? uvs.minV() + textureHeight : uvs.minV(),
                    uvs.maxU(),
                    isMaxUVMirrored ? uvs.maxV() - textureHeight : uvs.maxV());
        }
    }

    /**
     * No clue how these come to be, but from testing in-game this is the configuration we want for all the uvs to match
     * the full (double) block.
     */
    private static boolean isDirectionMirrored(Direction direction, Direction.Axis axis) {
        return switch (axis) {
            case X -> direction.getAxisDirection() == (direction != Direction.DOWN ? Direction.AxisDirection.NEGATIVE :
                    Direction.AxisDirection.POSITIVE);
            case Y -> true;
            case Z -> direction.getAxisDirection() == (direction.getAxis() == Direction.Axis.Y ?
                    Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
        };
    }

    @Override
    public Object visualEqualityGroup(BlockState blockState) {
        return this.model.visualEqualityGroup(blockState);
    }

    @Override
    public void resolveDependencies(Resolver resolver) {
        this.model.resolveDependencies(resolver);
    }
}
