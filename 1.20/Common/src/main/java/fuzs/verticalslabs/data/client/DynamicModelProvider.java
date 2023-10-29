package fuzs.verticalslabs.data.client;

import fuzs.puzzlesaccessapi.api.client.data.v2.BlockModelBuilder;
import fuzs.puzzlesaccessapi.api.client.data.v2.ItemModelBuilder;
import fuzs.puzzleslib.api.client.data.v2.AbstractModelProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.verticalslabs.handler.DiagonalBlockHandler;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

public class DynamicModelProvider extends AbstractModelProvider {

    public DynamicModelProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addBlockModels(BlockModelBuilder builder) {
        ResourceLocation resourceLocation = ModelLocationUtils.getModelLocation(Blocks.STONE);
        DiagonalBlockHandler.BLOCK_CONVERSIONS.values().forEach((block) -> {
            builder.getBlockStateOutput().accept(MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, resourceLocation)));
            builder.skipAutoItemBlock(block);
        });
    }

    @Override
    public void addItemModels(ItemModelBuilder builder) {

    }
}
