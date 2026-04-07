package com.coolerpromc.unstriplog.platform.services;

import com.coolerpromc.unstriplog.Constants;
import com.coolerpromc.unstriplog.platform.util.ItemRegistryHandler;
import com.coolerpromc.unstriplog.platform.util.RegistryHandler;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public interface IRegistryHelper {
    <T extends Item> ItemRegistryHandler<T> registerItem(String name, Function<Item.Properties, T> func);
    <T> RegistryHandler<DataComponentType<T>> registerDataComponent(String name, UnaryOperator<DataComponentType.Builder<T>> builder);

    static ResourceKey<Item> itemKey(String name) {
        return ResourceKey.create(Registries.ITEM, Constants.id(name));
    }
}
