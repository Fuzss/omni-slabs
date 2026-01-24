package fuzs.omnislabs.fabric.services;

import fuzs.omnislabs.services.CommonAbstractions;
import fuzs.omnislabs.world.level.block.RotatedSlabBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class FabricAbstractions implements CommonAbstractions {
    @Override
    public Block createRotatedSlabBlock(BlockBehaviour.Properties properties) {
        return new RotatedSlabBlock(properties);
    }
}
