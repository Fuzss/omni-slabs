package fuzs.verticalslabs.client.extensions;

import fuzs.verticalslabs.client.handler.BlockDestroyingHandler;
import fuzs.verticalslabs.world.level.block.RotatedSlabBlock;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;

public class RotatedSlabBlockExtensions implements IClientBlockExtensions {

    @Override
    public boolean addHitEffects(BlockState blockState, Level level, HitResult target, ParticleEngine manager) {
        BlockHitResult blockHitResult = (BlockHitResult) target;
        BlockPos blockPos = blockHitResult.getBlockPos();
        SlabType slabType = BlockDestroyingHandler.getSlabTypeAt(blockState, blockPos);
        if (slabType != null) {
            blockState = blockState.setValue(RotatedSlabBlock.TYPE, slabType);
            this.crack(blockState, blockPos, blockHitResult.getDirection(), manager, level);
            return true;
        }
        return false;
    }

    public void crack(BlockState blockState, BlockPos pos, Direction side, ParticleEngine manager, Level level) {
        if (blockState.getRenderShape() != RenderShape.INVISIBLE) {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            float f = 0.1F;
            AABB aABB = blockState.getShape(level, pos).bounds();
            RandomSource random = level.random;
            double d = i + random.nextDouble() * (aABB.maxX - aABB.minX - 0.2) + 0.1 + aABB.minX;
            double e = j + random.nextDouble() * (aABB.maxY - aABB.minY - 0.2) + 0.1 + aABB.minY;
            double g = k + random.nextDouble() * (aABB.maxZ - aABB.minZ - 0.2) + 0.1 + aABB.minZ;
            if (side == Direction.DOWN) {
                e = j + aABB.minY - 0.1;
            }

            if (side == Direction.UP) {
                e = j + aABB.maxY + 0.1;
            }

            if (side == Direction.NORTH) {
                g = k + aABB.minZ - 0.1;
            }

            if (side == Direction.SOUTH) {
                g = k + aABB.maxZ + 0.1;
            }

            if (side == Direction.WEST) {
                d = i + aABB.minX - 0.1;
            }

            if (side == Direction.EAST) {
                d = i + aABB.maxX + 0.1;
            }

            manager.add((new TerrainParticle((ClientLevel) level, d, e, g, 0.0, 0.0, 0.0, blockState, pos)).setPower(0.2F).scale(0.6F));
        }
    }
}
