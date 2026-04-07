package com.coolerpromc.unstriplog.item;

import com.coolerpromc.unstriplog.component.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.FuelValues;
import org.jetbrains.annotations.Nullable;

public class BarkItem extends Item {

    public BarkItem(Properties properties) {
        super(properties);
    }

    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType, FuelValues fuelValues) {
        return 150;
    }

    @Override
    public Component getName(ItemStack itemStack) {
        if (itemStack.has(ModDataComponents.BARK_TYPE.get())){
            return Component.translatable("item.unstriplog." + itemStack.get(ModDataComponents.BARK_TYPE.get()).name() + "_bark");
        }
        return super.getName(itemStack);
    }
}
