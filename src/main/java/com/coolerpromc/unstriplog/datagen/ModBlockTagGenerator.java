package com.coolerpromc.unstriplog.datagen;

import com.coolerpromc.unstriplog.UnstripLog;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, UnstripLog.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

    }
}
