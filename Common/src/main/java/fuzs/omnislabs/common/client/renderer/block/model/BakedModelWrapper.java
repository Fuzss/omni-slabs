package fuzs.omnislabs.common.client.renderer.block.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record BakedModelWrapper(BakedModel model, QuadCollection quadCollection) implements BakedModel {
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
        return this.quadCollection.getQuads(direction);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.model.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.model.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this.model.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return this.model.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.model.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return this.model.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return this.model.getOverrides();
    }
}
