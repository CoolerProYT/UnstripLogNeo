package com.coolerpromc.unstriplog.handler;

import com.coolerpromc.unstriplog.Constants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Strippable;

@SuppressWarnings("deprecation")
@EventBusSubscriber(modid = Constants.MODID)
public class NeoForgeLogHandler {
    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            for (Block block : BuiltInRegistries.BLOCK) {
                try {
                    BlockState strippable = block.getToolModifiedState(block.defaultBlockState(), null, ItemAbilities.AXE_STRIP, false);
                    if (strippable != null) {
                        LogHandler.LOGS.add(block);
                        LogHandler.STRIPPED_LOG.put(strippable.getBlock(), block);
                    }
                } catch (Exception ignored) {
                    Strippable strippable = block.builtInRegistryHolder().getData(NeoForgeDataMaps.STRIPPABLES);
                    if (strippable != null) {
                        LogHandler.LOGS.add(block);
                        LogHandler.STRIPPED_LOG.put(strippable.strippedBlock(), block);
                    }
                }
            }
            AxeItem.STRIPPABLES.forEach((key, value) -> {
                if (!LogHandler.LOGS.contains(key) && !LogHandler.STRIPPED_LOG.containsKey(value)) {
                    LogHandler.LOGS.add(key);
                    LogHandler.STRIPPED_LOG.put(value, key);
                }
            });

            LogHandler.onCommonSetup();
        });
    }

    @SubscribeEvent
    public static void onStrip(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = LogHandler.onStrip(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
        event.setCancellationResult(result);
        if (result == InteractionResult.SUCCESS) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onUnstrip(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = LogHandler.onUnstrip(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
        event.setCancellationResult(result);
        if (result == InteractionResult.SUCCESS) event.setCanceled(true);
    }
}
