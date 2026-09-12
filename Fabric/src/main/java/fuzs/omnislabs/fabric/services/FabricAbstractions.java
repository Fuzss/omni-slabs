package fuzs.omnislabs.fabric.services;

import fuzs.omnislabs.common.services.CommonAbstractions;
import fuzs.omnislabs.common.world.level.block.RotatedSlabBlock;
import fuzs.omnislabs.common.world.level.block.WeatheringCopperRotatedSlabBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;

public final class FabricAbstractions implements CommonAbstractions {
    @Override
    public Block createSimpleSlabBlock(Block block) {
        return new RotatedSlabBlock(block);
    }

    @Override
    public Block createWeatheringCopperSlabBlock(WeatheringCopper.WeatherState weatherState, Block block) {
        return new WeatheringCopperRotatedSlabBlock(weatherState, block);
    }
}
