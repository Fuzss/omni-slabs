package fuzs.omnislabs.common.data.client;

import fuzs.omnislabs.common.handler.BlockConversionHandler;
import fuzs.puzzleslib.api.client.data.v2.AbstractModelProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.core.Direction;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;

public class ModModelProvider extends AbstractModelProvider {

    public ModModelProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    protected boolean skipValidation() {
        return true;
    }

    @Override
    public void addBlockModels(BlockModelGenerators blockModelGenerators) {
        this.createOmniSlabWithOriginal(Blocks.SMOOTH_STONE_SLAB, Blocks.SMOOTH_STONE, blockModelGenerators);
    }

    public final void createOmniSlabWithOriginal(Block originalBlock, Block baseBlock, BlockModelGenerators blockModelGenerators) {
        ResourceLocation baseBlockTexture = TextureMapping.getBlockTexture(baseBlock);
        Block block = BlockConversionHandler.getBlockConversions().get(originalBlock);
        this.createColumnOmniSlab(block, originalBlock, baseBlockTexture, blockModelGenerators);
    }

    public final void createColumnOmniSlab(Block originalBlock, Block baseBlock, BlockModelGenerators blockModelGenerators) {
        this.createColumnOmniSlab(originalBlock, TextureMapping.getBlockTexture(baseBlock), blockModelGenerators);
    }

    public final void createColumnOmniSlab(Block originalBlock, ResourceLocation baseBlockTexture, BlockModelGenerators blockModelGenerators) {
        Block block = BlockConversionHandler.getBlockConversions().get(originalBlock);
        this.createColumnOmniSlab(block, block, baseBlockTexture, blockModelGenerators);
    }

    public final void createColumnOmniSlab(Block block, Block originalBlock, ResourceLocation baseBlockTexture, BlockModelGenerators blockModelGenerators) {
        TextureMapping textureMapping = TextureMapping.column(TextureMapping.getBlockTexture(originalBlock, "_side"),
                baseBlockTexture);
        this.createOmniSlab(block, textureMapping, ModelTemplates.CUBE_COLUMN, blockModelGenerators);
    }

    public final void createCubeBottomTopOmniSlab(Block originalBlock, Block baseBlock, BlockModelGenerators blockModelGenerators) {
        Block block = BlockConversionHandler.getBlockConversions().get(originalBlock);
        this.createCubeBottomTopOmniSlab(block, block, baseBlock, blockModelGenerators);
    }

