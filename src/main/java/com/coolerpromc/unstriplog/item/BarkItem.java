package com.coolerpromc.unstriplog.item;

import com.coolerpromc.unstriplog.component.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

public class BarkItem extends Item {

    public BarkItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        return 150;
    }

    @Override
    public Component getName(ItemStack itemStack) {
        if (itemStack.has(ModDataComponents.BARK_TYPE)){
            return Component.translatable("item.unstriplog." + itemStack.get(ModDataComponents.BARK_TYPE).name() + "_bark");
        }
        return super.getName(itemStack);
    }
}
