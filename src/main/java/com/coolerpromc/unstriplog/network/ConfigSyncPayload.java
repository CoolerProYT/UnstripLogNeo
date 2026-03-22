package com.coolerpromc.unstriplog.network;

import com.coolerpromc.unstriplog.UnstripLog;
import com.coolerpromc.unstriplog.config.BarkTypeConfig;
import com.coolerpromc.unstriplog.config.SyncedConfigState;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.Optional;

public record ConfigSyncPayload(boolean allowUnknownLog, ResourceLocation barkItem, List<BarkTypeConfig.BarkTypeEntry> barkTypes, List<SyncedLogEntry> logEntries) implements CustomPacketPayload {
    public static final Type<ConfigSyncPayload> TYPE = new Type<>(UnstripLog.id("config_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            ConfigSyncPayload::allowUnknownLog,
            ResourceLocation.STREAM_CODEC,
            ConfigSyncPayload::barkItem,
            BarkTypeConfig.BarkTypeEntry.STREAM_CODEC.apply(ByteBufCodecs.list()),
            ConfigSyncPayload::barkTypes,
            SyncedLogEntry.STREAM_CODEC.apply(ByteBufCodecs.list()),
            ConfigSyncPayload::logEntries,
            ConfigSyncPayload::new
    );

    public static void handle(ConfigSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> SyncedConfigState.apply(payload));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record SyncedLogEntry(ResourceLocation base, ResourceLocation stripped, ItemStack drop, Optional<ItemStack> unstripItem) {
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncedLogEntry> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC,
                SyncedLogEntry::base,
                ResourceLocation.STREAM_CODEC,
                SyncedLogEntry::stripped,
                ItemStack.STREAM_CODEC,
                SyncedLogEntry::drop,
                ByteBufCodecs.optional(ItemStack.STREAM_CODEC),
                SyncedLogEntry::unstripItem,
                SyncedLogEntry::new
        );
    }
}

