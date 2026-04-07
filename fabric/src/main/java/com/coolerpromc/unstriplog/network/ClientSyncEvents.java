package com.coolerpromc.unstriplog.network;

import com.coolerpromc.unstriplog.config.SyncedConfigState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

public final class ClientSyncEvents {
    private ClientSyncEvents() {
    }

    public static void onClientLogout(ClientPacketListener listener, Minecraft client) {
        SyncedConfigState.clear();
    }
}