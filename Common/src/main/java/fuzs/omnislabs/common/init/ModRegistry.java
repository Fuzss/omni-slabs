package fuzs.omnislabs.common.init;

import fuzs.omnislabs.common.OmniSlabs;
import fuzs.omnislabs.common.attachment.SyncedSlabSettings;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentType;
import fuzs.puzzleslib.api.init.v3.tags.TagFactory;
import fuzs.puzzleslib.api.network.v3.PlayerSet;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public class ModRegistry {
    public static final DataAttachmentType<Entity, SyncedSlabSettings> SYNCED_SLAB_SETTINGS_ATTACHMENT_TYPE = DataAttachmentRegistry.<SyncedSlabSettings>entityBuilder()
            .defaultValue(EntityType.PLAYER, SyncedSlabSettings.EMPTY)
            .networkSynchronized(SyncedSlabSettings.STREAM_CODEC, PlayerSet::ofEntity)
            .build(OmniSlabs.id("synced_slab_settings"));

    static final TagFactory TAGS = TagFactory.make(OmniSlabs.MOD_ID);
    public static final TagKey<Block> UNALTERED_SLABS_BLOCK_TAG = TAGS.registerBlockTag("unaltered_slabs");

    public static void bootstrap() {
        // NO-OP
    }
}
