//package fuzs.omnislabs.client.handler;
//
//import com.google.common.base.Suppliers;
//import com.google.common.collect.Maps;
//import com.google.common.collect.Sets;
//import com.mojang.math.Transformation;
//import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
//import fuzs.verticalslabs.VerticalSlabs;
//import fuzs.verticalslabs.handler.DiagonalBlockHandler;
//import fuzs.verticalslabs.world.level.block.RotatedSlabBlock;
//import net.minecraft.Util;
//import net.minecraft.client.renderer.block.BlockModelShaper;
//import net.minecraft.client.renderer.block.model.MultiVariant;
//import net.minecraft.client.renderer.block.model.Variant;
//import net.minecraft.client.resources.model.*;
//import net.minecraft.core.Direction;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.data.models.model.ModelLocationUtils;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.block.state.StateDefinition;
//import net.minecraft.world.level.block.state.properties.Property;
//import net.minecraft.world.level.block.state.properties.SlabType;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.function.BiConsumer;
//import java.util.function.Function;
//import java.util.function.Supplier;
//
//public class DiagonalModelHandler {
//    public static final ResourceLocation BUILT_IN_MODEL_LOCATION = ModelLocationUtils.getModelLocation(Blocks.STONE);
//    private static final Supplier<Map<ResourceLocation, Block>> BASE_BLOCKS_BY_DIAGONAL_LOCATION;
//    private static final Map<ResourceLocation, UnbakedModel> UNBAKED_MODEL_CACHE = Maps.newConcurrentMap();
//    private static final Supplier<Map<ResourceLocation, ModelConversionData>> MODEL_LOCATION_DATA;
//    private static final Set<ResourceLocation> REPORTED_BLOCKS = Sets.newHashSet();
//
//    static {
//        BASE_BLOCKS_BY_DIAGONAL_LOCATION = Suppliers.memoize(() -> DiagonalBlockHandler.BLOCK_CONVERSIONS.entrySet()
//                .stream()
//                .map(entry -> Map.entry(BuiltInRegistries.BLOCK.getKey(entry.getValue()), entry.getKey()))
//                .collect(Util.toMap()));
//        MODEL_LOCATION_DATA = Suppliers.memoize(() -> getBlockStateConversions().entrySet()
//                .stream()
//                .map(entry -> Map.entry(BlockModelShaper.stateToModelLocation(entry.getKey()),
//                        new ModelConversionData(entry.getKey(), entry.getValue())))
//                .collect(Util.toMap()));
//    }
//
//    public static Map<BlockState, BlockState> getBlockStateConversions() {
//        Map<BlockState, BlockState> blockStates = Maps.newHashMap();
//        for (Map.Entry<Block, Block> e1 : DiagonalBlockHandler.BLOCK_CONVERSIONS.entrySet()) {
//            for (BlockState possibleState : e1.getValue().getStateDefinition().getPossibleStates()) {
//                StateDefinition<Block, BlockState> stateDefinition = e1.getKey().getStateDefinition();
//                BlockState blockState = stateDefinition.any();
//                for (Map.Entry<Property<?>, Comparable<?>> e2 : possibleState.getValues().entrySet()) {
//                    blockState = setBlockStateValue(e2.getKey(),
//                            e2.getValue(),
//                            stateDefinition::getProperty,
//                            blockState);
//                }
//                blockStates.put(possibleState, blockState);
//            }
//        }
//        return blockStates;
//    }
//
//    public static EventResultHolder<UnbakedModel> onModifyUnbakedModel(ResourceLocation modelLocation, Supplier<UnbakedModel> unbakedModel, Function<ResourceLocation, UnbakedModel> modelGetter, BiConsumer<ResourceLocation, UnbakedModel> modelAdder) {
//        ModelConversionData data = MODEL_LOCATION_DATA.get().get(modelLocation);
//        if (data != null) {
//            if (modelGetter.apply(data.resourceLocation()) instanceof MultiVariant multiVariant && isBuiltInModel(
//                    unbakedModel.get(),
//                    modelGetter.apply(ModelBakery.MISSING_MODEL_LOCATION))) {
//                List<Variant> variants = new ArrayList<>();
//                for (Variant variant : multiVariant.getVariants()) {
//                    Transformation transformation = switch (data.axis) {
//                        case Y -> BlockModelRotation.X0_Y0.getRotation();
//                        case Z -> BlockModelRotation.X270_Y0.getRotation();
//                        case X -> BlockModelRotation.X90_Y90.getRotation();
//                    };
//
//                    variants.add(new Variant(variant.getModelLocation(), transformation, false, variant.getWeight()));
//                }
//
//                return EventResultHolder.interrupt(new MultiVariant(variants));
//            } else if (REPORTED_BLOCKS.add(modelLocation)) {
//                VerticalSlabs.LOGGER.warn(
//                        "Block '{}' is not using multi variant model, in-game model will not be visible!",
//                        modelLocation);
//            }
//        }
//
//        return EventResultHolder.pass();
//    }
//
//    private static boolean isBuiltInModel(UnbakedModel unbakedModel, UnbakedModel missingModel) {
//        return missingModel == unbakedModel
//                || unbakedModel instanceof MultiVariant multiVariant && multiVariant.getVariants().size() == 1
//                && BUILT_IN_MODEL_LOCATION.equals(multiVariant.getVariants().get(0).getModelLocation());
//    }
//
//    public static ModelResourceLocation convertAnyBlockState(Block oldBlock, Block newBlock) {
//        StateDefinition<Block, BlockState> stateDefinition = oldBlock.getStateDefinition();
//        BlockState blockState = convertBlockState(newBlock.getStateDefinition(), stateDefinition.any());
//        return BlockModelShaper.stateToModelLocation(blockState);
//    }
//
//    private static BlockState convertBlockState(StateDefinition<Block, BlockState> newStateDefinition, BlockState oldBlockState) {
//        BlockState newBlockState = newStateDefinition.any();
//        for (Map.Entry<Property<?>, Comparable<?>> entry : oldBlockState.getValues().entrySet()) {
//            newBlockState = setBlockStateValue(entry.getKey(),
//                    entry.getValue(),
//                    newStateDefinition::getProperty,
//                    newBlockState);
//        }
//
//        return newBlockState;
//    }
//
//    private static <T extends Comparable<T>, V extends T> BlockState setBlockStateValue(Property<?> oldProperty, Comparable<?> oldValue, Function<String, @Nullable Property<?>> propertyGetter, BlockState blockState) {
//        Property<?> newProperty = propertyGetter.apply(oldProperty.getName());
//        if (newProperty != null) {
//            return blockState.setValue((Property<T>) newProperty, (V) oldValue);
//        }
//        return blockState;
//    }
//
//    private record ModelConversionData(ResourceLocation resourceLocation,
//                                       Direction.Axis axis,
//                                       ResourceLocation doubleReference) {
//
//        public ModelConversionData(BlockState oldBlockState, BlockState newBlockState) {
//            this(BlockModelShaper.stateToModelLocation(newBlockState),
//                    oldBlockState.getValue(RotatedSlabBlock.AXIS),
//                    newBlockState.getValue(RotatedSlabBlock.TYPE) == SlabType.DOUBLE ?
//                            BlockModelShaper.stateToModelLocation(newBlockState) :
//                            BlockModelShaper.stateToModelLocation(newBlockState.getBlock()
//                                    .getStateDefinition()
//                                    .any()
//                                    .setValue(RotatedSlabBlock.TYPE, SlabType.DOUBLE)));
//        }
//    }
//}
