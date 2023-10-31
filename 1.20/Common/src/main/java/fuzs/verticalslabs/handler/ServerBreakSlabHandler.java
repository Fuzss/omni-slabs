package fuzs.verticalslabs.handler;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.verticalslabs.capability.HitVectorCapability;
import fuzs.verticalslabs.init.ModRegistry;
import fuzs.verticalslabs.util.SlabTypeHelper;
import fuzs.verticalslabs.world.level.block.RotatedSlabBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

public class ServerBreakSlabHandler {

    public static EventResult onBreakBlock(ServerLevel level, BlockPos pos, BlockState blockState, Player player, ItemStack itemInHand) {
        HitVectorCapability capability = ModRegistry.HIT_VECTOR_CAPABILITY.get(player);
        SlabType slabType = SlabTypeHelper.getSlabType(player, blockState, pos, capability.getHitVector());
        if (slabType != null) {
            BlockState brokenBlockState = blockState.setValue(RotatedSlabBlock.TYPE, slabType);
            destroyBlock(level, pos, brokenBlockState, player);
            BlockState newBlockState = blockState.setValue(RotatedSlabBlock.TYPE, SlabTypeHelper.flipSlabType(slabType));
            level.setBlock(pos, newBlockState, 3);
            return EventResult.INTERRUPT;
        }
        return EventResult.PASS;
    }

    private static void destroyBlock(ServerLevel level, BlockPos pos, BlockState blockState, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        Block block = blockState.getBlock();
        block.playerWillDestroy(level, pos, blockState, player);
        boolean bl = level.removeBlock(pos, false);
        if (bl) {
            block.destroy(level, pos, blockState);
        }
        if (!player.isCreative()) {
            ItemStack itemStack = player.getMainHandItem();
            ItemStack itemStack2 = itemStack.copy();
            boolean bl2 = player.hasCorrectToolForDrops(blockState);
            itemStack.mineBlock(level, blockState, pos, player);
            if (bl && bl2) {
                block.playerDestroy(level, player, pos, blockState, blockEntity, itemStack2);
            }
        }
    }
}
