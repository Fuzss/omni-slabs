package fuzs.omnislabs.neoforge.services;

import fuzs.omnislabs.common.services.ClientAbstractions;
import fuzs.puzzleslib.api.client.renderer.v1.model.QuadCollection;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class NeoForgeClientAbstractions implements ClientAbstractions {
    @Override
    public BakedModel createBakedModelWrapper(BakedModel model, QuadCollection quadCollection) {
        return new BakedModelWrapper<>(model) {
            @Override
            public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
                return quadCollection.getQuads(direction);
            }

            @Override
            public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random, ModelData data, @Nullable RenderType renderType) {
                return quadCollection.getQuads(direction);
            }
        };
    }
}
