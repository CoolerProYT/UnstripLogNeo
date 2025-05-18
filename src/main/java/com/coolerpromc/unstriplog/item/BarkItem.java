package com.coolerpromc.unstriplog.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BarkItem extends Item {
    private static final Map<Block, Block> REVERSE_STRIPPED = new HashMap<>();

    static {
        buildReverseStrippedMap();
    }

    private static void buildReverseStrippedMap() {
        for (Block block : BuiltInRegistries.BLOCK) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
            String path = id.getPath(); // e.g. "stripped_oak_log"

            if (path.startsWith("stripped_") && (path.endsWith("_log") || path.endsWith("_wood") || path.endsWith("stem") || path.endsWith("hyphae"))) {
                // Make sure the length of the path is greater than "stripped_" to avoid substring error
                if (path.length() > "stripped_".length()) {
                    String originalPath = path.substring("stripped_".length());
                    ResourceLocation originalId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), originalPath);

                    Block original = BuiltInRegistries.BLOCK.get(originalId);
                    if (original != Blocks.AIR) {
                        REVERSE_STRIPPED.put(block, original);
                    }
                }
            }
        }
    }

    public BarkItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState targetState = level.getBlockState(pos);

        Block reversed = REVERSE_STRIPPED.get(targetState.getBlock());
        if (reversed != null){
            if (!level.isClientSide){
                BlockState newState = reversed.defaultBlockState();

                if (newState.hasProperty(RotatedPillarBlock.AXIS) && targetState.hasProperty(RotatedPillarBlock.AXIS)){
                    Direction.Axis axis = targetState.getValue(RotatedPillarBlock.AXIS);
                    newState = newState.setValue(RotatedPillarBlock.AXIS, axis);
                }

                level.setBlock(pos, newState, Block.UPDATE_ALL);
                level.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0f, 1.0f);

                context.getItemInHand().shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        return 150;
    }
}
