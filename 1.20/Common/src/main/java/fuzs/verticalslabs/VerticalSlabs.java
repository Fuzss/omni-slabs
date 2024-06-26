package fuzs.verticalslabs;

import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.event.v1.RegistryEntryAddedCallback;
import fuzs.puzzleslib.api.event.v1.level.BlockEvents;
import fuzs.puzzleslib.api.event.v1.server.TagsUpdatedCallback;
import fuzs.puzzleslib.api.network.v3.NetworkHandlerV3;
import fuzs.verticalslabs.config.ClientConfig;
import fuzs.verticalslabs.handler.DiagonalBlockHandler;
import fuzs.verticalslabs.handler.ServerBreakSlabHandler;
import fuzs.verticalslabs.init.ModRegistry;
import fuzs.verticalslabs.network.client.ServerboundHitVectorMessage;
import fuzs.verticalslabs.network.client.ServerboundSlabPlacementMessage;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerticalSlabs implements ModConstructor {
    public static final String MOD_ID = "verticalslabs";
    public static final String MOD_NAME = "Vertical Slabs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static final NetworkHandlerV3 NETWORK = NetworkHandlerV3.builder(MOD_ID).registerServerbound(ServerboundHitVectorMessage.class).registerServerbound(ServerboundSlabPlacementMessage.class);
    public static final ConfigHolder CONFIG = ConfigHolder.builder(MOD_ID).client(ClientConfig.class);

    @Override
    public void onConstructMod() {
        ModRegistry.touch();
        registerHandlers();
        VoxelShape box = Shapes.box(0.0, 0.0, 0.0, 0.5, 0.5, 0.5);
        VoxelShape faceShape = box.getFaceShape(Direction.DOWN);
        box.forAllBoxes((startX, startY, startZ, endX, endY, endZ) -> {
            startX -= 0.5;
            startY -= 0.5;
            startZ -= 0.5;
            endX -= 0.5;
            endY -= 0.5;
            endZ -= 0.5;
            if (Math.abs(startX) > Math.abs(endX)) {
                double v = startX;
                startX = endX;
                endX = v;
            }
            if (Math.abs(startY) > Math.abs(endY)) {
                double v = startY;
                startY = endY;
                endY = v;
            }
            if (Math.abs(startZ) > Math.abs(endZ)) {
                double v = startZ;
                startZ = endZ;
                endZ = v;
            }
        });
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
