package com.coolerpromc.unstriplog;

import com.coolerpromc.unstriplog.client.BarkItemRenderer;
import com.coolerpromc.unstriplog.config.NeoForgeUnstripLogConfig;
import com.coolerpromc.unstriplog.network.ConfigSyncManager;
import com.coolerpromc.unstriplog.network.ConfigSyncPayload;
import com.coolerpromc.unstriplog.platform.NeoForgeRegistryHelper;
import com.coolerpromc.unstriplog.platform.util.NeoForgePayloadContext;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static com.coolerpromc.unstriplog.Constants.id;

@Mod(Constants.MODID)
@EventBusSubscriber(modid = Constants.MODID)
public class UnstripLog {
    public UnstripLog(IEventBus eventBus, ModContainer container) {
        CommonClass.init();
        NeoForgeRegistryHelper.register(eventBus);

        container.registerConfig(ModConfig.Type.COMMON, NeoForgeUnstripLogConfig.CONFIG_SPEC);
        eventBus.addListener(NeoForgeUnstripLogConfig::onModConfigLoading);
        eventBus.addListener(NeoForgeUnstripLogConfig::onModConfigReloading);

        NeoForge.EVENT_BUS.addListener(this::onPlayerLogin);
        NeoForge.EVENT_BUS.addListener(this::onDatapackSync);
    }

    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        ConfigSyncManager.onPlayerLogin(event.getEntity());
    }

    public void onDatapackSync(OnDatapackSyncEvent event) {
        ConfigSyncManager.onDatapackSync(event.getPlayer());
    }

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS){
            CommonClass.addCreative(event::accept);
        }
    }

    @SubscribeEvent
    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(ConfigSyncPayload.TYPE, ConfigSyncPayload.STREAM_CODEC, (payload, context) -> ConfigSyncPayload.handle(payload, new NeoForgePayloadContext(context)));
    }

    @EventBusSubscriber(modid = Constants.MODID, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onRegisterSpecialModelRenderer(RegisterSpecialModelRendererEvent event) {
            event.register(id("bark"), BarkItemRenderer.Unbaked.MAP_CODEC);
        }
    }
}