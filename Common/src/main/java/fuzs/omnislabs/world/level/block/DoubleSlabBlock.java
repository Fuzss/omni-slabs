package fuzs.omnislabs.world.level.block;

import fuzs.omnislabs.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DoubleSlabBlock extends RotatedPillarBlock implements EntityBlock {

    public DoubleSlabBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModRegistry.DOUBLE_SLAB_BLOCK_ENTITY_TYPE.value().create(pos, state);
    }
}
