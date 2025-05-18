package com.coolerpromc.unstriplog.datagen;

import com.coolerpromc.unstriplog.UnstripLog;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = UnstripLog.MODID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event){
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        event.addProvider(new ModRecipeProvider.Runner(packOutput, event.getLookupProvider()));

        ModBlockTagGenerator blockTagGenerator = event.addProvider(new ModBlockTagGenerator(packOutput, lookupProvider));
        event.addProvider(new ModItemTagProvider(packOutput, lookupProvider, blockTagGenerator.contentsGetter()));

    }
}