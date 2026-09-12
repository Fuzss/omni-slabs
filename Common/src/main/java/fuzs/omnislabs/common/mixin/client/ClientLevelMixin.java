package fuzs.omnislabs.common.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import fuzs.omnislabs.common.client.handler.BlockDestroyingHandler;
import fuzs.omnislabs.common.world.level.block.RotatedSlabBlock;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
abstract class ClientLevelMixin extends Level {

    protected ClientLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData,
                dimension,
                registryAccess,
                dimensionTypeRegistration,
                profiler,
                isClientSide,
                isDebug,
                biomeZoomSeed,
                maxChainedNeighborUpdates);
    }

    @ModifyVariable(method = "addDestroyBlockEffect", at = @At("HEAD"), argsOnly = true)
    public BlockState addDestroyBlockEffect(BlockState blockState, @Local(argsOnly = true) BlockPos blockPos) {
        SlabType slabType = BlockDestroyingHandler.getSlabTypeAt(blockState, blockPos);
        return slabType != null ? blockState.setValue(RotatedSlabBlock.TYPE, slabType) : blockState;
    }
}
