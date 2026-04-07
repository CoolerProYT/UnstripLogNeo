package com.coolerpromc.unstriplog.platform;

import com.coolerpromc.unstriplog.Constants;
import com.coolerpromc.unstriplog.platform.services.IRegistryHelper;
import com.coolerpromc.unstriplog.platform.util.ItemRegistryHandler;
import com.coolerpromc.unstriplog.platform.util.RegistryHandler;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class FabricRegistryHelper implements IRegistryHelper {
    @Override
    public <T extends Item> ItemRegistryHandler<T> registerItem(String name, Function<Item.Properties, T> func) {
        ResourceKey<Item> key = IRegistryHelper.itemKey(name);
        Identifier id = key.identifier();
        Holder<T> holder = Registry.registerForHolder(BuiltInRegistries.ITEM, id, func.apply(new Item.Properties().setId(key)));

        return new ItemRegistryHandler<>() {
            @Override
            public Item asItem() {
                return get();
            }

            @Override
            public Identifier id() {
                return id;
            }

            @Override
            public Holder<T> holder() {
                return holder;
            }

            @Override
            public T get() {
                return holder.value();
            }
        };
    }

    @Override
    public <T> RegistryHandler<DataComponentType<T>> registerDataComponent(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        Identifier id = Constants.id(name);
        Holder<DataComponentType<T>> holder = Registry.registerForHolder(BuiltInRegistries.DATA_COMPONENT_TYPE, id, builder.apply(DataComponentType.builder()).build());

        return new RegistryHandler<>() {
            @Override
            public Identifier id() {
                return id;
            }

            @Override
            public Holder<DataComponentType<T>> holder() {
                return holder;
            }

            @Override
            public DataComponentType<T> get() {
                return holder.value();
            }
        };
    }
}
