package fuzs.verticalslabs.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.verticalslabs.capability.HitVectorCapability;
import fuzs.verticalslabs.client.handler.BlockDestroyingHandler;
import net.minecraft.client.Minecraft;

public class ClientConfig implements ConfigCore {
    @Config(description = "Trigger method for precise slab placing behavior, allowing access to placing slabs in all possible orientations.")
    public HitVectorCapability.PreciseSlabPlacement preciseSlabPlacement = HitVectorCapability.PreciseSlabPlacement.WHILE_CROUCHING;

    @Override
    public void afterConfigReload() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() != null) {
            BlockDestroyingHandler.syncPreciseSlabPlacement(minecraft.player);
        }
    }
}
