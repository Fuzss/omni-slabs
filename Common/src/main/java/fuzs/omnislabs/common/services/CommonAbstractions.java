package fuzs.omnislabs.common.services;

import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperSlabBlock;

public interface CommonAbstractions {
    CommonAbstractions INSTANCE = ServiceProviderHelper.load(CommonAbstractions.class);

    default Block createSlabBlock(Block block) {
        if (block instanceof WeatheringCopperSlabBlock slabBlock) {
            return this.createWeatheringCopperSlabBlock(slabBlock.getAge(), block);
        } else {
            return this.createSimpleSlabBlock(block);
        }
    }

    Block createSimpleSlabBlock(Block block);

    Block createWeatheringCopperSlabBlock(WeatheringCopper.WeatherState weatherState, Block block);
}
