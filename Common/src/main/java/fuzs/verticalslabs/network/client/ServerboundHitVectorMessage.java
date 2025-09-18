package fuzs.verticalslabs.network.client;

import fuzs.puzzleslib.api.network.v3.ServerMessageListener;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.verticalslabs.capability.HitVectorCapability;
import fuzs.verticalslabs.init.ModRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;

public record ServerboundHitVectorMessage(Vec3 hitVector) implements ServerboundMessage<ServerboundHitVectorMessage> {

    @Override
    public ServerMessageListener<ServerboundHitVectorMessage> getHandler() {
        return new ServerMessageListener<>() {

            @Override
            public void handle(ServerboundHitVectorMessage message, MinecraftServer server, ServerGamePacketListenerImpl handler, ServerPlayer player, ServerLevel level) {
                HitVectorCapability capability = ModRegistry.HIT_VECTOR_CAPABILITY.get(player);
                capability.setHitVector(message.hitVector);
            }
        };
    }
}
