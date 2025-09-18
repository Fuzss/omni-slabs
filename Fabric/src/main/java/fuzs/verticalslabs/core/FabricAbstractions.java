package fuzs.verticalslabs.core;

import fuzs.verticalslabs.world.level.block.RotatedSlabBlock;
import net.minecraft.world.level.block.Block;

public class FabricAbstractions implements CommonAbstractions {

    @Override
    public Block getVerticalSlabBlock(Block block) {
        return new RotatedSlabBlock(block);
    }
}