    public final void createCubeBottomTopOmniSlab(Block block, Block originalBlock, Block baseBlock, BlockModelGenerators blockModelGenerators) {
        TextureMapping textureMapping = TextureMapping.cubeBottomTop(baseBlock)
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(originalBlock, "_side"));
        this.createOmniSlab(block, textureMapping, ModelTemplates.CUBE_BOTTOM_TOP, blockModelGenerators);
    }

    /**
     * @see BlockModelGenerators#createSmoothStoneSlab()
     */
    public final void createOmniSlab(Block block, TextureMapping textureMapping, ModelTemplate modelTemplate, BlockModelGenerators blockModelGenerators) {
        ResourceLocation bottomVariant = ModelTemplates.SLAB_BOTTOM.createWithOverride(block,
                "_bottom",
                textureMapping,
                blockModelGenerators.modelOutput);
        ResourceLocation topVariant = ModelTemplates.SLAB_TOP.createWithOverride(block,
                "_top",
                textureMapping,
                blockModelGenerators.modelOutput);
        ResourceLocation doubleVariant = modelTemplate.createWithOverride(block,
                "_double",
                textureMapping,
                blockModelGenerators.modelOutput);
        blockModelGenerators.blockStateOutput.accept(createSlab(block, bottomVariant, topVariant, doubleVariant));
    }

    /**
     * @see BlockModelGenerators#createSlab(Block, ResourceLocation, ResourceLocation, ResourceLocation)
     */
    public static BlockStateGenerator createSlab(Block block, ResourceLocation bottomVariant, ResourceLocation topVariant, ResourceLocation doubleVariant) {
        return MultiVariantGenerator.multiVariant(block)
                .with(PropertyDispatch.properties(BlockStateProperties.SLAB_TYPE, BlockStateProperties.AXIS)
                        .select(SlabType.BOTTOM,
                                Direction.Axis.Y,
                                Variant.variant().with(VariantProperties.MODEL, bottomVariant))
                        .select(SlabType.TOP,
                                Direction.Axis.Y,
                                Variant.variant().with(VariantProperties.MODEL, topVariant))
                        .select(SlabType.DOUBLE,
                                Direction.Axis.Y,
                                Variant.variant().with(VariantProperties.MODEL, doubleVariant))
                        .select(SlabType.BOTTOM,
                                Direction.Axis.Z,
                                Variant.variant()
                                        .with(VariantProperties.MODEL, bottomVariant)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270))
                        .select(SlabType.TOP,
                                Direction.Axis.Z,
                                Variant.variant()
                                        .with(VariantProperties.MODEL, topVariant)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270))
                        .select(SlabType.DOUBLE,
                                Direction.Axis.Z,
                                Variant.variant()
                                        .with(VariantProperties.MODEL, doubleVariant)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R270))
                        .select(SlabType.BOTTOM,
                                Direction.Axis.X,
                                Variant.variant()
                                        .with(VariantProperties.MODEL, bottomVariant)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                        .select(SlabType.TOP,
                                Direction.Axis.X,
                                Variant.variant()
                                        .with(VariantProperties.MODEL, topVariant)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                        .select(SlabType.DOUBLE,
                                Direction.Axis.X,
                                Variant.variant()
                                        .with(VariantProperties.MODEL, doubleVariant)
                                        .with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
                                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)));
    }

    public static class DistinctSlabs extends ModModelProvider {

        public DistinctSlabs(DataProviderContext context) {
            super(context);
        }

        @Override
        public void addBlockModels(BlockModelGenerators blockModelGenerators) {
            this.createColumnOmniSlab(Blocks.CUT_RED_SANDSTONE_SLAB,
                    TextureMapping.getBlockTexture(Blocks.RED_SANDSTONE, "_top"),
                    blockModelGenerators);
            this.createColumnOmniSlab(Blocks.CUT_SANDSTONE_SLAB,
                    TextureMapping.getBlockTexture(Blocks.SANDSTONE, "_top"),
                    blockModelGenerators);
            this.createColumnOmniSlab(Blocks.POLISHED_ANDESITE_SLAB, Blocks.POLISHED_ANDESITE, blockModelGenerators);
            this.createColumnOmniSlab(Blocks.POLISHED_BLACKSTONE_SLAB,
                    Blocks.POLISHED_BLACKSTONE,
                    blockModelGenerators);
            this.createColumnOmniSlab(Blocks.POLISHED_DEEPSLATE_SLAB, Blocks.POLISHED_DEEPSLATE, blockModelGenerators);
            this.createColumnOmniSlab(Blocks.POLISHED_DIORITE_SLAB, Blocks.POLISHED_DIORITE, blockModelGenerators);
            this.createColumnOmniSlab(Blocks.POLISHED_GRANITE_SLAB, Blocks.POLISHED_GRANITE, blockModelGenerators);
            this.createColumnOmniSlab(Blocks.POLISHED_TUFF_SLAB, Blocks.POLISHED_TUFF, blockModelGenerators);
            this.createColumnOmniSlab(Blocks.PRISMARINE_BRICK_SLAB, Blocks.PRISMARINE_BRICKS, blockModelGenerators);
            this.createColumnOmniSlab(Blocks.QUARTZ_SLAB,
                    TextureMapping.getBlockTexture(Blocks.QUARTZ_BLOCK, "_top"),
                    blockModelGenerators);
            this.createCubeBottomTopOmniSlab(Blocks.RED_SANDSTONE_SLAB, Blocks.RED_SANDSTONE, blockModelGenerators);
            this.createCubeBottomTopOmniSlab(Blocks.SANDSTONE_SLAB, Blocks.SANDSTONE, blockModelGenerators);
        }
    }
}
