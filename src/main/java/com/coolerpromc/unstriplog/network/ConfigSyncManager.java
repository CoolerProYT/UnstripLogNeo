package com.coolerpromc.unstriplog.network;

import com.coolerpromc.unstriplog.config.BarkTypeConfig;
import com.coolerpromc.unstriplog.config.UnstripDetailedConfig;
import com.coolerpromc.unstriplog.config.UnstripLogConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;

public final class ConfigSyncManager {
    private static final String PROTOCOL_VERSION = "1";

    private ConfigSyncManager() {
    }

    public static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(ConfigSyncPayload.TYPE, ConfigSyncPayload.STREAM_CODEC, ConfigSyncPayload::handle);
    }

    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        sendTo(event.getEntity());
    }

    public static void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null) {
            sendTo(event.getPlayer());
            return;
        }

        if (ServerLifecycleHooks.getCurrentServer() != null) {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(ConfigSyncManager::sendTo);
        }
    }

    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() != UnstripLogConfig.CONFIG_SPEC) {
            return;
        }

        if (ServerLifecycleHooks.getCurrentServer() != null) {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(ConfigSyncManager::sendTo);
        }
    }

    public static void syncAll(net.minecraft.server.MinecraftServer server) {
        server.getPlayerList().getPlayers().forEach(ConfigSyncManager::sendTo);
    }

    private static void sendTo(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        PacketDistributor.sendToPlayer(serverPlayer, createPayload());
    }

    private static ConfigSyncPayload createPayload() {
        Item bark = UnstripLogConfig.CONFIG.getBark();
        if (bark == null) {
            bark = BuiltInRegistries.ITEM.getValue(Identifier.parse(UnstripLogConfig.CONFIG.getConfiguredItemId()));
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
                UnstripLogConfig.CONFIG.allowUnknownLog(),
                barkId,
                List.copyOf(BarkTypeConfig.getEntries()),
                syncedEntries
        );
    }
}

