package com.coolerpromc.unstriplog.component;

import com.coolerpromc.unstriplog.UnstripLog;
import com.coolerpromc.unstriplog.config.BarkTypeConfig;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, UnstripLog.MODID);

    public static final Supplier<DataComponentType<BarkTypeConfig.BarkTypeEntry>> BARK_TYPE = COMPONENTS.registerComponentType("bark_type", builder -> builder.persistent(BarkTypeConfig.BarkTypeEntry.CODEC).networkSynchronized(BarkTypeConfig.BarkTypeEntry.STREAM_CODEC));

    public static void register(IEventBus eventBus){
        COMPONENTS.register(eventBus);
    }
}
