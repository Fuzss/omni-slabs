package fuzs.omnislabs.common.client.renderer.block.model;

/**
 * Copied from Minecraft 26.2.
 */
public final class CuboidFace {

    private CuboidFace() {
        // NO-OP
    }

    public record UVs(float minU, float minV, float maxU, float maxV) {
        public float getVertexU(int index) {
            return index != 0 && index != 1 ? this.maxU : this.minU;
        }

        public float getVertexV(int index) {
            return index != 0 && index != 3 ? this.maxV : this.minV;
        }
    }
}
