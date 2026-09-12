package fuzs.omnislabs.common.client.renderer.block.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.joml.Vector3fc;

import java.util.Arrays;

@Deprecated
public class MutableBakedQuad {
    private final BakedQuad bakedQuad;
    private Direction direction;
    private TextureAtlasSprite sprite;
    private int tintIndex;
    private boolean shade;
    private boolean copyAllProperties;
    private boolean computeQuadNormals;

    public MutableBakedQuad(BakedQuad bakedQuad) {
        this.bakedQuad = bakedQuad;
        this.direction = bakedQuad.getDirection();
        this.sprite = bakedQuad.getSprite();
        this.tintIndex = bakedQuad.getTintIndex();
        this.shade = bakedQuad.isShade();
    }

    public static MutableBakedQuad toMutable(BakedQuad bakedQuad) {
        return new MutableBakedQuad(bakedQuad);
    }

    public Vector3fc position0() {
        return this.position(0);
    }

    public Vector3fc position1() {
        return this.position(1);
    }

    public Vector3fc position2() {
        return this.position(2);
    }

    public Vector3fc position3() {
        return this.position(3);
    }

    public Vector3fc position(int vertexIndex) {
        return QuadUtils.getPosition(this.bakedQuad, vertexIndex);
    }

    public long packedUV0() {
        return this.packedUV(0);
    }

    public long packedUV1() {
        return this.packedUV(1);
    }

    public long packedUV2() {
        return this.packedUV(2);
    }

    public long packedUV3() {
        return this.packedUV(3);
    }

    public long packedUV(int vertexIndex) {
        float u = QuadUtils.getU(this.bakedQuad, vertexIndex);
        float v = QuadUtils.getV(this.bakedQuad, vertexIndex);
        return UVPair.pack(u, v);
    }

    public Direction direction() {
        return this.direction;
    }

    public TextureAtlasSprite sprite() {
        return this.sprite;
    }

    public int tintIndex() {
        return this.tintIndex;
    }

    public boolean shade() {
        return this.shade;
    }

    public MutableBakedQuad position0(Vector3fc position) {
        return this.position(0, position);
    }

    public MutableBakedQuad position1(Vector3fc position) {
        return this.position(1, position);
    }

    public MutableBakedQuad position2(Vector3fc position) {
        return this.position(2, position);
    }

    public MutableBakedQuad position3(Vector3fc position) {
        return this.position(3, position);
    }

    public MutableBakedQuad position(int vertexIndex, Vector3fc position) {
        QuadUtils.setPosition(this.bakedQuad, vertexIndex, position.x(), position.y(), position.z());
        return this;
    }

    public MutableBakedQuad packedUV0(long packedUV) {
        return this.packedUV(0, packedUV);
    }

    public MutableBakedQuad packedUV1(long packedUV) {
        return this.packedUV(1, packedUV);
    }

    public MutableBakedQuad packedUV2(long packedUV) {
        return this.packedUV(2, packedUV);
    }

    public MutableBakedQuad packedUV3(long packedUV) {
        return this.packedUV(3, packedUV);
    }

    public MutableBakedQuad packedUV(int vertexIndex, long packedUV) {
        float u = UVPair.unpackU(packedUV);
        QuadUtils.setU(this.bakedQuad, vertexIndex, u);
        float v = UVPair.unpackV(packedUV);
        QuadUtils.setV(this.bakedQuad, vertexIndex, v);
        return this;
    }

    public MutableBakedQuad direction(Direction direction) {
        this.copyAllProperties = true;
        this.direction = direction;
        return this;
    }

    public MutableBakedQuad sprite(TextureAtlasSprite sprite) {
        this.copyAllProperties = true;
        this.sprite = sprite;
        return this;
    }

    public MutableBakedQuad tintIndex(int tintIndex) {
        this.copyAllProperties = true;
        this.tintIndex = tintIndex;
        return this;
    }

    public MutableBakedQuad shade(boolean shade) {
        this.copyAllProperties = true;
        this.shade = shade;
        return this;
    }

    public MutableBakedQuad packedNormal0(int packedNormal) {
        return this.packedNormal(0, packedNormal);
    }

    public MutableBakedQuad packedNormal1(int packedNormal) {
        return this.packedNormal(1, packedNormal);
    }

    public MutableBakedQuad packedNormal2(int packedNormal) {
        return this.packedNormal(2, packedNormal);
    }

    public MutableBakedQuad packedNormal3(int packedNormal) {
        return this.packedNormal(3, packedNormal);
    }

    public MutableBakedQuad packedNormal(int vertexIndex, int packedNormal) {
        QuadUtils.setNormal(this.bakedQuad, vertexIndex, packedNormal);
        return this;
    }

    public MutableBakedQuad packedNormal(int packedNormal) {
        return this.packedNormal0(packedNormal)
                .packedNormal1(packedNormal)
                .packedNormal2(packedNormal)
                .packedNormal3(packedNormal);
    }

    public MutableBakedQuad computeQuadNormals() {
        this.computeQuadNormals = true;
        return this;
    }

    public MutableBakedQuad packedColor0(int packedColor) {
        return this.packedColor(0, packedColor);
    }

    public MutableBakedQuad packedColor1(int packedColor) {
        return this.packedColor(1, packedColor);
    }

    public MutableBakedQuad packedColor2(int packedColor) {
        return this.packedColor(2, packedColor);
    }

    public MutableBakedQuad packedColor3(int packedColor) {
        return this.packedColor(3, packedColor);
    }

    public MutableBakedQuad packedColor(int vertexIndex, int packedColor) {
        QuadUtils.setColor(this.bakedQuad, vertexIndex, packedColor);
        return this;
    }

    public MutableBakedQuad packedColor(int packedColor) {
        return this.packedColor0(packedColor)
                .packedColor1(packedColor)
                .packedColor2(packedColor)
                .packedColor3(packedColor);
    }

    public BakedQuad toImmutable() {
        BakedQuad bakedQuad = this.copyAllProperties ? this.copyBakedQuad(this.bakedQuad) : this.bakedQuad;
        if (this.computeQuadNormals) {
            QuadUtils.fillNormal(bakedQuad);
        }

        return bakedQuad;
    }

    protected BakedQuad copyBakedQuad(BakedQuad bakedQuad) {
        int[] vertices = bakedQuad.getVertices();
        return new BakedQuad(Arrays.copyOf(vertices, vertices.length),
                this.tintIndex(),
                this.direction(),
                this.sprite(),
                this.shade());
    }
}
