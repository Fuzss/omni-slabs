package fuzs.omnislabs.init;

import fuzs.omnislabs.Omnislabs;
import fuzs.omnislabs.attachment.SyncedSlabSettings;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentType;
import fuzs.puzzleslib.api.network.v4.PlayerSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class ModRegistry {
    public static final DataAttachmentType<Entity, SyncedSlabSettings> SYNCED_SLAB_SETTINGS_ATTACHMENT_TYPE = DataAttachmentRegistry.<SyncedSlabSettings>entityBuilder()
            .defaultValue(EntityType.PLAYER, SyncedSlabSettings.EMPTY)
            .networkSynchronized(SyncedSlabSettings.STREAM_CODEC, PlayerSet::ofEntity)
            .build(Omnislabs.id("synced_slab_settings"));

    public static void bootstrap() {
        // NO-OP
    }
}
