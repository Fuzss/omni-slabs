package fuzs.omnislabs.neoforge.services;

import fuzs.omnislabs.services.CommonAbstractions;
import fuzs.omnislabs.world.level.block.RotatedSlabBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public final class NeoForgeAbstractions implements CommonAbstractions {
    @Override
    public Block createRotatedSlabBlock(BlockBehaviour.Properties properties) {
        return new RotatedSlabBlock(properties) {
            @Override
            public boolean onDestroyedByPlayer(BlockState blockState, Level level, BlockPos blockPos, Player player, ItemStack itemStack, boolean willHarvest, FluidState fluidState) {
                boolean destroyedByPlayer = super.onDestroyedByPlayer(blockState,
                        level,
                        blockPos,
                        player,
                        itemStack,
                        willHarvest,
                        fluidState);
                if (destroyedByPlayer) {
                    this.destroyOnlyOneSlab(level, player, blockPos, blockState);
                    return true;
                } else {
                    return false;
                }
            }
        };
    }
}
