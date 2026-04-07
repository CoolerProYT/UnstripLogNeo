package com.coolerpromc.unstriplog.platform;

import com.coolerpromc.unstriplog.Constants;
import com.coolerpromc.unstriplog.platform.services.IRegistryHelper;
import com.coolerpromc.unstriplog.platform.util.ItemRegistryHandler;
import com.coolerpromc.unstriplog.platform.util.RegistryHandler;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class NeoForgeRegistryHelper implements IRegistryHelper {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MODID);
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Constants.MODID);

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
        DATA_COMPONENTS.register(eventBus);
    }

    @Override
    public <T extends Item> ItemRegistryHandler<T> registerItem(String name, Function<Item.Properties, T> func) {
        Identifier id = Constants.id(name);
        DeferredItem<T> item = ITEMS.registerItem(name, func);

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
                return (Holder<T>) item.getDelegate();
            }

            @Override
            public T get() {
                return item.get();
            }
        };
    }

    @Override
    public <T> RegistryHandler<DataComponentType<T>> registerDataComponent(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        Identifier id = Constants.id(name);
        DeferredHolder<DataComponentType<?>, DataComponentType<T>> type = DATA_COMPONENTS.registerComponentType(name, builder);

        return new RegistryHandler<>() {
            @Override
            public Identifier id() {
                return id;
            }

            @Override
            public Holder<DataComponentType<T>> holder() {
                return (Holder<DataComponentType<T>>) (Holder<?>) type.getDelegate();
            }

            @Override
            public DataComponentType<T> get() {
                return type.get();
            }
        };
    }
}
