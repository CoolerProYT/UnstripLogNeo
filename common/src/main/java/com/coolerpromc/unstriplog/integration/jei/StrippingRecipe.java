package com.coolerpromc.unstriplog.integration.jei;

import net.minecraft.world.item.ItemStack;

public record StrippingRecipe(ItemStack log, ItemStack strippedLog, ItemStack barkDrop) {
}

