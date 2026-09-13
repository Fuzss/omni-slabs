package fuzs.omnislabs.common.services;

import fuzs.puzzleslib.api.client.renderer.v1.model.QuadCollection;
import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import net.minecraft.client.resources.model.BakedModel;

public interface ClientAbstractions {
    ClientAbstractions INSTANCE = ServiceProviderHelper.load(ClientAbstractions.class);

    BakedModel createBakedModelWrapper(BakedModel model, QuadCollection quadCollection);
}
