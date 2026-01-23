package fuzs.omnislabs.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import fuzs.omnislabs.world.level.block.RotatedSlabBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
abstract class ServerPlayerGameModeMixin {
    @Shadow
    protected ServerLevel level;
    @Shadow
    @Final
    protected ServerPlayer player;

    @SuppressWarnings("UnresolvedLocalCapture")
    @Inject(method = "destroyBlock",
            at = @At("RETURN"),
            slice = @Slice(from = @At(value = "INVOKE",
                                      target = "Lnet/minecraft/server/level/ServerPlayer;preventsBlockDrops()Z")))
    public void destroyBlock(BlockPos blockPos, CallbackInfoReturnable<Boolean> callback, @Local BlockState blockState) {
        if (callback.getReturnValueZ() && this.player.preventsBlockDrops()) {
            if (blockState.getBlock() instanceof RotatedSlabBlock slabBlock) {
                slabBlock.destroyOnlyOneSlab(this.level, this.player, blockPos, blockState);
            }
        }
    }
}
