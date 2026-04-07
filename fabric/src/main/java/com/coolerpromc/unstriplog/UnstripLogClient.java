package com.coolerpromc.unstriplog;

import com.coolerpromc.unstriplog.client.BarkItemRenderer;
import com.coolerpromc.unstriplog.network.ClientSyncEvents;
import com.coolerpromc.unstriplog.network.ConfigSyncPayload;
import com.coolerpromc.unstriplog.platform.util.FabricClientPayloadContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.renderer.special.SpecialModelRenderers;

public class UnstripLogClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SpecialModelRenderers.ID_MAPPER.put(Constants.id("bark"), BarkItemRenderer.Unbaked.MAP_CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPayload.TYPE, (payload, context) -> ConfigSyncPayload.handle(payload, new FabricClientPayloadContext(context)));
        ClientPlayConnectionEvents.DISCONNECT.register(ClientSyncEvents::onClientLogout);
    }
}
