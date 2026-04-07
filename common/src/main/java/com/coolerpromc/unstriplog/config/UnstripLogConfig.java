package com.coolerpromc.unstriplog.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class UnstripLogConfig {
    private static Item bark;
    private static String itemId = "unstriplog:bark"; // default
    private static boolean allowUnknownLog = true;

    public static void updateCache(String itemId, boolean allowUnknownLog) {
        UnstripLogConfig.itemId = itemId;
        UnstripLogConfig.allowUnknownLog = allowUnknownLog;
        UnstripLogConfig.bark = BuiltInRegistries.ITEM.getValue(Identifier.parse(itemId));
    }

    public static Item getBark() { return bark; }
    public static String getConfiguredItemId() { return itemId; }
    public static boolean allowUnknownLog() { return allowUnknownLog; }

    public static boolean validateItemId(String itemName) {
        return BuiltInRegistries.ITEM.containsKey(Identifier.parse(itemName));
    }
}