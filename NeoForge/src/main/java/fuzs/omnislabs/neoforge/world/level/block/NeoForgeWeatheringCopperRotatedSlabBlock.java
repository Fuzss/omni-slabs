package fuzs.omnislabs.neoforge.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.omnislabs.common.world.level.block.WeatheringCopperRotatedSlabBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class NeoForgeWeatheringCopperRotatedSlabBlock extends WeatheringCopperRotatedSlabBlock {
    public static final MapCodec<NeoForgeWeatheringCopperRotatedSlabBlock> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                    WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(ChangeOverTimeBlock::getAge),
                    BlockTypes.CODEC.fieldOf("block").forGetter(block -> block.block))
            .apply(instance, NeoForgeWeatheringCopperRotatedSlabBlock::new));

    public NeoForgeWeatheringCopperRotatedSlabBlock(WeatheringCopper.WeatherState weatherState, Block block) {
        super(weatherState, block);
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
