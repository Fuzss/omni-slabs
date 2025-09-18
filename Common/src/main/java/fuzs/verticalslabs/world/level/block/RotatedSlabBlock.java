package fuzs.verticalslabs.world.level.block;

import fuzs.puzzleslib.api.shapes.v1.ShapesHelper;
import fuzs.verticalslabs.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
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
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class RotatedSlabBlock extends SlabBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    protected static final Map<Direction, VoxelShape> SHAPES = ShapesHelper.rotate(SlabBlock.TOP_AABB);

    private final Block block;

    public RotatedSlabBlock(Block block) {
        super(BlockBehaviour.Properties.copy(block).dropsLike(block));
        this.block = block;
        this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.Y));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);
        if (blockState.is(this) || !blockState.isAir()) {
            return super.getStateForPlacement(context);
        } else {
            FluidState fluidState = level.getFluidState(blockPos);
            BlockState newBlockState = this.defaultBlockState()
                    .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
            Direction clickedFace = context.getClickedFace();
            Direction.Axis axis = clickedFace.getAxis();
            if (context.getPlayer() != null
                    && ModRegistry.HIT_VECTOR_CAPABILITY.get(context.getPlayer()).precisePlacement.supportsAction(
                    context.getPlayer())) {
                Vec3 vec3 = context.getClickLocation()
                        .subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ())
                        .subtract(0.5, 0.5, 0.5);
                Direction direction = Direction.getNearest(axis != Direction.Axis.X ? vec3.x : 0.0,
                        axis != Direction.Axis.Y ? vec3.y : 0.0,
                        axis != Direction.Axis.Z ? vec3.z : 0.0);
                return newBlockState.setValue(AXIS, direction.getAxis())
                        .setValue(TYPE,
                                direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? SlabType.TOP :
                                        SlabType.BOTTOM);
            } else {
                clickedFace = clickedFace.getOpposite();
                BlockState neighborBlockPos = level.getBlockState(blockPos.relative(clickedFace));
                if (neighborBlockPos.getBlock() instanceof RotatedSlabBlock
                        && neighborBlockPos.getValue(TYPE) != SlabType.DOUBLE
                        && clickedFace.getAxis() != neighborBlockPos.getValue(AXIS)) {
                    return newBlockState.setValue(AXIS, neighborBlockPos.getValue(AXIS))
                            .setValue(TYPE, neighborBlockPos.getValue(TYPE));
                } else {
                    return newBlockState.setValue(AXIS, clickedFace.getAxis())
                            .setValue(TYPE,
                                    clickedFace.getAxisDirection() == Direction.AxisDirection.POSITIVE ? SlabType.TOP :
                                            SlabType.BOTTOM);
                }
            }
        }
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        ItemStack itemStack = useContext.getItemInHand();
        SlabType slabType = state.getValue(TYPE);
        if (slabType != SlabType.DOUBLE && itemStack.is(this.asItem())) {
            if (useContext.replacingClickedOnBlock()) {
                Direction.Axis axis = state.getValue(AXIS);
                boolean bl = useContext.getClickLocation().get(axis) - useContext.getClickedPos().get(axis) > 0.5;
                Direction direction = useContext.getClickedFace();
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

    @Override
    public String getDescriptionId() {
        return this.block.getDescriptionId();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AXIS);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        SlabType slabType = state.getValue(TYPE);
        if (slabType == SlabType.DOUBLE) {
            return Shapes.block();
        } else {
            Direction.Axis axis = state.getValue(AXIS);
            Direction.AxisDirection axisDirection =
                    slabType == SlabType.TOP ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
            return SHAPES.get(Direction.fromAxisAndDirection(axis, axisDirection));
        }
    }
}
