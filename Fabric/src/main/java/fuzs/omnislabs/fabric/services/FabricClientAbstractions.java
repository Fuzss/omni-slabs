package fuzs.omnislabs.fabric.services;

import fuzs.omnislabs.common.services.ClientAbstractions;
import fuzs.puzzleslib.api.client.renderer.v1.model.QuadCollection;
import net.fabricmc.fabric.api.renderer.v1.model.WrapperBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public final class FabricClientAbstractions implements ClientAbstractions {
    @Override
    public BakedModel createBakedModelWrapper(BakedModel model, QuadCollection quadCollection) {
        return new BakedModelWrapper(model, quadCollection);
    }

    /**
     * We cannot use {@link net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel} as it overrides
     * {@link net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel#emitBlockQuads(BlockAndTintGetter, BlockState,
     * BlockPos, Supplier, RenderContext)} and
     * {@link net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel#emitItemQuads(ItemStack, Supplier,
     * RenderContext)} which then call {@link BakedModel#getQuads(BlockState, Direction, RandomSource)} on the original
     * model, which is incorrect.
     */
    private record BakedModelWrapper(BakedModel model,
                                     QuadCollection quadCollection) implements BakedModel, WrapperBakedModel {
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

        @Override
        public BakedModel getWrappedModel() {
            return this.model;
        }
    }
}
