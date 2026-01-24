package fuzs.omnislabs.neoforge.services;

import fuzs.omnislabs.neoforge.world.level.block.NeoForgeRotatedSlabBlock;
import fuzs.omnislabs.neoforge.world.level.block.NeoForgeWeatheringCopperRotatedSlabBlock;
import fuzs.omnislabs.services.CommonAbstractions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class NeoForgeAbstractions implements CommonAbstractions {
    @Override
    public Block createSlabBlock(BlockBehaviour.Properties properties) {
        return new NeoForgeRotatedSlabBlock(properties);
    }

    @Override
    public Block createWeatheringCopperSlabBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties) {
        return new NeoForgeWeatheringCopperRotatedSlabBlock(weatherState, properties);
    }
}
