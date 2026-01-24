package fuzs.omnislabs.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import fuzs.omnislabs.client.handler.BlockDestroyingHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiPlayerGameMode.class)
abstract class MultiPlayerGameModeMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @ModifyReturnValue(method = "sameDestroyTarget", at = @At("RETURN"))
    private boolean sameDestroyTarget(boolean sameDestroyTarget, BlockPos blockPos) {
        return sameDestroyTarget && BlockDestroyingHandler.isSameDestroyTarget(blockPos,
                this.minecraft.player,
                this.minecraft.level,
                this.minecraft.hitResult);
    }
}
