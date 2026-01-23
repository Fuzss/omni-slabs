package fuzs.omnislabs.client.handler;

import fuzs.omnislabs.OmniSlabs;
import fuzs.omnislabs.attachment.SyncedSlabSettings;
import fuzs.omnislabs.config.ClientConfig;
import fuzs.omnislabs.config.SlabActionType;
import fuzs.omnislabs.init.ModRegistry;
import fuzs.omnislabs.network.client.ServerboundHitVectorMessage;
import fuzs.omnislabs.network.client.ServerboundSlabPlacementMessage;
import fuzs.omnislabs.util.SlabTypeHelper;
import fuzs.omnislabs.world.level.block.RotatedSlabBlock;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.network.v4.MessageSender;
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

    public static EventResult onAttackBlock(Player player, Level level, InteractionHand interactionHand, BlockPos blockPos, Direction direction) {
        Minecraft minecraft = Minecraft.getInstance();
        SyncedSlabSettings capability = ModRegistry.SYNCED_SLAB_SETTINGS_ATTACHMENT_TYPE.getOrDefault(player,
                SyncedSlabSettings.EMPTY);
        capability.setHitVector(minecraft.hitResult.getLocation());
        MessageSender.broadcast(new ServerboundHitVectorMessage(capability.hitVector()));
        destroyBlockPos = blockPos;
        return EventResult.PASS;
    }

    @Nullable
    public static SlabType getSlabType(Player player, BlockState blockState) {
        return destroyBlockPos != null ? SlabTypeHelper.getSlabType(player,
                blockState,
                destroyBlockPos,
                ModRegistry.SYNCED_SLAB_SETTINGS_ATTACHMENT_TYPE.getOrDefault(player, SyncedSlabSettings.EMPTY)
                        .hitVector()) : null;
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

    public static BlockState getBreakingTextureBlockState(BlockState blockState, BlockPos blockPos) {
        if (blockState.getRenderShape() == RenderShape.MODEL) {
            SlabType slabType = getSlabTypeAt(blockState, blockPos);
            if (slabType != null) {
                return blockState.setValue(RotatedSlabBlock.TYPE, slabType);
            }
        }

        return blockState;
    }

    public static void onPlayerJoin(LocalPlayer player, MultiPlayerGameMode multiPlayerGameMode, Connection connection) {
        syncPreciseSlabPlacement(player);
    }

    public static void syncPreciseSlabPlacement(Player player) {
        SlabActionType precisePlacement = OmniSlabs.CONFIG.get(ClientConfig.class).precisePlacement;
        SlabActionType preciseDestruction = OmniSlabs.CONFIG.get(ClientConfig.class).preciseDestruction;
        ModRegistry.SYNCED_SLAB_SETTINGS_ATTACHMENT_TYPE.getOrDefault(player, SyncedSlabSettings.EMPTY)
                .setActionSettings(precisePlacement, preciseDestruction);
        MessageSender.broadcast(new ServerboundSlabPlacementMessage(precisePlacement, preciseDestruction));
    }
}
