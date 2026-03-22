package com.coolerpromc.unstriplog.config;

import com.coolerpromc.unstriplog.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class UnstripLogConfig
{
    private Item bark;
    private final ModConfigSpec.ConfigValue<String> item;
    private final ModConfigSpec.BooleanValue allowUnknownLog;

    public static final UnstripLogConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    static {
        Pair<UnstripLogConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(UnstripLogConfig::new);

        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    private static boolean validateItemId(final Object obj)
    {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    private UnstripLogConfig(ModConfigSpec.Builder builder) {
        item = builder.comment("The id of the item that will be used for stripped log drop and item to unstrip a log").define("item", ModItems.BARK.getId().toString(), UnstripLogConfig::validateItemId);
        allowUnknownLog = builder.comment("Allow log that undefined in unstrip-detailed.json to use default bark as strip drop and unstrip item. (Some log can be failed to found in this case)").define("allowUnknownLog", true);

        builder.build();
    }

    public static void onModConfigLoading(ModConfigEvent.Loading event) {
        updateCache(event);
    }

    public static void onModConfigReloading(ModConfigEvent.Reloading event) {
        updateCache(event);
    }

    private static void updateCache(ModConfigEvent event) {
        if (event.getConfig().getSpec() == CONFIG_SPEC){
            CONFIG.bark = BuiltInRegistries.ITEM.get(ResourceLocation.parse(CONFIG.item.get()));
        }
    }

    public Item getBark() {
        return bark;
    }

    public String getConfiguredItemId() {
        return item.get();
    }

    public boolean allowUnknownLog() {
        return allowUnknownLog.getAsBoolean();
    }
}
