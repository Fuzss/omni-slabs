package fuzs.omnislabs.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.verticalslabs.client.handler.BlockDestroyingHandler;
import net.minecraft.client.Minecraft;

public class ClientConfig implements ConfigCore {
    @Config(description = "Trigger method for precise slab placement, allows placing slabs in all possible orientations.")
    public SlabActionType precisePlacement = SlabActionType.WHILE_CROUCHING;
    @Config(description = "Trigger method for precise slab destruction, allows breaking individual slabs in a single block.")
    public SlabActionType preciseDestruction = SlabActionType.WHILE_NOT_CROUCHING;

    @Override
    public void afterConfigReload() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() != null) {
            BlockDestroyingHandler.syncPreciseSlabPlacement(minecraft.player);
        }
    }
}
