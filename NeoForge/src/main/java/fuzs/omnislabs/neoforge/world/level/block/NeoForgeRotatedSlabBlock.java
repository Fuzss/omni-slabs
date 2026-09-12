package fuzs.omnislabs.neoforge.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.omnislabs.common.world.level.block.RotatedSlabBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockTypes;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class NeoForgeRotatedSlabBlock extends RotatedSlabBlock {
    public static final MapCodec<NeoForgeRotatedSlabBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    BlockTypes.CODEC.fieldOf("block").forGetter(block -> block.block))
            .apply(instance, NeoForgeRotatedSlabBlock::new));

    public NeoForgeRotatedSlabBlock(Block block) {
        super(block);
    }

    @Override
    public MapCodec<? extends SlabBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState blockState, Level level, BlockPos blockPos, Player player, boolean willHarvest, FluidState fluidState) {
        boolean destroyedByPlayer = super.onDestroyedByPlayer(blockState,
                level,
                blockPos,
                player,
                willHarvest,
                fluidState);
        if (destroyedByPlayer) {
            this.destroyOnlyOneSlab(level, player, blockPos, blockState);
            return true;
        } else {
            return false;
        }
    }
}
