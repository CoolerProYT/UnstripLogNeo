package com.coolerpromc.unstriplog.handler;

import com.coolerpromc.unstriplog.UnstripLog;
import com.coolerpromc.unstriplog.config.BarkTypeConfig;
import com.coolerpromc.unstriplog.config.RuntimeConfigAccess;
import com.coolerpromc.unstriplog.config.UnstripDetailedConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.*;

@SuppressWarnings("deprecation")
@EventBusSubscriber(modid = UnstripLog.MODID)
public class LogHandler {
    public static List<Block> LOGS = new ArrayList<>();
    public static final Map<Block, String> BARK_TYPE = new HashMap<>();
    public static final Map<Block, Block> STRIPPED_LOG = new HashMap<>(); // stripped -> log

    @SubscribeEvent
    public static void onFMLCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            for (Block block : BuiltInRegistries.BLOCK) {
                try {
                    BlockState strippable = block.getToolModifiedState(block.defaultBlockState(), null, ItemAbilities.AXE_STRIP, false);
                    if (strippable != null) {
                        LOGS.add(block);
                        STRIPPED_LOG.put(strippable.getBlock(), block);
                    }
                } catch (Exception ignored) {

                }
            }
            AxeItem.STRIPPABLES.forEach((key, value) -> {
                if (!LOGS.contains(key) && !STRIPPED_LOG.containsKey(value)) {
                    LOGS.add(key);
                    STRIPPED_LOG.put(value, key);
                }
            });
            LOGS.forEach(block -> {
                if(!block.builtInRegistryHolder().getKey().location().getNamespace().equals("minecraft")) return;
                String name = block.builtInRegistryHolder().getKey().location().getPath().replace("_wood", "").replace("_log", "").replace("_block", "").replace("_stem", "").replace("_hyphae", "");
                BARK_TYPE.put(block, name);
            });
            BarkTypeConfig.init();
            UnstripDetailedConfig.init();
        });
    }

    @SubscribeEvent
    public static void onStrip(PlayerInteractEvent.RightClickBlock event) {
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

        Optional<RuntimeConfigAccess.RuntimeLogEntry> optional = RuntimeConfigAccess.byBase(block);
        if (optional.isPresent()){
            RuntimeConfigAccess.RuntimeLogEntry entry = optional.get();
            ItemEntity drop = new ItemEntity(event.getLevel(), pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, entry.drop().copy());
            event.getLevel().addFreshEntity(drop);
            event.setCancellationResult(InteractionResult.PASS);
            return;
        }

        if (LOGS.contains(block) && RuntimeConfigAccess.allowUnknownLog()){
            ItemEntity drop = new ItemEntity(event.getLevel(), pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, new ItemStack(RuntimeConfigAccess.barkItem()));
            event.getLevel().addFreshEntity(drop);
        }

        event.setCancellationResult(InteractionResult.PASS);
    }

    @SubscribeEvent
    public static void onUnstrip(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) {
            event.setCancellationResult(InteractionResult.PASS);
            return;
        }

        Level level = event.getLevel();
        ItemStack useItem = event.getItemStack();
        BlockState targetBlock = level.getBlockState(event.getHitVec().getBlockPos());
        if (!isUnstrippable(targetBlock)){
            event.setCancellationResult(InteractionResult.PASS);
            return;
        }
        if (!ItemStack.isSameItemSameComponents(useItem, unstripItem(targetBlock))){
            event.setCancellationResult(InteractionResult.PASS);
            return;
        }
        BlockState log = unstripped(targetBlock);

        level.setBlock(event.getPos(), log, Block.UPDATE_ALL);
        level.playSound(null, event.getPos(), SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0f, 1.0f);

        useItem.shrink(1);

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    public static boolean isUnstrippable(BlockState block){
        return (STRIPPED_LOG.containsKey(block.getBlock()) && RuntimeConfigAccess.allowUnknownLog()) || RuntimeConfigAccess.byStripped(block.getBlock()).isPresent();
    }

    public static ItemStack unstripItem(BlockState block){
        Optional<RuntimeConfigAccess.RuntimeLogEntry> optional = RuntimeConfigAccess.byStripped(block.getBlock());
        if (optional.isPresent()){
            RuntimeConfigAccess.RuntimeLogEntry entry = optional.get();
            if (entry.unstripItem().isPresent()){
                return entry.unstripItem().get().copy();
            }
            else{
                return entry.drop().copy();
            }
        }
        return new ItemStack(RuntimeConfigAccess.barkItem());
    }

    public static BlockState unstripped(BlockState block){
        Optional<RuntimeConfigAccess.RuntimeLogEntry> optional = RuntimeConfigAccess.byStripped(block.getBlock());
        return optional.map(logEntry -> logEntry.base().defaultBlockState().setValue(RotatedPillarBlock.AXIS, block.getValue(RotatedPillarBlock.AXIS))).orElseGet(() -> STRIPPED_LOG.get(block.getBlock()).defaultBlockState().setValue(RotatedPillarBlock.AXIS, block.getValue(RotatedPillarBlock.AXIS)));
    }
}
