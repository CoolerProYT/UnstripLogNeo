package com.coolerpromc.unstriplog.integration.jei;

import com.coolerpromc.unstriplog.Constants;
import com.coolerpromc.unstriplog.component.ModDataComponents;
import com.coolerpromc.unstriplog.config.RuntimeConfigAccess;
import com.coolerpromc.unstriplog.config.UnstripDetailedConfig;
import com.coolerpromc.unstriplog.handler.LogHandler;
import com.coolerpromc.unstriplog.item.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@JeiPlugin
public class UnstripLogJEIPlugin implements IModPlugin {
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new StrippingCategory(guiHelper), new UnstrippingCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<StrippingRecipe> strippingRecipes = new ArrayList<>();
        List<UnstrippingRecipe> unstrippingRecipes = new ArrayList<>();

        for (UnstripDetailedConfig.LogEntry entry : UnstripDetailedConfig.getEntries()) {
            Block base = entry.base();
            Block stripped = entry.stripped();
            ItemStack drop = new ItemStack(entry.drop().item(), 1, entry.drop().componentPatch());
            ItemStack unstripItem = entry.unstripItem().map(i -> new ItemStack(i.item(), 1, i.componentPatch())).orElse(drop.copy());

            strippingRecipes.add(new StrippingRecipe(new ItemStack(base), new ItemStack(stripped), drop));
            unstrippingRecipes.add(new UnstrippingRecipe(new ItemStack(stripped), unstripItem, new ItemStack(base)));
        }

        if (RuntimeConfigAccess.allowUnknownLog()) {
            ItemStack defaultBark = new ItemStack(RuntimeConfigAccess.barkItem());
            for (Block log : LogHandler.LOGS) {
                Optional<RuntimeConfigAccess.RuntimeLogEntry> configured = RuntimeConfigAccess.byBase(log);
                if (configured.isPresent()) continue;

                Block strippedBlock = null;
                for (var mapEntry : LogHandler.STRIPPED_LOG.entrySet()) {
                    if (mapEntry.getValue().equals(log)) {
                        strippedBlock = mapEntry.getKey();
                        break;
                    }
                }

                if (strippedBlock != null) {
                    strippingRecipes.add(new StrippingRecipe(new ItemStack(log), new ItemStack(strippedBlock), defaultBark.copy()));
                    unstrippingRecipes.add(new UnstrippingRecipe(new ItemStack(strippedBlock), defaultBark.copy(), new ItemStack(log)));
                }
            }
        }

        registration.addRecipes(StrippingCategory.TYPE, strippingRecipes);
        registration.addRecipes(UnstrippingCategory.TYPE, unstrippingRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(StrippingCategory.TYPE, axes().toArray(ItemStack[]::new));
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerFromDataComponentTypes(ModItems.BARK.get(), ModDataComponents.BARK_TYPE.get());
    }

    @Override
    public Identifier getPluginUid() {
        return Constants.id("jei_plugin");
    }

    public static List<ItemStack> axes(){
        return BuiltInRegistries.ITEM.stream().filter(item -> item instanceof AxeItem).map(Item::getDefaultInstance).toList();
    }
}
