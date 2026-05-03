package fuzs.omnislabs.common.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import fuzs.omnislabs.common.handler.BlockConversionHandler;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(WeatheringCopper.class)
public interface WeatheringCopperMixin {

    @ModifyVariable(method = {
            "getPrevious(Lnet/minecraft/world/level/block/Block;)Ljava/util/Optional;",
            "getNext(Lnet/minecraft/world/level/block/Block;)Ljava/util/Optional;",
            "getFirst(Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/Block;"
    }, at = @At("HEAD"), argsOnly = true)
    private static Block getPrevious$0(Block block) {
        return BlockConversionHandler.getBlockConversions().inverse().getOrDefault(block, block);
    }

    @ModifyReturnValue(method = {
            "getPrevious(Lnet/minecraft/world/level/block/Block;)Ljava/util/Optional;",
            "getNext(Lnet/minecraft/world/level/block/Block;)Ljava/util/Optional;"
    }, at = @At("RETURN"))
    private static Optional<Block> getPrevious(Optional<Block> optional) {
        return optional.map((Block block) -> BlockConversionHandler.getBlockConversions().getOrDefault(block, block));
    }

    @ModifyReturnValue(method = "getFirst(Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/Block;",
                       at = @At("RETURN"))
    private static Block getPrevious$1(Block block) {
        return BlockConversionHandler.getBlockConversions().getOrDefault(block, block);
    }
}
