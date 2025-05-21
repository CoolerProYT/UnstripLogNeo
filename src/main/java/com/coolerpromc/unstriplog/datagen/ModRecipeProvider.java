package com.coolerpromc.unstriplog.datagen;

import com.coolerpromc.unstriplog.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.PAPER, 1)
                .pattern("##")
                .define('#', ModItems.BARK)
                .unlockedBy(getHasName(ModItems.BARK), has(ModItems.BARK))
                .save(recipeOutput, "paper_from_bark");
    }
}
