package com.coolerpromc.unstriplog.handler;

import com.coolerpromc.unstriplog.UnstripLog;
import com.coolerpromc.unstriplog.config.BarkTypeConfig;
import com.coolerpromc.unstriplog.config.UnstripDetailedConfig;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
@EventBusSubscriber(modid = UnstripLog.MODID, bus = EventBusSubscriber.Bus.MOD)
public class LogHandlerMod {
    public static List<Block> LOGS = new ArrayList<>();
    public static final Map<Block, String> BARK_TYPE = new HashMap<>();
    public static final Map<Block, Block> STRIPPED_LOG = new HashMap<>(); // stripped -> log

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            AxeItem.STRIPPABLES.forEach((key, value) -> {
                LOGS.add(key);
                STRIPPED_LOG.put(value, key);
            });
            LOGS.forEach(block -> {
                String name = block.builtInRegistryHolder().getKey().location().getPath().replace("_wood", "").replace("_log", "").replace("_block", "").replace("_stem", "").replace("_hyphae", "");
                BARK_TYPE.put(block, name);
            });
            BarkTypeConfig.init();
            UnstripDetailedConfig.init();
        });
    }
}
