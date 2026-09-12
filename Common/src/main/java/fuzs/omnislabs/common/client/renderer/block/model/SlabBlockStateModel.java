package fuzs.omnislabs.common.client.renderer.block.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.client.renderer.v1.model.QuadUtils;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.*;
import java.util.function.Function;

public record SlabBlockStateModel(UnbakedModel model, Direction.Axis axis, SlabType slabType) implements UnbakedModel {
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
    public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state) {
        Function<BakedModel, BakedModel> blockModelPartFunction = Util.memoize((BakedModel blockModelPart) -> {
            QuadCollection quadCollection = rebakeQuads(blockModelPart, this.axis, this.slabType);
            return new BakedModelWrapper(blockModelPart, quadCollection);
        });
        return blockModelPartFunction.apply(this.model.bake(baker, spriteGetter, state));
    }

    private static QuadCollection rebakeQuads(BakedModel blockModelPart, Direction.Axis axis, SlabType slabType) {
        return switch (slabType) {
            case BOTTOM -> rebakeQuads(blockModelPart, MIN_AXIS_VECTORS, axis, slabType);
            case TOP -> rebakeQuads(blockModelPart, MAX_AXIS_VECTORS, axis, slabType);
            default -> QuadCollection.EMPTY;
        };
    }

    private static QuadCollection rebakeQuads(BakedModel blockModelPart, Map<Direction.Axis, Vector3fc> axisVectors, Direction.Axis axis, SlabType slabType) {
        QuadCollection.Builder builder = new QuadCollection.Builder();
        for (Direction direction : VALID_QUAD_FACES) {
            List<BakedQuad> bakedQuads = blockModelPart.getQuads(null, direction, RandomSource.create());
            for (BakedQuad bakedQuad : bakedQuads) {
                MutableBakedQuad mutable = MutableBakedQuad.toMutable(bakedQuad);
                rebakeQuadPositions(mutable, axisVectors, axis, slabType);
                rebakeQuadUVs(mutable, slabType, axis, direction);
                if (direction != null && (slabType == SlabType.DOUBLE || direction != Direction.fromAxisAndDirection(
                        axis,
                        slabType == SlabType.BOTTOM ? Direction.AxisDirection.POSITIVE :
                                Direction.AxisDirection.NEGATIVE))) {
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
        for (int i = 0; i < QuadUtils.VERTEX_STRIDE; i++) {
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
            CuboidFace.UVs uvs = new CuboidFace.UVs(UVPair.unpackU(minUV),
                    UVPair.unpackV(minUV),
                    UVPair.unpackU(maxUV),
                    UVPair.unpackV(maxUV));
            CuboidFace.UVs newUvs = computeSlabUVs(uvs, slabType, axis, direction);
            for (int i = 0; i < QuadUtils.VERTEX_STRIDE; i++) {
                bakedQuad.packedUV(i, UVPair.pack(newUvs.getVertexU(i), newUvs.getVertexV(i)));
            }
        }
    }

    private static CuboidFace.UVs computeSlabUVs(CuboidFace.UVs uvs, SlabType slabType, Direction.Axis axis, Direction direction) {
        boolean isMirrored = isDirectionMirrored(direction, axis);
        boolean isMinUVMirrored = slabType == SlabType.TOP && !isMirrored || slabType == SlabType.BOTTOM && isMirrored;
        boolean isMaxUVMirrored = slabType == SlabType.BOTTOM && !isMirrored || slabType == SlabType.TOP && isMirrored;
        if (axis.isHorizontal() && (axis != Direction.Axis.Z || direction.getAxis() != Direction.Axis.Y)) {
            float textureWidth = (uvs.maxU() - uvs.minU()) / 2.0F;
            return new CuboidFace.UVs(isMinUVMirrored ? uvs.minU() + textureWidth : uvs.minU(),
                    uvs.minV(),
                    isMaxUVMirrored ? uvs.maxU() - textureWidth : uvs.maxU(),
                    uvs.maxV());
        } else {
            float textureHeight = (uvs.maxV() - uvs.minV()) / 2.0F;
            return new CuboidFace.UVs(uvs.minU(),
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
    public void resolveParents(Function<ResourceLocation, UnbakedModel> resolver) {
        this.model.resolveParents(resolver);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return this.model.getDependencies();
    }
}
