package fuzs.omnislabs.network.client;

import fuzs.omnislabs.attachment.SyncedSlabSettings;
import fuzs.omnislabs.config.SlabActionType;
import fuzs.puzzleslib.common.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.common.api.network.v4.message.play.ServerboundPlayMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ServerboundSlabPlacementMessage(SlabActionType precisePlacement,
                                              SlabActionType preciseDestruction) implements ServerboundPlayMessage {
    public static final StreamCodec<ByteBuf, ServerboundSlabPlacementMessage> STREAM_CODEC = StreamCodec.composite(
            SlabActionType.STREAM_CODEC,
            ServerboundSlabPlacementMessage::precisePlacement,
            SlabActionType.STREAM_CODEC,
            ServerboundSlabPlacementMessage::preciseDestruction,
            ServerboundSlabPlacementMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                SyncedSlabSettings.setActionSettings(context.player(),
                        ServerboundSlabPlacementMessage.this.precisePlacement,
                        ServerboundSlabPlacementMessage.this.preciseDestruction);
            }
        };
    }
}
