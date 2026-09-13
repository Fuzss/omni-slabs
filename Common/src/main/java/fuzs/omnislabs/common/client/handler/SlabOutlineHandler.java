package fuzs.omnislabs.common.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.omnislabs.common.attachment.SyncedSlabSettings;
import fuzs.omnislabs.common.init.ModRegistry;
import fuzs.omnislabs.common.world.level.block.RotatedSlabBlock;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SlabOutlineHandler {

    public static EventResult onRenderHighlight(LevelRenderer levelRenderer, Camera camera, GameRenderer gameRenderer, HitResult hitResult, DeltaTracker deltaTracker, PoseStack poseStack, MultiBufferSource multiBufferSource, ClientLevel level) {
        if (hitResult.getType() == HitResult.Type.BLOCK && camera.getEntity() instanceof Player player) {
            BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
            BlockState blockState = player.level().getBlockState(blockPos);
            SyncedSlabSettings syncedSlabSettings = ModRegistry.SYNCED_SLAB_SETTINGS_ATTACHMENT_TYPE.getOrDefault(camera.getEntity(),
                    SyncedSlabSettings.EMPTY);
            SlabType slabType = syncedSlabSettings.getSlabType(player, blockState, blockPos, hitResult.getLocation());
            if (slabType != null) {
                VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.lines());
                Vec3 cameraPosition = camera.getPosition();
                double posX = blockPos.getX() - cameraPosition.x();
                double posY = blockPos.getY() - cameraPosition.y();
                double posZ = blockPos.getZ() - cameraPosition.z();
                // Do not use LevelRenderer::renderHitOutline directly; the Fabric callback runs from leading to unexpected behavior.
                VoxelShape voxelShape = blockState.setValue(RotatedSlabBlock.TYPE, slabType)
                        .getShape(player.level(), blockPos, CollisionContext.of(player));
                LevelRenderer.renderShape(poseStack,
                        vertexConsumer,
                        voxelShape,
                        posX,
                        posY,
                        posZ,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.4F);

                return EventResult.INTERRUPT;
            }
        }

        return EventResult.PASS;
    }
}
