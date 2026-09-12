package fuzs.omnislabs.neoforge.client;

import fuzs.omnislabs.common.OmniSlabs;
import fuzs.omnislabs.common.client.OmniSlabsClient;
import fuzs.omnislabs.common.data.client.ModModelProvider;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import net.minecraft.server.packs.PackType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = OmniSlabs.MOD_ID, dist = Dist.CLIENT)
public class OmniSlabsNeoForgeClient {

    public OmniSlabsNeoForgeClient() {
        ClientModConstructor.construct(OmniSlabs.MOD_ID, OmniSlabsClient::new);
        DataProviderHelper.registerDataProviders(OmniSlabs.MOD_ID, ModModelProvider::new);
        DataProviderHelper.registerDataProviders(OmniSlabsClient.DISTINCT_SLABS_ID,
                PackType.CLIENT_RESOURCES,
                ModModelProvider.DistinctSlabs::new);
    }
}
