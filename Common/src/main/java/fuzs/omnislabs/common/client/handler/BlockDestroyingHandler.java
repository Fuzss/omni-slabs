package fuzs.omnislabs.common.client.handler;

import fuzs.omnislabs.common.OmniSlabs;
import fuzs.omnislabs.common.attachment.SyncedSlabSettings;
import fuzs.omnislabs.common.config.ClientConfig;
import fuzs.omnislabs.common.config.SlabActionType;
import fuzs.omnislabs.common.init.ModRegistry;
import fuzs.omnislabs.common.network.client.ServerboundHitVectorMessage;
import fuzs.omnislabs.common.network.client.ServerboundSlabPlacementMessage;
import fuzs.omnislabs.common.world.level.block.RotatedSlabBlock;
import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import fuzs.puzzleslib.common.api.network.v4.MessageSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
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
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class BlockDestroyingHandler {
    @Nullable
    private static BlockPos destroyBlockPos;

    public static EventResult onAttackBlock(Player player, Level level, InteractionHand interactionHand, BlockPos blockPos, Direction direction) {
        if (level.isClientSide()) {
            Minecraft minecraft = Minecraft.getInstance();
            Vec3 hitVector = minecraft.hitResult.getLocation();
            SyncedSlabSettings.setHitVector(player, hitVector);
            MessageSender.broadcast(new ServerboundHitVectorMessage(hitVector));
            destroyBlockPos = blockPos;
        }

        return EventResult.PASS;
    }

    public static @Nullable SlabType getSlabType(Player player, BlockState blockState) {
        if (destroyBlockPos != null) {
            SyncedSlabSettings syncedSlabSettings = ModRegistry.SYNCED_SLAB_SETTINGS_ATTACHMENT_TYPE.getOrDefault(player,
                    SyncedSlabSettings.EMPTY);
            return syncedSlabSettings.getSlabType(player, blockState, destroyBlockPos);
        } else {
            return null;
        }
    }

    public static @Nullable SlabType getSlabTypeAt(BlockState blockState, BlockPos blockPos) {
        if (Objects.equals(blockPos, destroyBlockPos)) {
            Minecraft minecraft = Minecraft.getInstance();
            return getSlabType(minecraft.player, blockState);
        } else {
            return null;
        }
    }

    public static BlockState getBreakingTextureBlockState(BlockState blockState, BlockPos blockPos) {
        if (blockState.getRenderShape() == RenderShape.MODEL) {
            SlabType slabType = getSlabTypeAt(blockState, blockPos);
            if (slabType != null) {
                return blockState.setValue(RotatedSlabBlock.TYPE, slabType);
            }
        }

        return blockState;
    }

    public static boolean isSameDestroyTarget(BlockPos blockPos, Player player, ClientLevel clientLevel, HitResult hitResult) {
        BlockState blockState = clientLevel.getBlockState(blockPos);
        SlabType destroySlabType = BlockDestroyingHandler.getSlabType(player, blockState);
        SyncedSlabSettings syncedSlabSettings = ModRegistry.SYNCED_SLAB_SETTINGS_ATTACHMENT_TYPE.getOrDefault(player,
                SyncedSlabSettings.EMPTY);
        SlabType slabType = syncedSlabSettings.getSlabType(player, blockState, blockPos, hitResult.getLocation());
        return slabType == destroySlabType;
    }

    public static void onPlayerJoin(LocalPlayer player, MultiPlayerGameMode multiPlayerGameMode, Connection connection) {
        syncPreciseSlabPlacement(player);
    }

    public static void syncPreciseSlabPlacement(Player player) {
        SlabActionType precisePlacement = OmniSlabs.CONFIG.get(ClientConfig.class).precisePlacement;
        SlabActionType preciseDestruction = OmniSlabs.CONFIG.get(ClientConfig.class).preciseDestruction;
        SyncedSlabSettings.setActionSettings(player, precisePlacement, preciseDestruction);
        MessageSender.broadcast(new ServerboundSlabPlacementMessage(precisePlacement, preciseDestruction));
    }
}
