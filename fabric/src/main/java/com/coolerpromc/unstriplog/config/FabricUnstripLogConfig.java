package com.coolerpromc.unstriplog.config;

import com.coolerpromc.unstriplog.Constants;
import com.coolerpromc.unstriplog.network.ConfigSyncManager;
import com.coolerpromc.unstriplog.platform.Services;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.nio.file.*;

public class FabricUnstripLogConfig {
    private static FileConfig config;
    private static String bark;
    private static boolean allowUnknownLog;

    // default values
    private static final String DEFAULT_ITEM = "unstriplog:bark";
    private static final boolean DEFAULT_ALLOW_UNKNOWN = true;
    private static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("unstriplog-common.toml");

    public static void init() {
        config = FileConfig.builder(configPath, TomlFormat.instance())
                .autosave()
                .sync()
                .build();

        config.load();
        writeDefaults();
        updateCache();
        startWatcher();
    }

    private static void writeDefaults() {
        if (!config.contains("item")) {
            config.set("item", DEFAULT_ITEM);
        }
        if (!config.contains("allowUnknownLog")) {
            config.set("allowUnknownLog", DEFAULT_ALLOW_UNKNOWN);
        }
        config.save();
    }

    private static void startWatcher() {
        Thread watcherThread = new Thread(() -> {
            try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
                configPath.getParent().register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

                while (!Thread.currentThread().isInterrupted()) {
                    WatchKey key = watcher.take();

                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path changed = (Path) event.context();
                        if (changed.endsWith(configPath.getFileName())) {
                            Constants.LOGGER.info("Config changed, reloading...");
                            Thread.sleep(100);
                            reload();
                            if (Services.PLATFORM.getServer() != null) {
                                Services.PLATFORM.getServer().getPlayerList().getPlayers().forEach(ConfigSyncManager::sendTo);
                            }
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

    private static void updateCache() {
        String itemId = config.getOrElse("item", DEFAULT_ITEM);
        boolean allowUnknownLog = config.getOrElse("allowUnknownLog", DEFAULT_ALLOW_UNKNOWN);

        // validate
        Identifier id = Identifier.tryParse(itemId);
        if (id != null && BuiltInRegistries.ITEM.containsKey(id)) {
            bark = itemId;
        } else {
            Constants.LOGGER.warn("Invalid item id in config: {}, falling back to default", itemId);
            bark = DEFAULT_ITEM;
        }

        UnstripLogConfig.updateCache(itemId, allowUnknownLog);
    }

    public static void reload() {
        config.load();
        updateCache();
    }
}