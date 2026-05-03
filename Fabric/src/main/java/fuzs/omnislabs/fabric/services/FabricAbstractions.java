package fuzs.omnislabs.fabric.services;

import fuzs.omnislabs.common.services.CommonAbstractions;
import fuzs.omnislabs.common.world.level.block.RotatedSlabBlock;
import fuzs.omnislabs.common.world.level.block.WeatheringCopperRotatedSlabBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class FabricAbstractions implements CommonAbstractions {
    @Override
    public Block createSlabBlock(BlockBehaviour.Properties properties) {
        return new RotatedSlabBlock(properties);
    }

    @Override
    public Block createWeatheringCopperSlabBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties) {
        return new WeatheringCopperRotatedSlabBlock(weatherState, properties);
    }
}
