package fuzs.omnislabs.fabric.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import fuzs.omnislabs.common.world.level.block.RotatedSlabBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
abstract class MultiPlayerGameModeFabricMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "destroyBlock",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/world/level/block/Block;destroy(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    public void destroyBlock(BlockPos pos, CallbackInfoReturnable<Boolean> callback, @Local BlockState oldState) {
        if (oldState.getBlock() instanceof RotatedSlabBlock slabBlock) {
            slabBlock.destroyOnlyOneSlab(this.minecraft.level, this.minecraft.player, pos, oldState);
        }
    }
}
