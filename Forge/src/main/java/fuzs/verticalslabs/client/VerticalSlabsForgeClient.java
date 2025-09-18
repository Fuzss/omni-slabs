package fuzs.verticalslabs.client;

import fuzs.verticalslabs.VerticalSlabs;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = VerticalSlabs.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VerticalSlabsForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientModConstructor.construct(VerticalSlabs.MOD_ID, VerticalSlabsClient::new);
    }

    @SubscribeEvent
    public static void onAddPackFinders(final AddPackFindersEvent evt) {
        if (evt.getPackType() == PackType.CLIENT_RESOURCES) {
//            evt.addRepositorySource(p_10542_ -> {
//                p_10542_.accept(Pack.create("test", Component.literal("comedy"), false));
//            });
        }
    }
}
