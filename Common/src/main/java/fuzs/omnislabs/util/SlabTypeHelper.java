package fuzs.omnislabs.util;

import fuzs.omnislabs.attachment.SyncedSlabSettings;
import fuzs.omnislabs.init.ModRegistry;
import fuzs.omnislabs.world.level.block.RotatedSlabBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class SlabTypeHelper {

    public static SlabType flipSlabType(SlabType slabType) {
        if (slabType != SlabType.DOUBLE) {
            if (slabType == SlabType.TOP) {
                return SlabType.BOTTOM;
            } else {
                return SlabType.TOP;
            }
        } else {
            return SlabType.DOUBLE;
        }
    }

    @Nullable
    public static SlabType getSlabType(Player player, BlockState blockState, BlockPos blockPos, Vec3 hitVector) {
        if (ModRegistry.SYNCED_SLAB_SETTINGS_ATTACHMENT_TYPE.getOrDefault(player, SyncedSlabSettings.EMPTY)
                .preciseDestruction()
                .supportsAction(player)) {
            if (blockState.getBlock() instanceof RotatedSlabBlock
                    && blockState.getValue(RotatedSlabBlock.TYPE) == SlabType.DOUBLE) {
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
