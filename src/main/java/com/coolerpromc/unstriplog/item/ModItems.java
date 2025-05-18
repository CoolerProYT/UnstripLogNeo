package com.coolerpromc.unstriplog.item;

import com.coolerpromc.unstriplog.UnstripLog;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(UnstripLog.MODID);

    public static final DeferredItem<Item> BARK = ITEMS.registerItem("bark", BarkItem::new);

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
