package fuzs.verticalslabs.client.handler;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.verticalslabs.VerticalSlabs;
import fuzs.verticalslabs.capability.HitVectorCapability;
import fuzs.verticalslabs.config.ClientConfig;
import fuzs.verticalslabs.config.SlabActionType;
import fuzs.verticalslabs.init.ModRegistry;
import fuzs.verticalslabs.network.client.ServerboundHitVectorMessage;
import fuzs.verticalslabs.network.client.ServerboundSlabPlacementMessage;
import fuzs.verticalslabs.util.SlabTypeHelper;
import fuzs.verticalslabs.world.level.block.RotatedSlabBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.Connection;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.jetbrains.annotations.Nullable;

public class BlockDestroyingHandler {
    private static BlockPos destroyBlockPos = new BlockPos(-1, -1, -1);

    public static EventResult onAttackBlock(Player player, Level level, InteractionHand interactionHand, BlockPos pos, Direction direction) {
        Minecraft minecraft = Minecraft.getInstance();
        HitVectorCapability capability = ModRegistry.HIT_VECTOR_CAPABILITY.get(player);
        capability.setHitVector(minecraft.hitResult.getLocation());
        VerticalSlabs.NETWORK.sendToServer(new ServerboundHitVectorMessage(capability.getHitVector()));
        destroyBlockPos = pos;
        return EventResult.PASS;
    }

    @Nullable
    public static SlabType getSlabType(Player player, BlockState blockState) {
        return destroyBlockPos != null ? SlabTypeHelper.getSlabType(player,
                blockState,
                destroyBlockPos,
                ModRegistry.HIT_VECTOR_CAPABILITY.get(player).getHitVector()) : null;
    }

    @Nullable
    public static SlabType getSlabTypeAt(BlockState blockState, BlockPos blockPos) {
        if (blockPos.equals(destroyBlockPos)) {
            Minecraft minecraft = Minecraft.getInstance();
            return getSlabType(minecraft.player, blockState);
        } else {
            return null;
        }
    }

    public static BlockState getBreakingTextureBlockState(BlockState blockState, BlockPos pos) {
        if (blockState.getRenderShape() == RenderShape.MODEL) {
            SlabType slabType = getSlabTypeAt(blockState, pos);
            if (slabType != null) {
                return blockState.setValue(RotatedSlabBlock.TYPE, slabType);
            }
        }

        return blockState;
    }

    public static void onLoggedIn(LocalPlayer player, MultiPlayerGameMode multiPlayerGameMode, Connection connection) {
        syncPreciseSlabPlacement(player);
    }

    public static void syncPreciseSlabPlacement(Player player) {
        SlabActionType precisePlacement = VerticalSlabs.CONFIG.get(ClientConfig.class).precisePlacement;
        SlabActionType preciseDestruction = VerticalSlabs.CONFIG.get(ClientConfig.class).preciseDestruction;
        ModRegistry.HIT_VECTOR_CAPABILITY.get(player).setActionSettings(precisePlacement, preciseDestruction);
        VerticalSlabs.NETWORK.sendToServer(new ServerboundSlabPlacementMessage(precisePlacement, preciseDestruction));
    }
}
