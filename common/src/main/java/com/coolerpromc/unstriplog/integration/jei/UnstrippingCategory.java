package com.coolerpromc.unstriplog.integration.jei;

import com.coolerpromc.unstriplog.Constants;
import com.coolerpromc.unstriplog.item.ModItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public record UnstrippingCategory(IGuiHelper guiHelper) implements IRecipeCategory<UnstrippingRecipe> {
    public static final IRecipeType<UnstrippingRecipe> TYPE = IRecipeType.create(Constants.MODID, "unstripping", UnstrippingRecipe.class);

    @Override
    public IRecipeType<UnstrippingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("unstriplog.jei.unstripping");
    }

    @Override
    public int getWidth() {
        return 89;
    }

    @Override
    public int getHeight() {
        return 36;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return guiHelper.createDrawableItemLike(ModItems.BARK);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, UnstrippingRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(3, 9).setStandardSlotBackground().add(recipe.bark());
        builder.addInputSlot(21, 9).setStandardSlotBackground().add(recipe.strippedLog());
        builder.addOutputSlot(70, 9).setStandardSlotBackground().add(recipe.log());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, UnstrippingRecipe recipe, IFocusGroup focuses) {
        builder.addRecipeArrow().setPosition(42, 9);
    }
}

