package fuzs.omnislabs;

import fuzs.omnislabs.init.ModRegistry;
import fuzs.omnislabs.network.client.ServerboundHitVectorMessage;
import fuzs.omnislabs.network.client.ServerboundSlabPlacementMessage;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.context.PayloadTypesContext;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Omnislabs implements ModConstructor {
    public static final String MOD_ID = "omnislabs";
    public static final String MOD_NAME = "Omnislabs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onConstructMod() {
        ModRegistry.bootstrap();
    }

    @Override
    public void onRegisterPayloadTypes(PayloadTypesContext context) {
        context.playToServer(ServerboundHitVectorMessage.class, ServerboundHitVectorMessage.STREAM_CODEC);
        context.playToServer(ServerboundSlabPlacementMessage.class, ServerboundSlabPlacementMessage.STREAM_CODEC);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocationHelper.fromNamespaceAndPath(MOD_ID, path);
    }
}
