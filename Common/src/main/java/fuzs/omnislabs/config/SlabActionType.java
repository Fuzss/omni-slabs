package fuzs.omnislabs.config;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.network.v4.codec.ExtraStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;

import java.util.Locale;

public enum SlabActionType implements StringRepresentable {
    NEVER {
        @Override
        public boolean supportsAction(Player player) {
            return false;
        }
    },
    WHILE_CROUCHING {
        @Override
        public boolean supportsAction(Player player) {
            return player.isSecondaryUseActive();
        }
    },
    WHILE_NOT_CROUCHING {
        @Override
        public boolean supportsAction(Player player) {
            return !player.isSecondaryUseActive();
        }
    },
    ALWAYS {
        @Override
        public boolean supportsAction(Player player) {
            return true;
        }
    };

    public static final Codec<SlabActionType> CODEC = StringRepresentable.fromEnum(SlabActionType::values);
    public static final StreamCodec<ByteBuf, SlabActionType> STREAM_CODEC = ExtraStreamCodecs.fromEnum(SlabActionType.class);

    public abstract boolean supportsAction(Player player);

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
