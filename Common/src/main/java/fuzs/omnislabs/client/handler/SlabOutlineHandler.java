package fuzs.omnislabs.client.handler;

import fuzs.omnislabs.util.SlabTypeHelper;
import fuzs.omnislabs.world.level.block.RotatedSlabBlock;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SlabOutlineHandler {

    public static EventResultHolder<VoxelShape> onExtractBlockOutline(ClientLevel clientLevel, BlockPos blockPos, BlockState blockState, BlockHitResult hitResult, CollisionContext collisionContext) {
        Player player = Minecraft.getInstance().player;
        SlabType slabType = SlabTypeHelper.getSlabType(player, blockState, blockPos, hitResult.getLocation());
        if (slabType != null) {
            VoxelShape voxelShape = blockState.setValue(RotatedSlabBlock.TYPE, slabType)
                    .getShape(clientLevel, blockPos, collisionContext);
            return EventResultHolder.interrupt(voxelShape);
        } else {
            return EventResultHolder.pass();
        }
    }
}
