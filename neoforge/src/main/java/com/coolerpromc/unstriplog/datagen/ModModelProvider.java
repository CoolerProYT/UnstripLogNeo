package com.coolerpromc.unstriplog.datagen;

import com.coolerpromc.unstriplog.Constants;
import com.coolerpromc.unstriplog.client.BarkItemRenderer;
import com.coolerpromc.unstriplog.item.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.data.PackOutput;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, Constants.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.itemModelOutput.accept(ModItems.BARK.get(), ItemModelUtils.specialModel(modLocation("item/bark"), new BarkItemRenderer.Unbaked()));
    }
}
