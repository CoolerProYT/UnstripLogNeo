package com.coolerpromc.unstriplog.config;

import com.coolerpromc.unstriplog.UnstripLog;
import com.coolerpromc.unstriplog.handler.LogHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class BarkTypeConfig {
    private static final Codec<List<BarkTypeEntry>> LIST_CODEC = BarkTypeEntry.CODEC.listOf();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<BarkTypeEntry> ENTRIES = new ArrayList<>();
    private static final Map<String, BarkTypeEntry> LOOKUP = new LinkedHashMap<>();
    private static Path configPath;

    public static void init() {
        configPath = FMLPaths.CONFIGDIR.get().resolve("unstriplog/bark-type.json");

        try {
            Files.createDirectories(configPath.getParent());
        } catch (IOException e) {
            UnstripLog.LOGGER.error("Failed to create config directory", e);
        }

        if (!Files.exists(configPath)) {
            writeDefaults();
        }

        load();
    }

    public static void load() {
        ENTRIES.clear();
        LOOKUP.clear();

        try (Reader reader = Files.newBufferedReader(configPath)) {
            JsonElement json = JsonParser.parseReader(reader);

            DataResult<List<BarkTypeEntry>> result = LIST_CODEC.parse(JsonOps.INSTANCE, json);
            result.resultOrPartial(err -> UnstripLog.LOGGER.error("Config parse error: {}", err))
                    .ifPresent(list -> {
                        ENTRIES.addAll(list);
                        list.forEach(entry -> LOOKUP.put(entry.name, entry));
                    });
        } catch (IOException e) {
            UnstripLog.LOGGER.error("Failed to load config", e);
        }
    }

    private static void writeDefaults() {
        Set<String> addedBarks = new HashSet<>();

        LogHandler.BARK_TYPE.forEach((log, name) -> {
            if (addedBarks.add(name)) { // add returns false if name already exists
                ENTRIES.add(new BarkTypeEntry(name, UnstripLog.id("textures/item/" + name + "_bark.png")));
            }
        });

        save();
    }

    public static void save() {
        DataResult<JsonElement> result = LIST_CODEC.encodeStart(JsonOps.INSTANCE, ENTRIES);

        result.resultOrPartial(err -> UnstripLog.LOGGER.error("Config encode error: {}", err))
                .ifPresent(json -> {
                    try (Writer writer = Files.newBufferedWriter(configPath)) {
                        GSON.toJson(json, writer);
                    } catch (IOException e) {
                        UnstripLog.LOGGER.error("Failed to save config", e);
                    }
                });
    }

    public static List<BarkTypeEntry> getEntries() {
        return Collections.unmodifiableList(ENTRIES);
    }

    public static BarkTypeEntry getByBase(String name) {
        return LOOKUP.get(name);
    }


    public record BarkTypeEntry(String name, ResourceLocation texture){
        public static final Codec<BarkTypeEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("name").forGetter(BarkTypeEntry::name),
                ResourceLocation.CODEC.fieldOf("texture").forGetter(BarkTypeEntry::texture)
        ).apply(instance, BarkTypeEntry::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, BarkTypeEntry> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                BarkTypeEntry::name,
                ResourceLocation.STREAM_CODEC,
                BarkTypeEntry::texture,
                BarkTypeEntry::new
        );
    }
}
