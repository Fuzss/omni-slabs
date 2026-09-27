package fuzs.omnislabs.neoforge.world.level.block;

import fuzs.omnislabs.common.world.level.block.WeatheringCopperRotatedSlabBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class NeoForgeWeatheringCopperRotatedSlabBlock extends WeatheringCopperRotatedSlabBlock {
    public NeoForgeWeatheringCopperRotatedSlabBlock(WeatheringCopper.WeatherState weatherState, Properties properties) {
        super(weatherState, properties);
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
