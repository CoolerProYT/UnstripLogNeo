package com.coolerpromc.unstriplog.platform.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public interface ItemRegistryHandler<T extends Item> extends RegistryHandler<T>, ItemLike {
}
