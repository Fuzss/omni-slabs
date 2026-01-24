package fuzs.omnislabs.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;

public class ServerConfig implements ConfigCore {
    @Config(description = "When a slab block is placed against another non-double slab block usual placement behavior is bypassed and instead the orientation of the other slab is copied.")
    public boolean copyNeighborSlabOrientation = true;
    @Config(description = "This mod replaces all original slab blocks with a custom implementation. Slabs placed in the world can be converted or reverted via sneak+right-clicking with an empty hand.")
    public boolean enableBlockConversionInteraction = true;
}
