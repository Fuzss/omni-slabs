package fuzs.verticalslabs.network.client;

import fuzs.puzzleslib.api.network.v3.ServerMessageListener;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import fuzs.verticalslabs.capability.HitVectorCapability;
import fuzs.verticalslabs.init.ModRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public record ServerboundSlabPlacementMessage(HitVectorCapability.PreciseSlabPlacement preciseSlabPlacement) implements ServerboundMessage<ServerboundSlabPlacementMessage> {

    @Override
    public ServerMessageListener<ServerboundSlabPlacementMessage> getHandler() {
        return new ServerMessageListener<>() {

            @Override
            public void handle(ServerboundSlabPlacementMessage message, MinecraftServer server, ServerGamePacketListenerImpl handler, ServerPlayer player, ServerLevel level) {
                ModRegistry.HIT_VECTOR_CAPABILITY.get(player).setPrecisePlacement(message.preciseSlabPlacement);
            }
        };
    }
}
