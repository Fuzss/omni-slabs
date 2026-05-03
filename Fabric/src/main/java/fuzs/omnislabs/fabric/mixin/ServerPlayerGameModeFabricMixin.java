package fuzs.omnislabs.fabric.mixin;

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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
abstract class ServerPlayerGameModeFabricMixin {
    @Shadow
    protected ServerLevel level;
    @Shadow
    @Final
    protected ServerPlayer player;

    @Inject(method = "destroyBlock",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/world/level/block/Block;destroy(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    public void destroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> callback, @Local BlockState adjustedState) {
        if (adjustedState.getBlock() instanceof RotatedSlabBlock slabBlock) {
            slabBlock.destroyOnlyOneSlab(this.level, this.player, pos, adjustedState);
        }
    }
}
