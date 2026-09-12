package fuzs.omnislabs.common.services;

import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperSlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public interface CommonAbstractions {
    CommonAbstractions INSTANCE = ServiceProviderHelper.load(CommonAbstractions.class);

    default Block createSlabBlock(Block block, BlockBehaviour.Properties properties) {
        if (block instanceof WeatheringCopperSlabBlock slabBlock) {
            return this.createWeatheringCopperSlabBlock(slabBlock.getAge(), properties);
        } else {
            return this.createSlabBlock(properties);
        }
    }

    Block createSlabBlock(BlockBehaviour.Properties properties);

    Block createWeatheringCopperSlabBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties);
}
