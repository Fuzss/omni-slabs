package fuzs.omnislabs.network.client;

import fuzs.omnislabs.init.ModRegistry;
import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.play.ServerboundPlayMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public record ServerboundHitVectorMessage(Vec3 hitVector) implements ServerboundPlayMessage {
    public static final StreamCodec<ByteBuf, ServerboundHitVectorMessage> STREAM_CODEC = StreamCodec.composite(Vec3.STREAM_CODEC,
            ServerboundHitVectorMessage::hitVector,
            ServerboundHitVectorMessage::new);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<Context>() {
            @Override
            public void accept(Context context) {
                ModRegistry.SYNCED_SLAB_SETTINGS_ATTACHMENT_TYPE.get(context.player())
                        .setHitVector(ServerboundHitVectorMessage.this.hitVector);
            }
        };
    }
}
