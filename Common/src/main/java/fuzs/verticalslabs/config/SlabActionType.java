package fuzs.verticalslabs.config;

import com.mojang.serialization.Codec;
import fuzs.verticalslabs.capability.HitVectorCapability;
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

    public abstract boolean supportsAction(Player player);

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
