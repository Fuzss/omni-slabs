package fuzs.verticalslabs.data.client;

import fuzs.puzzlesaccessapi.api.client.data.v2.BlockModelBuilder;
import fuzs.puzzlesaccessapi.api.client.data.v2.ItemModelBuilder;
import fuzs.puzzleslib.api.client.data.v2.AbstractModelProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.verticalslabs.handler.DiagonalBlockHandler;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.blockstates.*;
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
    protected boolean throwForMissingBlocks() {
        return false;
    }

    @Override
    public void addBlockModels(BlockModelBuilder builder) {
        TextureMapping texturemapping = TextureMapping.cube(Blocks.SMOOTH_STONE);
        TextureMapping texturemapping1 = TextureMapping.column(TextureMapping.getBlockTexture(Blocks.SMOOTH_STONE_SLAB, "_side"), texturemapping.get(TextureSlot.TOP));
        ResourceLocation resourcelocation = ModelTemplates.SLAB_BOTTOM.create(Blocks.SMOOTH_STONE_SLAB, texturemapping1, builder.getModelOutput());
        ResourceLocation resourcelocation1 = ModelTemplates.SLAB_TOP.create(Blocks.SMOOTH_STONE_SLAB, texturemapping1, builder.getModelOutput());
        ResourceLocation resourcelocation2 = ModelTemplates.CUBE_COLUMN.createWithOverride(Blocks.SMOOTH_STONE_SLAB, "_double", texturemapping1, builder.getModelOutput());
        Block slabBlock = DiagonalBlockHandler.BLOCK_CONVERSIONS.get(Blocks.SMOOTH_STONE_SLAB);
        builder.getBlockStateOutput().accept(createSlab(slabBlock, resourcelocation, resourcelocation1, resourcelocation2));
    }

    @Override
    public void addItemModels(ItemModelBuilder builder) {

    }

    static BlockStateGenerator createSlab(Block slabBlock, ResourceLocation bottomHalfModelLocation, ResourceLocation topHalfModelLocation, ResourceLocation doubleModelLocation) {
        return MultiVariantGenerator.multiVariant(slabBlock)
                .with(PropertyDispatch.property(BlockStateProperties.SLAB_TYPE)
                        .select(SlabType.BOTTOM, Variant.variant().with(VariantProperties.MODEL, bottomHalfModelLocation))
                        .select(SlabType.TOP, Variant.variant().with(VariantProperties.MODEL, topHalfModelLocation))
                        .select(SlabType.DOUBLE, Variant.variant().with(VariantProperties.MODEL, doubleModelLocation))
                )
                .with(PropertyDispatch.property(BlockStateProperties.AXIS)
                        .select(Direction.Axis.X, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true))
                        .select(Direction.Axis.Y, Variant.variant())
                        .select(Direction.Axis.Z, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true))
                );
    }
}
