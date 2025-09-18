package fuzs.verticalslabs.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.verticalslabs.util.SlabTypeHelper;
import fuzs.verticalslabs.world.level.block.RotatedSlabBlock;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SlabOutlineHandler {

    public static EventResult onRenderHighlight(LevelRenderer levelRenderer, Camera camera, GameRenderer gameRenderer, HitResult hitResult, float tickDelta, PoseStack poseStack, MultiBufferSource multiBufferSource, ClientLevel level) {
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
            BlockState blockState = level.getBlockState(blockPos);
            if (level.getWorldBorder().isWithinBounds(blockPos)) {
                Player player = gameRenderer.getMinecraft().player;
                SlabType slabType = SlabTypeHelper.getSlabType(player, blockState, blockPos, hitResult.getLocation());
                if (slabType != null) {
                    VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.lines());
                    Vec3 position = camera.getPosition();
                    blockState = blockState.setValue(RotatedSlabBlock.TYPE, slabType);
                    renderHitOutline(poseStack, vertexConsumer, camera.getEntity(), position.x, position.y, position.z, blockPos, blockState, level);
                    return EventResult.INTERRUPT;
                }
            }
        }

        return EventResult.PASS;
    }

    private static void renderHitOutline(PoseStack poseStack, VertexConsumer consumer, Entity entity, double camX, double camY, double camZ, BlockPos pos, BlockState state, ClientLevel level) {
        renderShape(poseStack, consumer, state.getShape(level, pos, CollisionContext.of(entity)), pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ, 0.0F, 0.0F, 0.0F, 0.4F);
    }

    private static void renderShape(PoseStack poseStack, VertexConsumer consumer, VoxelShape shape, double x, double y, double z, float red, float green, float blue, float alpha) {
        PoseStack.Pose pose = poseStack.last();
        shape.forAllEdges((k, l, m, n, o, p) -> {
            float q = (float) (n - k);
            float r = (float) (o - l);
            float s = (float) (p - m);
            float t = Mth.sqrt(q * q + r * r + s * s);
            q /= t;
            r /= t;
            s /= t;
            consumer.vertex(pose.pose(), (float) (k + x), (float) (l + y), (float) (m + z)).color(red, green, blue, alpha).normal(pose.normal(), q, r, s).endVertex();
            consumer.vertex(pose.pose(), (float) (n + x), (float) (o + y), (float) (p + z)).color(red, green, blue, alpha).normal(pose.normal(), q, r, s).endVertex();
        });
    }
}
