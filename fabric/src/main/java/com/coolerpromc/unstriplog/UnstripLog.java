package com.coolerpromc.unstriplog;

import com.coolerpromc.unstriplog.config.FabricUnstripLogConfig;
import com.coolerpromc.unstriplog.handler.FabricLogHandler;
import com.coolerpromc.unstriplog.item.ModItems;
import com.coolerpromc.unstriplog.network.ConfigSyncManager;
import com.coolerpromc.unstriplog.network.ConfigSyncPayload;
import com.coolerpromc.unstriplog.platform.FabricPlatformHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.registry.FuelValueEvents;
import net.minecraft.world.item.CreativeModeTabs;

public class UnstripLog implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CommonClass.init();
        FabricLogHandler.init();
        FuelValueEvents.BUILD.register((builder, _) -> builder.add(ModItems.BARK.get(), 150));
        ServerLifecycleEvents.SERVER_STARTED.register(server -> FabricPlatformHelper.SERVER = server);
        ServerLifecycleEvents.SERVER_STOPPED.register(_ -> FabricPlatformHelper.SERVER = null);
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(output -> CommonClass.addCreative(output::accept));
        PayloadTypeRegistry.clientboundPlay().register(ConfigSyncPayload.TYPE, ConfigSyncPayload.STREAM_CODEC);
        ServerPlayConnectionEvents.JOIN.register((listener, _, _) -> ConfigSyncManager.onPlayerLogin(listener.player));
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((serverPlayer, _) -> ConfigSyncManager.onPlayerLogin(serverPlayer));

        FabricUnstripLogConfig.init();
    }
}
