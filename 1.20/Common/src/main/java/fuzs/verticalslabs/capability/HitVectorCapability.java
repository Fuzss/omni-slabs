package fuzs.verticalslabs.capability;

import fuzs.puzzleslib.api.capability.v2.data.CapabilityComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class HitVectorCapability implements CapabilityComponent {
    private Vec3 hitVector;

    public HitVectorCapability(Player player) {

    }

    public Vec3 getHitVector() {
        return this.hitVector;
    }

    public void setHitVector(Vec3 hitVector) {
        this.hitVector = hitVector;
    }
}
