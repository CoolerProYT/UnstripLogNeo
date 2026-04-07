package com.coolerpromc.unstriplog.item;

import com.coolerpromc.unstriplog.platform.Services;
import com.coolerpromc.unstriplog.platform.util.ItemRegistryHandler;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final ItemRegistryHandler<Item> BARK = Services.REGISTRY.registerItem("bark", BarkItem::new);

    public static void load() {
    }
}
