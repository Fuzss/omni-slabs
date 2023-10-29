package fuzs.verticalslabs.core;

import fuzs.verticalslabs.world.level.block.ForgeRotatedSlabBlock;
import net.minecraft.world.level.block.Block;

public class ForgeAbstractions implements CommonAbstractions {

    @Override
    public Block getVerticalSlabBlock(Block block) {
        return new ForgeRotatedSlabBlock(block);
    }
}
