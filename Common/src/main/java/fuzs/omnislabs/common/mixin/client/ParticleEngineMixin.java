package fuzs.omnislabs.common.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import fuzs.omnislabs.common.client.handler.BlockDestroyingHandler;
import fuzs.omnislabs.common.world.level.block.RotatedSlabBlock;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ParticleEngine.class)
abstract class ParticleEngineMixin {

    @ModifyVariable(method = "crack", at = @At("STORE"))
    public BlockState addBreakingBlockEffect(BlockState blockState, @Local(argsOnly = true) BlockPos pos) {
        SlabType slabType = BlockDestroyingHandler.getSlabTypeAt(blockState, pos);
        return slabType != null ? blockState.setValue(RotatedSlabBlock.TYPE, slabType) : blockState;
    }
}
