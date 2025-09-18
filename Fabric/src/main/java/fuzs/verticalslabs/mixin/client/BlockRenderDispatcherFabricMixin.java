package fuzs.verticalslabs.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.verticalslabs.client.handler.BlockDestroyingHandler;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BlockRenderDispatcher.class)
abstract class BlockRenderDispatcherFabricMixin {

    @ModifyVariable(method = "renderBreakingTexture", at = @At("HEAD"))
    public BlockState renderBreakingTexture(BlockState blockState, BlockState $, BlockPos pos, BlockAndTintGetter level, PoseStack poseStack, VertexConsumer consumer) {
        return BlockDestroyingHandler.getBreakingTextureBlockState(blockState, pos);
    }
}
