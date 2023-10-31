package fuzs.verticalslabs.client.handler;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.verticalslabs.VerticalSlabs;
import fuzs.verticalslabs.network.client.ServerboundHitVectorMessage;
import fuzs.verticalslabs.util.SlabTypeHelper;
import fuzs.verticalslabs.world.level.block.RotatedSlabBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BlockDestroyingHandler {
    private static Vec3 destroyHitVector;
    private static BlockPos destroyBlockPos = new BlockPos(-1, -1, -1);

    public static EventResult onAttackBlock(Player player, Level level, InteractionHand interactionHand, BlockPos pos, Direction direction) {
        Minecraft minecraft = Minecraft.getInstance();
        destroyHitVector = minecraft.hitResult.getLocation();
        VerticalSlabs.NETWORK.sendToServer(new ServerboundHitVectorMessage(destroyHitVector));
        destroyBlockPos = pos;
        return EventResult.PASS;
    }

    @Nullable
    public static SlabType getSlabType(Player player, BlockState blockState) {
        return SlabTypeHelper.getSlabType(player, blockState, destroyBlockPos, destroyHitVector);
    }

    @Nullable
    public static SlabType getSlabTypeAt(BlockState blockState, BlockPos blockPos) {
        if (destroyBlockPos.equals(blockPos)) {
            Minecraft minecraft = Minecraft.getInstance();
            return getSlabType(minecraft.player, blockState);
        }
        return null;
    }

    public static BlockState getBreakingTextureBlockState(BlockState blockState, BlockPos pos) {
        if (blockState.getRenderShape() == RenderShape.MODEL) {
            SlabType slabType = getSlabTypeAt(blockState, pos);
            if (slabType != null) {
                blockState = blockState.setValue(RotatedSlabBlock.TYPE, slabType);
            }
        }
        return blockState;
    }
}
