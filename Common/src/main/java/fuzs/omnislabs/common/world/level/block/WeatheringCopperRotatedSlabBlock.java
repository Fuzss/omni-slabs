package fuzs.omnislabs.common.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @see net.minecraft.world.level.block.WeatheringCopperSlabBlock
 */
public class WeatheringCopperRotatedSlabBlock extends RotatedSlabBlock implements WeatheringCopper {
    public static final MapCodec<WeatheringCopperRotatedSlabBlock> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                    WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(ChangeOverTimeBlock::getAge),
                    BlockTypes.CODEC.fieldOf("block").forGetter(block -> block.block))
            .apply(instance, WeatheringCopperRotatedSlabBlock::new));

    private final WeatheringCopper.WeatherState weatherState;

    public WeatheringCopperRotatedSlabBlock(WeatheringCopper.WeatherState weatherState, Block block) {
        super(block);
        this.weatherState = weatherState;
    }

    @Override
    public MapCodec<? extends SlabBlock> codec() {
        return CODEC;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.changeOverTime(state, level, pos, random);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return WeatheringCopper.getNext(state.getBlock()).isPresent();
    }

    @Override
    public WeatheringCopper.WeatherState getAge() {
        return this.weatherState;
    }
}
