package fuzs.verticalslabs.world.level.block;

import fuzs.verticalslabs.client.extensions.RotatedSlabBlockExtensions;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;

import java.util.function.Consumer;

public class ForgeRotatedSlabBlock extends RotatedSlabBlock {

    public ForgeRotatedSlabBlock(Block block) {
        super(block);
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer) {
        consumer.accept(new RotatedSlabBlockExtensions());
    }
}
