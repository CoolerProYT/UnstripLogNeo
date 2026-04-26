package com.coolerpromc.unstriplog.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnstripLogConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnstripLogConfig.class);
    private static final String DEFAULT_ITEM_ID = "unstriplog:bark";

    private static String itemId = DEFAULT_ITEM_ID;
    private static boolean allowUnknownLog = true;

    public static void updateCache(String itemId, boolean allowUnknownLog) {
        UnstripLogConfig.itemId = itemId;
        UnstripLogConfig.allowUnknownLog = allowUnknownLog;
    }

    public static Item getBark() {
        Identifier id = Identifier.parse(itemId);
        if (!BuiltInRegistries.ITEM.containsKey(id)) {
            LOGGER.warn("[UnstripLog] Configured item '{}' is not a registered item. Falling back to default '{}'.", itemId, DEFAULT_ITEM_ID);
            return BuiltInRegistries.ITEM.getValue(Identifier.parse(DEFAULT_ITEM_ID));
        }
        return BuiltInRegistries.ITEM.getValue(id);
    }
    public static String getConfiguredItemId() { return itemId; }
    public static boolean allowUnknownLog() { return allowUnknownLog; }
}