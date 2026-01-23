package fuzs.omnislabs.handler;

import fuzs.omnislabs.attachment.SyncedSlabSettings;
import fuzs.omnislabs.init.ModRegistry;
import fuzs.omnislabs.util.SlabTypeHelper;
import fuzs.omnislabs.world.level.block.RotatedSlabBlock;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

public class ServerBreakSlabHandler {

    public static EventResult onBreakBlock(ServerLevel serverLevel, BlockPos blockPos, BlockState blockState, ServerPlayer serverPlayer, ItemStack itemInHand) {
        SyncedSlabSettings capability = ModRegistry.SYNCED_SLAB_SETTINGS_ATTACHMENT_TYPE.getOrDefault(serverPlayer,
                SyncedSlabSettings.EMPTY);
        SlabType slabType = SlabTypeHelper.getSlabType(serverPlayer, blockState, blockPos, capability.hitVector());
        if (slabType != null) {
            BlockState brokenBlockState = blockState.setValue(RotatedSlabBlock.TYPE, slabType);
            destroyBlock(serverLevel, blockPos, brokenBlockState, serverPlayer);
            BlockState newBlockState = blockState.setValue(RotatedSlabBlock.TYPE,
                    SlabTypeHelper.flipSlabType(slabType));
            serverLevel.setBlockAndUpdate(blockPos, newBlockState);
            return EventResult.INTERRUPT;
        } else {
            return EventResult.PASS;
        }
    }

    private static void destroyBlock(ServerLevel level, BlockPos pos, BlockState blockState, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        Block block = blockState.getBlock();
        block.playerWillDestroy(level, pos, blockState, player);
        boolean removeBlock = level.removeBlock(pos, false);
        if (removeBlock) {
            block.destroy(level, pos, blockState);
        }

        if (!player.isCreative()) {
            ItemStack itemStack = player.getMainHandItem();
            ItemStack itemStack2 = itemStack.copy();
            boolean hasCorrectToolForDrops = player.hasCorrectToolForDrops(blockState);
            itemStack.mineBlock(level, blockState, pos, player);
            if (removeBlock && hasCorrectToolForDrops) {
                block.playerDestroy(level, player, pos, blockState, blockEntity, itemStack2);
            }
        }
    }
}
