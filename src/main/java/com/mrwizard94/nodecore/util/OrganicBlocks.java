package com.mrwizard94.nodecore.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class OrganicBlocks {
    private OrganicBlocks() {}

    public static boolean isPlantableItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return stack.is(Items.WHEAT_SEEDS)
                || stack.is(Items.BEETROOT_SEEDS)
                || stack.is(Items.MELON_SEEDS)
                || stack.is(Items.PUMPKIN_SEEDS)
                || stack.is(Items.CARROT)
                || stack.is(Items.POTATO)
                || stack.is(Items.NETHER_WART)
                || stack.is(Items.OAK_SAPLING)
                || stack.is(Items.SPRUCE_SAPLING)
                || stack.is(Items.BIRCH_SAPLING)
                || stack.is(Items.JUNGLE_SAPLING)
                || stack.is(Items.ACACIA_SAPLING)
                || stack.is(Items.DARK_OAK_SAPLING)
                || stack.is(Items.MANGROVE_PROPAGULE)
                || stack.is(Items.CHERRY_SAPLING)
                || stack.is(Items.SUGAR_CANE)
                || stack.is(Items.BAMBOO)
                || stack.is(Items.CACTUS)
                || stack.is(Items.SWEET_BERRIES);
    }

    public static boolean isCropOrPlantBlock(Block block) {
        return block instanceof CropBlock
                || block instanceof SaplingBlock
                || block instanceof BushBlock
                || block instanceof LeavesBlock
                || block == Blocks.SUGAR_CANE
                || block == Blocks.BAMBOO
                || block == Blocks.CACTUS
                || block == Blocks.SWEET_BERRY_BUSH
                || block == Blocks.NETHER_WART
                || block == Blocks.MOSS_CARPET
                || block == Blocks.GLOW_LICHEN;
    }

    public static boolean isOrganicBlockState(BlockState state) {
        return isCropOrPlantBlock(state.getBlock());
    }
}