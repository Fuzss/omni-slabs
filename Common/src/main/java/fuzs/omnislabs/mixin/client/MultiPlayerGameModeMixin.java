package fuzs.omnislabs.mixin.client;

import fuzs.omnislabs.client.handler.BlockDestroyingHandler;
import fuzs.omnislabs.util.SlabTypeHelper;
import fuzs.omnislabs.world.level.block.RotatedSlabBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
abstract class MultiPlayerGameModeMixin {
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    private BlockPos destroyBlockPos;

    @Inject(method = "destroyBlock",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"),
            cancellable = true)
    public void destroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> callback) {
        BlockState blockState = this.minecraft.level.getBlockState(pos);
        Block block = blockState.getBlock();
        if ((!(block instanceof GameMasterBlock) || this.minecraft.player.canUseGameMasterBlocks())
                && !blockState.isAir()) {
            SlabType slabType = BlockDestroyingHandler.getSlabType(this.minecraft.player, blockState);
            if (slabType != null) {
                BlockState brokenBlockState = blockState.setValue(RotatedSlabBlock.TYPE, slabType);
                boolean destroyBlock = this.verticalslabs$destroyBlock(pos,
                        block,
                        this.minecraft.level,
                        brokenBlockState);
                BlockState newBlockState = blockState.setValue(RotatedSlabBlock.TYPE,
                        SlabTypeHelper.flipSlabType(slabType));
                this.minecraft.level.setBlock(pos, newBlockState, 11);
                // reset this so that MultiPlayerGameMode::sameDestroyTarget returns false and MultiPlayerGameMode::startDestroyBlock can be called again
                this.destroyBlockPos = new BlockPos(-1, -1, -1);
                callback.setReturnValue(destroyBlock);
            }
        }
    }

    @Unique
    private boolean verticalslabs$destroyBlock(BlockPos pos, Block block, Level level, BlockState blockState) {
        block.playerWillDestroy(level, pos, blockState, this.minecraft.player);
        FluidState fluidState = level.getFluidState(pos);
        if (level.setBlock(pos, fluidState.createLegacyBlock(), 11)) {
            block.destroy(level, pos, blockState);
            return true;
        } else {
            return false;
        }
    }

    @Inject(method = "sameDestroyTarget", at = @At("HEAD"), cancellable = true)
    private void sameDestroyTarget(BlockPos pos, CallbackInfoReturnable<Boolean> callback) {
        BlockState blockState = this.minecraft.level.getBlockState(pos);
        SlabType destroySlabType = BlockDestroyingHandler.getSlabType(this.minecraft.player, blockState);
        SlabType slabType = SlabTypeHelper.getSlabType(this.minecraft.player,
                blockState,
                pos,
                this.minecraft.hitResult.getLocation());
        if (destroySlabType != slabType) {
            callback.setReturnValue(false);
        }
    }
}
