package fuzs.verticalslabs.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.verticalslabs.client.handler.SlabOutlineHandler;
import fuzs.verticalslabs.world.level.block.RotatedSlabBlock;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BlockRenderDispatcher.class)
abstract class BlockRenderDispatcherFabricMixin {

    @ModifyVariable(method = "renderBreakingTexture", at = @At("HEAD"))
    public BlockState renderBreakingTexture(BlockState blockState, BlockState $, BlockPos pos, BlockAndTintGetter level, PoseStack poseStack, VertexConsumer consumer) {
        if (blockState.getRenderShape() == RenderShape.MODEL) {
            SlabType slabType = SlabOutlineHandler.getSlabType(blockState);
            if (slabType != null) {
                blockState = blockState.setValue(RotatedSlabBlock.TYPE, slabType);
            }
        }
        return blockState;
    }
}
