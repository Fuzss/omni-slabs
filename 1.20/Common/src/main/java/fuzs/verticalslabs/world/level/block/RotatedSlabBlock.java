package fuzs.verticalslabs.world.level.block;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import fuzs.puzzleslib.api.shapes.v1.ShapesHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RotatedSlabBlock extends SlabBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    protected static final Map<Direction, VoxelShape> SHAPES = ShapesHelper.rotate(SlabBlock.TOP_AABB);
    private static final BiMap<Direction, SlabTypeKey> DIRECTIONS = Stream.of(Direction.values()).collect(Collectors.toMap(Function.identity(), SlabTypeKey::new, (o1, o2) -> o1, HashBiMap::create));

    private final Block block;

    public RotatedSlabBlock(Block block) {
        super(BlockBehaviour.Properties.copy(block).dropsLike(block));
        this.block = block;
        this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.Y));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = context.getLevel().getBlockState(blockPos);
        if (blockState.is(this)) {
            return blockState.setValue(TYPE, SlabType.DOUBLE).setValue(WATERLOGGED, false);
        } else {
            FluidState fluidState = context.getLevel().getFluidState(blockPos);
            BlockState blockState2 = this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
            if (context.isSecondaryUseActive()) {
                Direction clickedFace = context.getClickedFace();
                Direction.Axis axis = clickedFace.getAxis();
                if (axis.isHorizontal()) {
                    axis = Direction.Axis.Y;
                } else {
                    boolean good = false;
                    for (Direction nearestLookingDirection : context.getNearestLookingDirections()) {
                        if (nearestLookingDirection.getAxis().isHorizontal()) {
                            axis = nearestLookingDirection.getAxis();
                            axis = axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
                            good = true;
                            break;
                        }
                    }
                    if (!good) throw new RuntimeException();
                }
                blockState2 = blockState2.setValue(AXIS, axis);
                if (context.getClickLocation().get(axis) - blockPos.get(axis) > 0.5) {
                    return blockState2.setValue(TYPE, SlabType.TOP);
                } else {
                    return blockState2.setValue(TYPE, SlabType.BOTTOM);
                }
            } else {
                Direction clickedFace = context.getClickedFace().getOpposite();
                BlockPos relative = blockPos.relative(clickedFace);
                BlockState blockState1 = context.getLevel().getBlockState(relative);
                if (blockState1.getBlock() instanceof RotatedSlabBlock && blockState1.getValue(TYPE) != SlabType.DOUBLE && clickedFace.getAxis() != blockState1.getValue(AXIS)) {
                    return blockState2.setValue(AXIS, blockState1.getValue(AXIS)).setValue(TYPE, blockState1.getValue(TYPE));
                } else {
                    return blockState2.setValue(AXIS, clickedFace.getAxis()).setValue(TYPE, clickedFace.getAxisDirection() == Direction.AxisDirection.POSITIVE ? SlabType.TOP : SlabType.BOTTOM);
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
                    return direction.getAxis() == axis && direction.getAxisDirection() == Direction.AxisDirection.POSITIVE || bl && direction.getAxis() != axis;
                } else {
                    return direction.getAxis() == axis && direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE || !bl && direction.getAxis() != axis;
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
            Direction.AxisDirection axisDirection = slabType == SlabType.TOP ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
            return SHAPES.get(DIRECTIONS.inverse().get(new SlabTypeKey(axis, axisDirection)));
        }
    }

    private record SlabTypeKey(Direction.Axis axis, Direction.AxisDirection axisDirection) {

        public SlabTypeKey(Direction direction) {
            this(direction.getAxis(), direction.getAxisDirection());
        }

        public boolean is(Direction direction) {
            return direction.getAxis() == this.axis && direction.getAxisDirection() == this.axisDirection;
        }
    }
}
