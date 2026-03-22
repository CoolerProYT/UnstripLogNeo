package com.coolerpromc.unstriplog;

import com.coolerpromc.unstriplog.component.ModDataComponents;
import com.coolerpromc.unstriplog.config.RuntimeConfigAccess;
import com.coolerpromc.unstriplog.config.UnstripLogConfig;
import com.coolerpromc.unstriplog.datagen.model.BarkItemRenderer;
import com.coolerpromc.unstriplog.item.ModItems;
import com.coolerpromc.unstriplog.network.ConfigSyncManager;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(UnstripLog.MODID)
public class UnstripLog
{
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "unstriplog";

    public UnstripLog(IEventBus modEventBus, ModContainer modContainer)
    {
        ModItems.register(modEventBus);
        ModDataComponents.register(modEventBus);

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(ConfigSyncManager::onRegisterPayloadHandlers);
        modEventBus.addListener(ConfigSyncManager::onConfigReload);
        modEventBus.addListener((ModConfigEvent.Loading event) -> UnstripLogConfig.onModConfigLoading(event));
        modEventBus.addListener((ModConfigEvent.Reloading event) -> UnstripLogConfig.onModConfigReloading(event));
        NeoForge.EVENT_BUS.addListener(ConfigSyncManager::onPlayerLogin);
        NeoForge.EVENT_BUS.addListener(ConfigSyncManager::onDatapackSync);
        modContainer.registerConfig(ModConfig.Type.COMMON, UnstripLogConfig.CONFIG_SPEC);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            RuntimeConfigAccess.barkTypes().forEach(entry -> {
                ItemStack stack = ModItems.BARK.toStack();
                stack.set(ModDataComponents.BARK_TYPE, entry);
                event.accept(stack);
            });
        }
    }

    public static Identifier id(String path){
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onRegisterSpecialModelRenderer(RegisterSpecialModelRendererEvent event) {
            event.register(id("bark"), BarkItemRenderer.Unbaked.MAP_CODEC);
        }
    }
}
