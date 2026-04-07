package com.coolerpromc.unstriplog;

import com.coolerpromc.unstriplog.component.ModDataComponents;
import com.coolerpromc.unstriplog.config.RuntimeConfigAccess;
import com.coolerpromc.unstriplog.item.ModItems;
import com.coolerpromc.unstriplog.platform.util.CreativeTabOutput;
import net.minecraft.world.item.ItemStack;

public class CommonClass {
    public static void init() {
        ModItems.load();
        ModDataComponents.load();
    }

    public static void addCreative(CreativeTabOutput output) {
        RuntimeConfigAccess.barkTypes().forEach(entry -> {
            ItemStack stack = ModItems.BARK.asItem().getDefaultInstance();
            stack.set(ModDataComponents.BARK_TYPE.get(), entry);
            output.accept(stack);
        });
    }
}