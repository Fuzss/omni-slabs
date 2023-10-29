package fuzs.verticalslabs.world.level.block;

import fuzs.verticalslabs.client.handler.SlabOutlineHandler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;

import java.util.function.Consumer;

public class ForgeRotatedSlabBlock extends RotatedSlabBlock {

    public ForgeRotatedSlabBlock(Block block) {
        super(block);
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer) {
        consumer.accept(new IClientBlockExtensions() {

            @Override
            public boolean addHitEffects(BlockState blockState, Level level, HitResult target, ParticleEngine manager) {
                BlockHitResult blockHitResult = (BlockHitResult) target;
                BlockPos blockPos = blockHitResult.getBlockPos();
                if (blockState.getValue(RotatedSlabBlock.TYPE) == SlabType.DOUBLE) {
                    SlabType slabType = SlabOutlineHandler.getSlabType(blockState);
                    if (slabType != null) {
                        blockState = blockState.setValue(RotatedSlabBlock.TYPE, slabType);
                        this.crack(blockState, blockPos, blockHitResult.getDirection(), manager, level);
                        return true;
                    }
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
                    double d = (double) i + random.nextDouble() * (aABB.maxX - aABB.minX - 0.20000000298023224) + 0.10000000149011612 + aABB.minX;
                    double e = (double) j + random.nextDouble() * (aABB.maxY - aABB.minY - 0.20000000298023224) + 0.10000000149011612 + aABB.minY;
                    double g = (double) k + random.nextDouble() * (aABB.maxZ - aABB.minZ - 0.20000000298023224) + 0.10000000149011612 + aABB.minZ;
                    if (side == Direction.DOWN) {
                        e = (double) j + aABB.minY - 0.10000000149011612;
                    }

                    if (side == Direction.UP) {
                        e = (double) j + aABB.maxY + 0.10000000149011612;
                    }

                    if (side == Direction.NORTH) {
                        g = (double) k + aABB.minZ - 0.10000000149011612;
                    }

                    if (side == Direction.SOUTH) {
                        g = (double) k + aABB.maxZ + 0.10000000149011612;
                    }

                    if (side == Direction.WEST) {
                        d = (double) i + aABB.minX - 0.10000000149011612;
                    }

                    if (side == Direction.EAST) {
                        d = (double) i + aABB.maxX + 0.10000000149011612;
                    }

                    manager.add((new TerrainParticle((ClientLevel) level, d, e, g, 0.0, 0.0, 0.0, blockState, pos)).setPower(0.2F).scale(0.6F));
                }
            }
        });
    }
}
