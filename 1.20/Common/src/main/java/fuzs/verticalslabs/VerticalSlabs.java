package fuzs.verticalslabs;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.event.v1.RegistryEntryAddedCallback;
import fuzs.puzzleslib.api.event.v1.server.TagsUpdatedCallback;
import fuzs.verticalslabs.handler.DiagonalBlockHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerticalSlabs implements ModConstructor {
    public static final String MOD_ID = "verticalslabs";
    public static final String MOD_NAME = "Vertical Slabs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onConstructMod() {
        RegistryEntryAddedCallback.registryEntryAdded(Registries.BLOCK).register(DiagonalBlockHandler::onBlockAdded);
        TagsUpdatedCallback.EVENT.register(DiagonalBlockHandler::onTagsUpdated);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
