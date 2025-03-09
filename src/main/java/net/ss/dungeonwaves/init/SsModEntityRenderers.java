
package net.ss.dungeonwaves.init;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ss.dungeonwaves.client.renderer.WanderingMerchantRenderer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SsModEntityRenderers {
    @SubscribeEvent
    public static void registerEntityRenderers (EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SsModEntities.WANDERING_MERCHANT.get(), WanderingMerchantRenderer::new);
    }
}
