package fuzs.omnislabs;

import fuzs.omnislabs.config.ClientConfig;
import fuzs.omnislabs.config.ServerConfig;
import fuzs.omnislabs.handler.BlockConversionHandler;
import fuzs.omnislabs.init.ModRegistry;
import fuzs.omnislabs.network.client.ServerboundHitVectorMessage;
import fuzs.omnislabs.network.client.ServerboundSlabPlacementMessage;
import fuzs.omnislabs.services.CommonAbstractions;
import fuzs.omnislabs.world.level.block.RotatedSlabBlock;
import fuzs.puzzleslib.common.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.puzzleslib.common.api.core.v1.context.PayloadTypesContext;
import fuzs.puzzleslib.common.api.event.v1.RegistryEntryAddedCallback;
import fuzs.puzzleslib.common.api.event.v1.core.EventPhase;
import fuzs.puzzleslib.common.api.event.v1.entity.player.PlayerInteractEvents;
import fuzs.puzzleslib.common.api.event.v1.server.TagsUpdatedCallback;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public class OmniSlabs implements ModConstructor {
    public static final String MOD_ID = "omnislabs";
    public static final String MOD_NAME = "Omni Slabs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID)
            .client(ClientConfig.class)
            .server(ServerConfig.class);
    public static final Predicate<Block> BLOCK_PREDICATE = (Block block) -> {
        return block instanceof SlabBlock && !(block instanceof RotatedSlabBlock);
    };

    @Override
    public void onConstructMod() {
        ModRegistry.bootstrap();
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        RegistryEntryAddedCallback.registryEntryAdded(Registries.BLOCK)
                .register(BlockConversionHandler.onRegistryEntryAdded(BLOCK_PREDICATE,
                        CommonAbstractions.INSTANCE::createSlabBlock,
                        MOD_ID));
        PlayerInteractEvents.USE_BLOCK.register(BlockConversionHandler.onUseBlock(ModRegistry.UNALTERED_SLABS_BLOCK_TAG,
                () -> CONFIG.get(ServerConfig.class).enableBlockConversionInteraction));
        TagsUpdatedCallback.EVENT.register(EventPhase.FIRST,
                BlockConversionHandler.onTagsUpdated(ModRegistry.UNALTERED_SLABS_BLOCK_TAG, BLOCK_PREDICATE));
    }

    @Override
    public void onRegisterPayloadTypes(PayloadTypesContext context) {
        context.playToServer(ServerboundHitVectorMessage.class, ServerboundHitVectorMessage.STREAM_CODEC);
        context.playToServer(ServerboundSlabPlacementMessage.class, ServerboundSlabPlacementMessage.STREAM_CODEC);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
