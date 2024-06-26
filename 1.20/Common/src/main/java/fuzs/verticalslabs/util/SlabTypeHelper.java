package fuzs.verticalslabs.util;

import fuzs.verticalslabs.init.ModRegistry;
import fuzs.verticalslabs.world.level.block.RotatedSlabBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SlabTypeHelper {

    public static SlabType flipSlabType(SlabType slabType) {
        return slabType != SlabType.DOUBLE ? slabType == SlabType.TOP ? SlabType.BOTTOM : SlabType.TOP : SlabType.DOUBLE;
    }

    @Nullable
    public static SlabType getSlabType(Player player, BlockState blockState, BlockPos blockPos, Vec3 hitVector) {
        if (ModRegistry.HIT_VECTOR_CAPABILITY.get(player).isPlacementPrecise()) {
            if (blockState.getBlock() instanceof RotatedSlabBlock && blockState.getValue(RotatedSlabBlock.TYPE) == SlabType.DOUBLE) {
                Direction.Axis axis = blockState.getValue(RotatedSlabBlock.AXIS);
                if (hitVector.get(axis) - blockPos.get(axis) > 0.5) {
                    return SlabType.TOP;
                } else {
                    return SlabType.BOTTOM;
                }
            }
        }
        return null;
    }
}
