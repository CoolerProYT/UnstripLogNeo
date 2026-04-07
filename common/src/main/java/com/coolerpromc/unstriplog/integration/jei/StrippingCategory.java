package com.coolerpromc.unstriplog.integration.jei;

import com.coolerpromc.unstriplog.Constants;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

public record StrippingCategory(IGuiHelper guiHelper) implements IRecipeCategory<StrippingRecipe> {
    public static final IRecipeType<StrippingRecipe> TYPE = IRecipeType.create(Constants.MODID, "stripping", StrippingRecipe.class);

    @Override
    public IRecipeType<StrippingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("unstriplog.jei.stripping");
    }

    @Override
    public int getWidth() {
        return 105;
    }

    @Override
    public int getHeight() {
        return 36;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return guiHelper.createDrawableItemLike(Items.IRON_AXE);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, StrippingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 0, 9).addItemStacks(UnstripLogJEIPlugin.axes());
        builder.addInputSlot(18, 9).setStandardSlotBackground().add(recipe.log());
        builder.addOutputSlot(69, 9).setStandardSlotBackground().add(recipe.strippedLog());
        builder.addOutputSlot(87, 9).setStandardSlotBackground().add(recipe.barkDrop());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, StrippingRecipe recipe, IFocusGroup focuses) {
        builder.addRecipeArrow().setPosition(40, 9);
    }
}

