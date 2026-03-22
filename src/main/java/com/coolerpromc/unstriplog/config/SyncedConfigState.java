package com.coolerpromc.unstriplog.config;

import com.coolerpromc.unstriplog.network.ConfigSyncPayload;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class SyncedConfigState {
    private static volatile boolean hasSnapshot = false;
    private static volatile boolean allowUnknownLog = true;
    private static volatile String barkItemId = "unstriplog:bark";
    private static volatile List<BarkTypeConfig.BarkTypeEntry> barkTypes = List.of();
    private static volatile Map<Block, ConfigSyncPayload.SyncedLogEntry> byBase = Map.of();
    private static volatile Map<Block, ConfigSyncPayload.SyncedLogEntry> byStripped = Map.of();

    private SyncedConfigState() {
    }

    public static void apply(ConfigSyncPayload payload) {
        Map<Block, ConfigSyncPayload.SyncedLogEntry> nextByBase = new LinkedHashMap<>();
        Map<Block, ConfigSyncPayload.SyncedLogEntry> nextByStripped = new LinkedHashMap<>();

        for (ConfigSyncPayload.SyncedLogEntry entry : payload.logEntries()) {
            Block base = BuiltInRegistries.BLOCK.getValue(entry.base());
            Block stripped = BuiltInRegistries.BLOCK.getValue(entry.stripped());


            nextByBase.put(base, entry);
            nextByStripped.put(stripped, entry);
        }

        allowUnknownLog = payload.allowUnknownLog();
        barkItemId = payload.barkItem().toString();
        barkTypes = List.copyOf(payload.barkTypes());
        byBase = Collections.unmodifiableMap(nextByBase);
        byStripped = Collections.unmodifiableMap(nextByStripped);
        hasSnapshot = true;
    }

    public static void clear() {
        hasSnapshot = false;
        allowUnknownLog = true;
        barkItemId = "unstriplog:bark";
        barkTypes = List.of();
        byBase = Map.of();
        byStripped = Map.of();
    }

    public static boolean hasSnapshot() {
        return hasSnapshot;
    }

    public static boolean allowUnknownLog() {
        return allowUnknownLog;
    }

    public static String barkItemId() {
        return barkItemId;
    }

    public static List<BarkTypeConfig.BarkTypeEntry> barkTypes() {
        return barkTypes;
    }

    public static Optional<ConfigSyncPayload.SyncedLogEntry> byBase(Block block) {
        return Optional.ofNullable(byBase.get(block));
    }

    public static Optional<ConfigSyncPayload.SyncedLogEntry> byStripped(Block block) {
        return Optional.ofNullable(byStripped.get(block));
    }
}

