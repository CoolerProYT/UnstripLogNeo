package com.coolerpromc.unstriplog.component;

import com.coolerpromc.unstriplog.config.BarkTypeConfig;
import com.coolerpromc.unstriplog.platform.Services;
import com.coolerpromc.unstriplog.platform.util.RegistryHandler;
import net.minecraft.core.component.DataComponentType;

public class ModDataComponents {
    public static final RegistryHandler<DataComponentType<BarkTypeConfig.BarkTypeEntry>> BARK_TYPE = Services.REGISTRY.registerDataComponent("bark_type", builder -> builder.persistent(BarkTypeConfig.BarkTypeEntry.CODEC).networkSynchronized(BarkTypeConfig.BarkTypeEntry.STREAM_CODEC));

    public static void load() {
    }
}
