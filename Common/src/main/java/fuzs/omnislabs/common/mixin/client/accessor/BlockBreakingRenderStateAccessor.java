package fuzs.omnislabs.common.mixin.client.accessor;

import net.minecraft.client.renderer.state.level.BlockBreakingRenderState;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBreakingRenderState.class)
public interface BlockBreakingRenderStateAccessor {
    @Accessor("blockState")
    @Mutable
    void omnislabs$setBlockState(BlockState blockState);
}
