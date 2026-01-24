package fuzs.omnislabs.init;

import fuzs.omnislabs.OmniSlabs;
import fuzs.omnislabs.attachment.SyncedSlabSettings;
import fuzs.omnislabs.world.level.block.DoubleSlabBlock;
import fuzs.omnislabs.world.level.block.entity.DoubleSlabBlockEntity;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentType;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.init.v3.tags.TagFactory;
import fuzs.puzzleslib.api.network.v4.PlayerSet;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModRegistry {
    static final RegistryManager REGISTRIES = RegistryManager.from(OmniSlabs.MOD_ID);
    public static final Holder.Reference<Block> DOUBLE_SLAB_BLOCK = REGISTRIES.registerBlock("double_slab",
            DoubleSlabBlock::new,
            () -> BlockBehaviour.Properties.of().noLootTable());
    public static final Holder.Reference<BlockEntityType<DoubleSlabBlockEntity>> DOUBLE_SLAB_BLOCK_ENTITY_TYPE = REGISTRIES.registerBlockEntityType(
            "double_slab",
            DoubleSlabBlockEntity::new,
            DOUBLE_SLAB_BLOCK);

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
