package com.coolerpromc.unstriplog.platform.util;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public interface CreativeTabOutput {
    void accept(ItemStack stack, CreativeModeTab.TabVisibility visibility);

    default void accept(ItemStack stack) {
        accept(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }
}
