package com.coolerpromc.unstriplog.datagen;

import com.coolerpromc.unstriplog.UnstripLog;
import com.coolerpromc.unstriplog.item.ModItems;
import com.coolerpromc.unstriplog.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, UnstripLog.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Items.TRANSFORMABLE_ITEMS)
                .add(ModItems.BARK.get());
    }
}
