package fuzs.omnislabs.neoforge.services;

import fuzs.omnislabs.neoforge.world.level.block.NeoForgeRotatedSlabBlock;
import fuzs.omnislabs.neoforge.world.level.block.NeoForgeWeatheringCopperRotatedSlabBlock;
import fuzs.omnislabs.common.services.CommonAbstractions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;

public final class NeoForgeAbstractions implements CommonAbstractions {
    @Override
    public Block createSimpleSlabBlock(Block block) {
        return new NeoForgeRotatedSlabBlock(block);
    }

    @Override
    public Block createWeatheringCopperSlabBlock(WeatheringCopper.WeatherState weatherState, Block block) {
        return new NeoForgeWeatheringCopperRotatedSlabBlock(weatherState, block);
    }
}
