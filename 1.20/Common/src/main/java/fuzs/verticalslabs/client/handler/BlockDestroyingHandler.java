package fuzs.verticalslabs.client.handler;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.jetbrains.annotations.Nullable;

public class BlockDestroyingHandler {
    @Nullable
    private static SlabType lastSlabType;

    public static SlabType getLastSlabType() {
        return lastSlabType;
    }

    public static EventResult onAttackBlock(Player player, Level level, InteractionHand interactionHand, BlockPos pos, Direction direction) {
        if (!player.isCreative()) lastSlabType = SlabOutlineHandler.getSlabType(level.getBlockState(pos));
        return EventResult.PASS;
    }
}
