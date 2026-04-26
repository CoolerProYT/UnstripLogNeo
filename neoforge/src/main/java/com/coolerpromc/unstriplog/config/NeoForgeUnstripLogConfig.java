package com.coolerpromc.unstriplog.config;

import com.coolerpromc.unstriplog.item.ModItems;
import com.coolerpromc.unstriplog.network.ConfigSyncManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;

public class NeoForgeUnstripLogConfig
{
    private final ModConfigSpec.ConfigValue<String> item;
    private final ModConfigSpec.BooleanValue allowUnknownLog;

    public static final NeoForgeUnstripLogConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    static {
        Pair<NeoForgeUnstripLogConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(NeoForgeUnstripLogConfig::new);

        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    private static boolean validateItemId(final Object obj)
    {
        if (!(obj instanceof String itemName)) return false;
        try {
            Identifier.parse(itemName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private NeoForgeUnstripLogConfig(ModConfigSpec.Builder builder) {
        item = builder.comment("The id of the item that will be used for stripped log drop and item to unstrip a log").define("item", ModItems.BARK.id().toString(), NeoForgeUnstripLogConfig::validateItemId);
        allowUnknownLog = builder.comment("Allow log that undefined in unstrip-detailed.json to use default bark as strip drop and unstrip item. (Some log can be failed to found in this case)").define("allowUnknownLog", true);

        builder.build();
    }

    public static void onModConfigLoading(ModConfigEvent.Loading event) {
        updateCache(event);
    }

    public static void onModConfigReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() != CONFIG_SPEC) {
            return;
        }

        updateCache(event);

        if (ServerLifecycleHooks.getCurrentServer() != null) {
            ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(ConfigSyncManager::sendTo);
        }
    }

    private static void updateCache(ModConfigEvent event) {
        UnstripLogConfig.updateCache(CONFIG.item.get(), CONFIG.allowUnknownLog.getAsBoolean());
    }
}
