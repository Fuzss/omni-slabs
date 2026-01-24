package fuzs.omnislabs.world.level.block;

import com.mojang.serialization.MapCodec;
import fuzs.omnislabs.OmniSlabs;
import fuzs.omnislabs.attachment.SyncedSlabSettings;
import fuzs.omnislabs.config.ServerConfig;
import fuzs.omnislabs.handler.BlockConversionHandler;
import fuzs.omnislabs.init.ModRegistry;
import fuzs.omnislabs.util.SlabTypeHelper;
import fuzs.puzzleslib.api.util.v1.ShapesHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class RotatedSlabBlock extends SlabBlock {
    public static final MapCodec<RotatedSlabBlock> CODEC = simpleCodec(RotatedSlabBlock::new);
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    private static final Map<Direction, VoxelShape> SHAPES = ShapesHelper.rotate(SlabBlock.SHAPE_TOP);

    public RotatedSlabBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.Y));
    }

    @Override
    public MapCodec<? extends SlabBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos blockPos, CollisionContext context) {
        SlabType slabType = blockState.getValue(TYPE);
        if (slabType == SlabType.DOUBLE) {
            return Shapes.block();
        } else {
            Direction.Axis axis = blockState.getValue(AXIS);
            Direction.AxisDirection axisDirection =
                    slabType == SlabType.TOP ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
            return SHAPES.get(Direction.fromAxisAndDirection(axis, axisDirection));
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);
        if (blockState.is(this) || !blockState.isAir()) {
            return super.getStateForPlacement(context);
        } else {
            FluidState fluidState = level.getFluidState(blockPos);
            BlockState newBlockState = this.defaultBlockState()
                    .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
            Direction direction = context.getClickedFace();
            Direction.Axis axis = direction.getAxis();
            if (context.getPlayer() != null
                    && ModRegistry.SYNCED_SLAB_SETTINGS_ATTACHMENT_TYPE.getOrDefault(context.getPlayer(),
                    SyncedSlabSettings.EMPTY).precisePlacement().supportsAction(context.getPlayer())) {
                Vec3 vec3 = context.getClickLocation()
                        .subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ())
                        .subtract(0.5, 0.5, 0.5);
                Direction placementDirection = Direction.getApproximateNearest(axis != Direction.Axis.X ? vec3.x : 0.0,
                        axis != Direction.Axis.Y ? vec3.y : 0.0,
                        axis != Direction.Axis.Z ? vec3.z : 0.0);
                return newBlockState.setValue(AXIS, placementDirection.getAxis())
                        .setValue(TYPE,
                                placementDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE ?
                                        SlabType.TOP : SlabType.BOTTOM);
            } else {
                BlockState neighborBlockPos = level.getBlockState(blockPos.relative(direction.getOpposite()));
                if (OmniSlabs.CONFIG.get(ServerConfig.class).copyNeighborSlabOrientation
                        && neighborBlockPos.getBlock() instanceof RotatedSlabBlock
                        && neighborBlockPos.getValue(TYPE) != SlabType.DOUBLE
                        && axis != neighborBlockPos.getValue(AXIS)) {
                    return newBlockState.setValue(AXIS, neighborBlockPos.getValue(AXIS))
                            .setValue(TYPE, neighborBlockPos.getValue(TYPE));
                } else {
                    return newBlockState.setValue(AXIS, axis)
                            .setValue(TYPE,
                                    direction.getAxisDirection() != Direction.AxisDirection.POSITIVE ? SlabType.TOP :
                                            SlabType.BOTTOM);
                }
            }
        }
    }

    @Override
    public boolean canBeReplaced(BlockState blockState, BlockPlaceContext context) {
        ItemStack itemInHand = context.getItemInHand();
        SlabType slabType = blockState.getValue(TYPE);
        if (slabType != SlabType.DOUBLE && itemInHand.is(this.asItem())) {
            if (context.replacingClickedOnBlock()) {
                Direction.Axis axis = blockState.getValue(AXIS);
                boolean bl = context.getClickLocation().get(axis) - context.getClickedPos().get(axis) > 0.5;
                Direction direction = context.getClickedFace();
                if (slabType == SlabType.BOTTOM) {
                    return direction.getAxis() == axis
                            && direction.getAxisDirection() == Direction.AxisDirection.POSITIVE
                            || bl && direction.getAxis() != axis;
                } else {
                    return direction.getAxis() == axis
                            && direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE
                            || !bl && direction.getAxis() != axis;
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * The slab loot table usually uses
     * {@link net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition} to check for the
     * specific slab block when deciding on the number of slabs to drop. So, we pass the original block state to the
     * super call which handles the drops.
     */
    @Override
    public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
        BlockState originalBlockState = BlockConversionHandler.getBlockConversions()
                .inverse()
                .getOrDefault(blockState.getBlock(), blockState.getBlock())
                .withPropertiesOf(blockState);
        SyncedSlabSettings syncedSlabSettings = ModRegistry.SYNCED_SLAB_SETTINGS_ATTACHMENT_TYPE.getOrDefault(player,
                SyncedSlabSettings.EMPTY);
        SlabType slabType = syncedSlabSettings.getSlabType(player, blockState, blockPos);
        if (slabType != null) {
            originalBlockState = originalBlockState.setValue(SlabBlock.TYPE, slabType);
        }

        super.playerDestroy(level, player, blockPos, originalBlockState, blockEntity, itemStack);
    }

    /**
     * @see TurtleEggBlock#decreaseEggs(Level, BlockPos, BlockState)
     */
    public void destroyOnlyOneSlab(Level level, Player player, BlockPos blockPos, BlockState blockState) {
        SyncedSlabSettings syncedSlabSettings = ModRegistry.SYNCED_SLAB_SETTINGS_ATTACHMENT_TYPE.getOrDefault(player,
                SyncedSlabSettings.EMPTY);
        SlabType slabType = syncedSlabSettings.getSlabType(player, blockState, blockPos);
        if (slabType != null) {
            BlockState remainingBlockState = blockState.setValue(RotatedSlabBlock.TYPE,
                    SlabTypeHelper.flipSlabType(slabType));
            int updateFlags = level.isClientSide() ? Block.UPDATE_ALL_IMMEDIATE : Block.UPDATE_CLIENTS;
            level.setBlock(blockPos, remainingBlockState, updateFlags);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AXIS);
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        if (rotation == Rotation.CLOCKWISE_180 && blockState.getValue(AXIS).isHorizontal()) {
            return blockState.setValue(TYPE, SlabTypeHelper.flipSlabType(blockState.getValue(TYPE)));
        } else {
            return RotatedPillarBlock.rotatePillar(blockState, rotation);
        }
    }

    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT:
                if (blockState.getValue(AXIS) == Direction.Axis.Z) {
                    return blockState.rotate(Rotation.CLOCKWISE_180);
                }
            case FRONT_BACK:
                if (blockState.getValue(AXIS) == Direction.Axis.X) {
                    return blockState.rotate(Rotation.CLOCKWISE_180);
                }
        }

        return super.mirror(blockState, mirror);
    }
}
