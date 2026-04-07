package com.coolerpromc.unstriplog.config;

import com.coolerpromc.unstriplog.Constants;
import com.coolerpromc.unstriplog.component.ModDataComponents;
import com.coolerpromc.unstriplog.handler.LogHandler;
import com.coolerpromc.unstriplog.item.ModItems;
import com.coolerpromc.unstriplog.network.ConfigSyncManager;
import com.coolerpromc.unstriplog.platform.Services;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.*;
import java.util.*;

public class UnstripDetailedConfig {
    private static final Codec<List<LogEntry>> LIST_CODEC = LogEntry.CODEC.listOf();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<LogEntry> ENTRIES = new ArrayList<>();
    private static final Map<Block, LogEntry> LOOKUP = new LinkedHashMap<>();
    private static Path configPath;
    private static Thread watcherThread;

    public static void init() {
        configPath = Services.PLATFORM.getConfigDir().resolve("unstriplog/unstrip-detailed.json");

        try {
            Files.createDirectories(configPath.getParent());
        } catch (IOException e) {
            Constants.LOGGER.error("Failed to create config directory", e);
        }

        if (!Files.exists(configPath)) {
            writeDefaults();
        }

        load();
        startWatcher();
    }

    private static void startWatcher() {
        watcherThread = new Thread(() -> {
            try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
                configPath.getParent().register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

                while (!Thread.currentThread().isInterrupted()) {
                    WatchKey key = watcher.take(); // blocks until event

                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path changed = (Path) event.context();

                        // make sure it's our file, not some other config
                        if (changed.endsWith(configPath.getFileName())) {
                            Constants.LOGGER.info("Config changed, reloading...");
                            Thread.sleep(100);
                            load();
                        }
                    }

                    key.reset();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                Constants.LOGGER.error("Config watcher failed", e);
            }
        }, "unstriplog-config-watcher");

        watcherThread.setDaemon(true);
        watcherThread.start();
    }

    public static void load() {
        ENTRIES.clear();
        LOOKUP.clear();

        try (Reader reader = Files.newBufferedReader(configPath)) {
            JsonElement json = JsonParser.parseReader(reader);

            DataResult<List<LogEntry>> result = LIST_CODEC.parse(JsonOps.INSTANCE, json);
            result.resultOrPartial(err -> Constants.LOGGER.error("Config parse error: {}", err))
                    .ifPresent(list -> {
                        ENTRIES.addAll(list);
                        list.forEach(entry -> LOOKUP.put(entry.base(), entry));
                    });
        } catch (IOException e) {
            Constants.LOGGER.error("Failed to load config", e);
        }

        if (Services.PLATFORM.getServer() != null) {
            ConfigSyncManager.syncAll(Services.PLATFORM.getServer());
        }
    }

    private static void writeDefaults() {
        LogHandler.STRIPPED_LOG.forEach((key, value) -> {
            if (LogHandler.BARK_TYPE.get(value) != null){
                ENTRIES.add(new LogEntry(
                        value,
                        key,
                        new ItemEntry(ModItems.BARK.holder(), DataComponentPatch.builder().set(ModDataComponents.BARK_TYPE.get(), BarkTypeConfig.getByBase(LogHandler.BARK_TYPE.get(value))).build()),
                        Optional.empty()
                ));
            }
        });

        save();
    }

    public static void save() {
        DataResult<JsonElement> result = LIST_CODEC.encodeStart(JsonOps.INSTANCE, ENTRIES);

        result.resultOrPartial(err -> Constants.LOGGER.error("Config encode error: {}", err))
                .ifPresent(json -> {
                    try (Writer writer = Files.newBufferedWriter(configPath)) {
                        GSON.toJson(json, writer);
                    } catch (IOException e) {
                        Constants.LOGGER.error("Failed to save config", e);
                    }
                });
    }

    public static Optional<LogEntry> getByBase(Block block) {
        return Optional.ofNullable(LOOKUP.get(block));
    }

    public static Optional<LogEntry> getByStripped(Block block) {
        return ENTRIES.stream().filter(e -> e.stripped().equals(block)).findFirst();
    }

    public static List<LogEntry> getEntries() {
        return Collections.unmodifiableList(ENTRIES);
    }

    public record ItemEntry(Holder<Item> item, DataComponentPatch componentPatch) {
        public static final Codec<ItemEntry> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Item.CODEC.fieldOf("id").forGetter(ItemEntry::item),
                        DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemEntry::componentPatch)
                ).apply(instance, ItemEntry::new)
        );
    }

    public record LogEntry(Block base, Block stripped, ItemEntry drop, Optional<ItemEntry> unstripItem) {
        public static final Codec<LogEntry> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("base").forGetter(LogEntry::base),
                        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("stripped").forGetter(LogEntry::stripped),
                        ItemEntry.CODEC.fieldOf("drop").forGetter(LogEntry::drop),
                        ItemEntry.CODEC.optionalFieldOf("unstrip_item").forGetter(LogEntry::unstripItem)
                ).apply(instance, LogEntry::new)
        );
    }
}
