package fuzs.verticalslabs.init;

import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.capability.v2.data.CapabilityKey;
import fuzs.puzzleslib.api.capability.v2.data.PlayerRespawnCopyStrategy;
import fuzs.verticalslabs.VerticalSlabs;
import fuzs.verticalslabs.capability.HitVectorCapability;

public class ModRegistry {
    static final CapabilityController CAPABILITY = CapabilityController.from(VerticalSlabs.MOD_ID);
    public static final CapabilityKey<HitVectorCapability> HIT_VECTOR_CAPABILITY = CAPABILITY.registerPlayerCapability(
            "hit_vector",
            HitVectorCapability.class,
            HitVectorCapability::new,
            PlayerRespawnCopyStrategy.NEVER);

    public static void bootstrap() {
        // NO-OP
    }
}
