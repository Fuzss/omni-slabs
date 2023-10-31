package fuzs.verticalslabs;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.event.v1.RegistryEntryAddedCallback;
import fuzs.puzzleslib.api.event.v1.level.BlockEvents;
import fuzs.puzzleslib.api.event.v1.server.TagsUpdatedCallback;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.verticalslabs.handler.DiagonalBlockHandler;
import fuzs.verticalslabs.handler.ServerBreakSlabHandler;
import fuzs.verticalslabs.init.ModRegistry;
import fuzs.verticalslabs.network.client.ServerboundHitVectorMessage;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerticalSlabs implements ModConstructor {
    public static final String MOD_ID = "verticalslabs";
    public static final String MOD_NAME = "Vertical Slabs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandlerV3 NETWORK = NetworkHandlerV3.builder(MOD_ID).registerServerbound(ServerboundHitVectorMessage.class);

    @Override
    public void onConstructMod() {
        ModRegistry.touch();
        registerHandlers();
    }

    private static void registerHandlers() {
        RegistryEntryAddedCallback.registryEntryAdded(Registries.BLOCK).register(DiagonalBlockHandler::onBlockAdded);
        TagsUpdatedCallback.EVENT.register(DiagonalBlockHandler::onTagsUpdated);
        BlockEvents.BREAK.register(ServerBreakSlabHandler::onBreakBlock);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
