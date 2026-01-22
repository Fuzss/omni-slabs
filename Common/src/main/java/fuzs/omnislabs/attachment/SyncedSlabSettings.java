package fuzs.omnislabs.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fuzs.omnislabs.config.SlabActionType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public record SyncedSlabSettings(Vec3 hitVector, SlabActionType precisePlacement, SlabActionType preciseDestruction) {
    public static final SyncedSlabSettings EMPTY = new SyncedSlabSettings(Vec3.ZERO,
            SlabActionType.NEVER,
            SlabActionType.NEVER);
    public static final Codec<SyncedSlabSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(Vec3.CODEC.optionalFieldOf(
                    "hit_vector",
                    Vec3.ZERO).forGetter(SyncedSlabSettings::hitVector),
            SlabActionType.CODEC.optionalFieldOf("precise_placement", SlabActionType.NEVER)
                    .forGetter(SyncedSlabSettings::precisePlacement),
            SlabActionType.CODEC.optionalFieldOf("precise_destruction", SlabActionType.NEVER)
                    .forGetter(SyncedSlabSettings::preciseDestruction)).apply(instance, SyncedSlabSettings::new));
    public static final StreamCodec<ByteBuf, SyncedSlabSettings> STREAM_CODEC = StreamCodec.composite(Vec3.STREAM_CODEC,
            SyncedSlabSettings::hitVector,
            SlabActionType.STREAM_CODEC,
            SyncedSlabSettings::precisePlacement,
            SlabActionType.STREAM_CODEC,
            SyncedSlabSettings::preciseDestruction,
            SyncedSlabSettings::new);

    public SyncedSlabSettings setHitVector(Vec3 hitVector) {
        return new SyncedSlabSettings(hitVector, this.precisePlacement, this.preciseDestruction);
    }

    public SyncedSlabSettings setActionSettings(SlabActionType precisePlacement, SlabActionType preciseDestruction) {
        return new SyncedSlabSettings(this.hitVector, precisePlacement, preciseDestruction);
    }
}
