package com.coolerpromc.unstriplog.handler;

import com.coolerpromc.unstriplog.config.BarkTypeConfig;
import com.coolerpromc.unstriplog.config.RuntimeConfigAccess;
import com.coolerpromc.unstriplog.config.UnstripDetailedConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.*;

@SuppressWarnings("deprecation")
public class LogHandler {
    public static List<Block> LOGS = new ArrayList<>();
    public static final Map<Block, String> BARK_TYPE = new HashMap<>();
    public static final Map<Block, Block> STRIPPED_LOG = new HashMap<>(); // stripped -> log

    public static void onCommonSetup() {
        LOGS.forEach(block -> {
            if(!block.builtInRegistryHolder().key().identifier().getNamespace().equals("minecraft")) return;
            String name = block.builtInRegistryHolder().key().identifier().getPath().replace("_wood", "").replace("_log", "").replace("_block", "").replace("_stem", "").replace("_hyphae", "");
            BARK_TYPE.put(block, name);
        });
        BarkTypeConfig.init();
        UnstripDetailedConfig.init();
    }

    public static InteractionResult onStrip(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack itemStack = player.getItemInHand(hand);
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (level.isClientSide()){
            return InteractionResult.PASS;
        }

        if (hand != InteractionHand.MAIN_HAND){
            return InteractionResult.PASS;
        }

        if (player.getOffhandItem().getItem() == Items.SHIELD && !player.isShiftKeyDown()){
            return InteractionResult.PASS;
        }

        if (!(itemStack.getItem() instanceof AxeItem)){
            return InteractionResult.PASS;
        }

        Block block = state.getBlock();

        Optional<RuntimeConfigAccess.RuntimeLogEntry> optional = RuntimeConfigAccess.byBase(block);
        if (optional.isPresent()){
            RuntimeConfigAccess.RuntimeLogEntry entry = optional.get();
            ItemEntity drop = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, entry.drop().copy());
            level.addFreshEntity(drop);
            return InteractionResult.PASS;
        }

        if (LOGS.contains(block) && RuntimeConfigAccess.allowUnknownLog()){
            ItemEntity drop = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, new ItemStack(RuntimeConfigAccess.barkItem()));
            level.addFreshEntity(drop);
        }

        return InteractionResult.PASS;
    }

    public static InteractionResult onUnstrip(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack useItem = player.getItemInHand(hand);
        BlockPos pos = hitResult.getBlockPos();
        BlockState targetBlock = level.getBlockState(pos);
        if (level.isClientSide()) {
            return InteractionResult.PASS;
        }

        if (!isUnstrippable(targetBlock)){
            return InteractionResult.PASS;
        }
        if (!ItemStack.isSameItemSameComponents(useItem, unstripItem(targetBlock))){
            return InteractionResult.PASS;
        }
        BlockState log = unstripped(targetBlock);

        level.setBlock(pos, log, Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0f, 1.0f);

        useItem.shrink(1);

        return InteractionResult.SUCCESS;
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
