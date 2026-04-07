package com.coolerpromc.unstriplog.network;

import com.coolerpromc.unstriplog.Constants;
import com.coolerpromc.unstriplog.config.SyncedConfigState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(modid = Constants.MODID, value = Dist.CLIENT)
public final class ClientSyncEvents {
    private ClientSyncEvents() {
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        SyncedConfigState.clear();
    }
}

