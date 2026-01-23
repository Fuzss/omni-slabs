package fuzs.omnislabs.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fuzs.omnislabs.client.handler.BlockDestroyingHandler;
import fuzs.omnislabs.util.SlabTypeHelper;
import fuzs.omnislabs.world.level.block.RotatedSlabBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MultiPlayerGameMode.class)
abstract class MultiPlayerGameModeMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @ModifyVariable(method = "destroyBlock", at = @At("STORE"))
    public BlockState destroyBlock$0(BlockState blockState, @Share("remainingBlockState") LocalRef<BlockState> remainingBlockState) {
        SlabType slabType = BlockDestroyingHandler.getSlabType(this.minecraft.player, blockState);
        if (slabType != null) {
            remainingBlockState.set(blockState.setValue(RotatedSlabBlock.TYPE, SlabTypeHelper.flipSlabType(slabType)));
            return blockState.setValue(RotatedSlabBlock.TYPE, slabType);
        } else {
            return blockState;
        }
    }

    @ModifyArg(method = "destroyBlock",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    public BlockState destroyBlock$1(BlockState blockState, @Share("remainingBlockState") LocalRef<BlockState> remainingBlockState) {
        return remainingBlockState.get() != null ? remainingBlockState.get() : blockState;
    }

    @ModifyReturnValue(method = "sameDestroyTarget", at = @At("RETURN"))
    private boolean sameDestroyTarget(boolean sameDestroyTarget, BlockPos blockPos) {
        return sameDestroyTarget && BlockDestroyingHandler.isSameDestroyTarget(blockPos,
                this.minecraft.player,
                this.minecraft.level,
                this.minecraft.hitResult);
    }
}
