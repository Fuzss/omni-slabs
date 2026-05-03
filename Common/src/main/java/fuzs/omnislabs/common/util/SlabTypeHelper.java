package fuzs.omnislabs.common.util;

import net.minecraft.world.level.block.state.properties.SlabType;

public class SlabTypeHelper {

    public static SlabType flipSlabType(SlabType slabType) {
        if (slabType != SlabType.DOUBLE) {
            if (slabType == SlabType.TOP) {
                return SlabType.BOTTOM;
            } else {
                return SlabType.TOP;
            }
        } else {
            return slabType;
        }
    }
}
