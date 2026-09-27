package fuzs.omnislabs.common.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import fuzs.omnislabs.common.client.handler.BlockDestroyingHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
    @Shadow
    private boolean isDestroying;
    @Shadow
    private BlockPos destroyBlockPos;

    @ModifyReturnValue(method = "sameDestroyTarget", at = @At("RETURN"))
    private boolean sameDestroyTarget(boolean sameDestroyTarget, BlockPos blockPos) {
        return sameDestroyTarget && BlockDestroyingHandler.isSameDestroyTarget(blockPos,
                this.minecraft.player,
                this.minecraft.level,
                this.minecraft.hitResult);
    }

    @Inject(method = "startDestroyBlock", at = @At("RETURN"))
    private void startDestroyBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> callback) {
        if (this.isDestroying && pos.equals(this.destroyBlockPos)) {
            BlockDestroyingHandler.onStartDestroy(pos,
                    this.minecraft.player,
                    this.minecraft.level,
                    this.minecraft.hitResult);
        }
    }
}
