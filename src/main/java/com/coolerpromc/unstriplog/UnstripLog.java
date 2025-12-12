package com.coolerpromc.unstriplog;

import com.coolerpromc.unstriplog.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.ArrayList;
import java.util.List;

@Mod(UnstripLog.MODID)
public class UnstripLog
{
    public static final String MODID = "unstriplog";

    public UnstripLog(IEventBus modEventBus, ModContainer modContainer)
    {
        ModItems.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.BARK);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }

    @SubscribeEvent
    public void onUseItemOnBlock(PlayerInteractEvent.RightClickBlock event) {
        List<Block> LOGS = new ArrayList<>();

        for (Block block : BuiltInRegistries.BLOCK) {
            Identifier id = BuiltInRegistries.BLOCK.getKey(block);
            String path = id.getPath();

            if ((path.endsWith("_log") || path.endsWith("_wood") || path.endsWith("stem") || path.endsWith("hyphae")) && !path.startsWith("stripped_")) {
                LOGS.add(block);
            }
        }

        if (event.getLevel().isClientSide()){
            event.setCancellationResult(InteractionResult.PASS);
            return;
        }

        if (event.getHand() != InteractionHand.MAIN_HAND){
            event.setCancellationResult(InteractionResult.PASS);
            return;
        }

        if (event.getEntity().getOffhandItem().getItem() == Items.SHIELD && !event.getEntity().isShiftKeyDown()){
            event.setCancellationResult(InteractionResult.PASS);
            return;
        }

        if (!(event.getItemStack().getItem() instanceof AxeItem)){
            event.setCancellationResult(InteractionResult.PASS);
            return;
        }

        BlockPos pos = event.getPos();
        BlockState state = event.getLevel().getBlockState(pos);
        Block block = state.getBlock();

        if (LOGS.contains(block)){
            ItemEntity drop = new ItemEntity(event.getLevel(), pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, new ItemStack(ModItems.BARK.get()));
            event.getLevel().addFreshEntity(drop);
        }

        event.setCancellationResult(InteractionResult.PASS);
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }
}
