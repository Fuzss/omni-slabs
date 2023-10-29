package fuzs.verticalslabs.mixin.client;

import fuzs.verticalslabs.client.handler.BlockDestroyingHandler;
import fuzs.verticalslabs.client.handler.SlabOutlineHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
abstract class MultiPlayerGameModeMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "sameDestroyTarget", at = @At("HEAD"), cancellable = true)
    private void sameDestroyTarget(BlockPos pos, CallbackInfoReturnable<Boolean> callback) {
        BlockState blockState = this.minecraft.level.getBlockState(pos);
        HitResult hitResult = this.minecraft.hitResult;
        SlabType slabType = SlabOutlineHandler.getSlabType(blockState, hitResult);
        if (BlockDestroyingHandler.getLastSlabType() != slabType) {
            callback.setReturnValue(false);
        }
    }
}
