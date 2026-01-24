package fuzs.omnislabs.neoforge.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.omnislabs.world.level.block.WeatheringCopperRotatedSlabBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class NeoForgeWeatheringCopperRotatedSlabBlock extends WeatheringCopperRotatedSlabBlock {
    public static final MapCodec<NeoForgeWeatheringCopperRotatedSlabBlock> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(ChangeOverTimeBlock::getAge),
            propertiesCodec()).apply(instance, NeoForgeWeatheringCopperRotatedSlabBlock::new));

    public NeoForgeWeatheringCopperRotatedSlabBlock(WeatheringCopper.WeatherState weatherState, Properties properties) {
        super(weatherState, properties);
    }

    @Override
    public MapCodec<? extends SlabBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState blockState, Level level, BlockPos blockPos, Player player, ItemStack itemStack, boolean willHarvest, FluidState fluidState) {
        boolean destroyedByPlayer = super.onDestroyedByPlayer(blockState,
                level,
                blockPos,
                player,
                itemStack,
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
