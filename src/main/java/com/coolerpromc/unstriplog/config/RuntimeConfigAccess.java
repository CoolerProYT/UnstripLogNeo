package com.coolerpromc.unstriplog.config;

import com.coolerpromc.unstriplog.network.ConfigSyncPayload;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.List;
import java.util.Optional;

public final class RuntimeConfigAccess {
    private RuntimeConfigAccess() {
    }

    public static List<BarkTypeConfig.BarkTypeEntry> barkTypes() {
        if (useSyncedSnapshot()) {
            return SyncedConfigState.barkTypes();
        }
        return BarkTypeConfig.getEntries();
    }

    public static boolean allowUnknownLog() {
        if (useSyncedSnapshot()) {
            return SyncedConfigState.allowUnknownLog();
        }
        return UnstripLogConfig.CONFIG.allowUnknownLog();
    }

    public static Item barkItem() {
        if (useSyncedSnapshot()) {
            return BuiltInRegistries.ITEM.get(ResourceLocation.parse(SyncedConfigState.barkItemId()));
        }

        Item bark = UnstripLogConfig.CONFIG.getBark();
        if (bark != null) {
            return bark;
        }

        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(UnstripLogConfig.CONFIG.getConfiguredItemId()));
    }

    public static Optional<RuntimeLogEntry> byBase(Block block) {
        if (useSyncedSnapshot()) {
            return SyncedConfigState.byBase(block).map(RuntimeConfigAccess::fromSynced);
        }
        return UnstripDetailedConfig.getByBase(block).map(RuntimeConfigAccess::fromLocal);
    }

    public static Optional<RuntimeLogEntry> byStripped(Block block) {
        if (useSyncedSnapshot()) {
            return SyncedConfigState.byStripped(block).map(RuntimeConfigAccess::fromSynced);
        }
        return UnstripDetailedConfig.getByStripped(block).map(RuntimeConfigAccess::fromLocal);
    }

    private static RuntimeLogEntry fromLocal(UnstripDetailedConfig.LogEntry entry) {
        ItemStack drop = new ItemStack(entry.drop().item(), 1, entry.drop().componentPatch());
        Optional<ItemStack> unstripItem = entry.unstripItem().map(i -> new ItemStack(i.item(), 1, i.componentPatch()));
        return new RuntimeLogEntry(entry.base(), entry.stripped(), drop, unstripItem);
    }

    private static RuntimeLogEntry fromSynced(ConfigSyncPayload.SyncedLogEntry entry) {
        Block base = BuiltInRegistries.BLOCK.get(entry.base());
        Block stripped = BuiltInRegistries.BLOCK.get(entry.stripped());
        return new RuntimeLogEntry(base, stripped, entry.drop().copy(), entry.unstripItem().map(ItemStack::copy));
    }

    private static boolean useSyncedSnapshot() {
        return SyncedConfigState.hasSnapshot() && FMLEnvironment.dist == Dist.CLIENT;
    }

    public record RuntimeLogEntry(Block base, Block stripped, ItemStack drop, Optional<ItemStack> unstripItem) {
    }
}

