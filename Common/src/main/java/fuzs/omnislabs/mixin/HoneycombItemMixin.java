package fuzs.omnislabs.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fuzs.omnislabs.handler.BlockConversionHandler;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(HoneycombItem.class)
abstract class HoneycombItemMixin extends Item {

    public HoneycombItemMixin(Properties properties) {
        super(properties);
    }

    @ModifyVariable(method = "getWaxed", at = @At("HEAD"), argsOnly = true)
    private static BlockState getWaxed(BlockState blockState, @Share("originalBlockState") LocalRef<BlockState> originalBlockState) {
        if (BlockConversionHandler.getBlockConversions().containsValue(blockState.getBlock())) {
            originalBlockState.set(blockState);
            return BlockConversionHandler.getBlockConversions()
                    .inverse()
                    .get(blockState.getBlock())
                    .withPropertiesOf(blockState);
        } else {
            return blockState;
        }
    }

    @ModifyReturnValue(method = "getWaxed", at = @At("RETURN"))
    private static Optional<BlockState> getWaxed(Optional<BlockState> optional, @Share("originalBlockState") LocalRef<BlockState> originalBlockState) {
        return optional.map((BlockState blockState) -> {
            if (originalBlockState.get() != null && BlockConversionHandler.getBlockConversions()
                    .containsKey(blockState.getBlock())) {
                return BlockConversionHandler.getBlockConversions()
                        .get(blockState.getBlock())
                        .withPropertiesOf(originalBlockState.get());
            } else {
                return blockState;
            }
        });
    }
}
