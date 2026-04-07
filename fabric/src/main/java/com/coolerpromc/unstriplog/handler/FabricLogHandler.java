package com.coolerpromc.unstriplog.handler;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class FabricLogHandler {
    public static void init() {
        AxeItem axeItem = (AxeItem) Items.COPPER_AXE;
        for (Block block : BuiltInRegistries.BLOCK) {
            Optional<BlockState> strippable = axeItem.getStripped(block.defaultBlockState());
            if (strippable.isPresent()) {
                LogHandler.LOGS.add(block);
                LogHandler.STRIPPED_LOG.put(strippable.get().getBlock(), block);
            }
        }
        AxeItem.STRIPPABLES.forEach((key, value) -> {
            if (!LogHandler.LOGS.contains(key) && !LogHandler.STRIPPED_LOG.containsKey(value)) {
                LogHandler.LOGS.add(key);
                LogHandler.STRIPPED_LOG.put(value, key);
            }
        });

        LogHandler.onCommonSetup();
        UseBlockCallback.EVENT.register(LogHandler::onStrip);
        UseBlockCallback.EVENT.register(LogHandler::onUnstrip);
    }
}
