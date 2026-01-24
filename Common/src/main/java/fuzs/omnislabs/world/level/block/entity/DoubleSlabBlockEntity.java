package fuzs.omnislabs.world.level.block.entity;

import fuzs.omnislabs.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class DoubleSlabBlockEntity extends BlockEntity {
    private static final String TAG_BOTTOM_BLOCK = "bottom";
    private static final String TAG_TOP_BLOCK = "top";

    private Holder<Block> bottom = Blocks.AIR.builtInRegistryHolder();
    private Holder<Block> top = Blocks.AIR.builtInRegistryHolder();

    public DoubleSlabBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModRegistry.DOUBLE_SLAB_BLOCK_ENTITY_TYPE.value(), pos, blockState);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.read(TAG_BOTTOM_BLOCK, BuiltInRegistries.BLOCK.holderByNameCodec()).ifPresent((Holder<Block> holder) -> {
            this.bottom = holder;
        });
        input.read(TAG_TOP_BLOCK, BuiltInRegistries.BLOCK.holderByNameCodec()).ifPresent((Holder<Block> holder) -> {
            this.top = holder;
        });
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store(TAG_BOTTOM_BLOCK, BuiltInRegistries.BLOCK.holderByNameCodec(), this.bottom);
        output.store(TAG_TOP_BLOCK, BuiltInRegistries.BLOCK.holderByNameCodec(), this.top);
    }
}
