package com.coolerpromc.unstriplog.network;

import com.coolerpromc.unstriplog.config.BarkTypeConfig;
import com.coolerpromc.unstriplog.config.UnstripDetailedConfig;
import com.coolerpromc.unstriplog.config.UnstripLogConfig;
import com.coolerpromc.unstriplog.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class ConfigSyncManager {
    private ConfigSyncManager() {
    }

    public static void onPlayerLogin(Player player) {
        sendTo(player);
    }

    public static void onDatapackSync(Player player) {
        if (player != null) {
            sendTo(player);
            return;
        }

        if (Services.PLATFORM.getServer() != null) {
            Services.PLATFORM.getServer().getPlayerList().getPlayers().forEach(ConfigSyncManager::sendTo);
        }
    }

    public static void syncAll(MinecraftServer server) {
        server.getPlayerList().getPlayers().forEach(ConfigSyncManager::sendTo);
    }

    public static void sendTo(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        Services.NETWORK.sendToPlayer(serverPlayer, createPayload());
    }

    private static ConfigSyncPayload createPayload() {
        Item bark = UnstripLogConfig.getBark();
        if (bark == null) {
            bark = BuiltInRegistries.ITEM.getValue(Identifier.parse(UnstripLogConfig.getConfiguredItemId()));
        }

        Identifier barkId = BuiltInRegistries.ITEM.getKey(bark);

        List<ConfigSyncPayload.SyncedLogEntry> syncedEntries = UnstripDetailedConfig.getEntries().stream()
                .map(entry -> {
                    ItemStack drop = new ItemStack(entry.drop().item(), 1, entry.drop().componentPatch());
                    return new ConfigSyncPayload.SyncedLogEntry(
                            BuiltInRegistries.BLOCK.getKey(entry.base()),
                            BuiltInRegistries.BLOCK.getKey(entry.stripped()),
                            drop,
                            entry.unstripItem().map(i -> new ItemStack(i.item(), 1, i.componentPatch()))
                    );
                })
                .toList();

        return new ConfigSyncPayload(
                UnstripLogConfig.allowUnknownLog(),
                barkId,
                List.copyOf(BarkTypeConfig.getEntries()),
                syncedEntries
        );
    }
}

