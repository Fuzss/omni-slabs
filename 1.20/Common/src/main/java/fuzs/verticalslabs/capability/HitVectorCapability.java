package fuzs.verticalslabs.capability;

import fuzs.puzzleslib.api.capability.v2.data.CapabilityComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class HitVectorCapability implements CapabilityComponent {
    private final Player player;
    private Vec3 hitVector;
    private PreciseSlabPlacement preciseSlabPlacement;

    public HitVectorCapability(Player player) {
        this.player = player;
    }

    public Vec3 getHitVector() {
        return this.hitVector;
    }

    public void setHitVector(Vec3 hitVector) {
        this.hitVector = hitVector;
    }

    public boolean isPlacementPrecise() {
        return switch (this.preciseSlabPlacement) {
            case NEVER -> false;
            case WHILE_CROUCHING -> this.player.isSecondaryUseActive();
            case WHILE_NOT_CROUCHING -> !this.player.isSecondaryUseActive();
            case ALWAYS -> true;
        };
    }

    public void setPrecisePlacement(PreciseSlabPlacement preciseSlabPlacement) {
        this.preciseSlabPlacement = preciseSlabPlacement;
    }

    public enum PreciseSlabPlacement {
        WHILE_CROUCHING, WHILE_NOT_CROUCHING, ALWAYS, NEVER
    }
}
