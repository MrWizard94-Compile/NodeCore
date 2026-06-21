package com.mrwizard94.nodecore.integration.dynamictrees;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Soft-optional Dynamic Trees growth acceleration for lush-cavern booster sampling.
 */
public final class DynamicTreesGrowthBoost {
    private DynamicTreesGrowthBoost() {
    }

    public static boolean tryAccelerateGrowth(
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            RandomSource random,
            int extraTicks
    ) {
        if (!DynamicTreesModProbe.isLoaded()) {
            return false;
        }
        Block block = state.getBlock();
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
        if (blockId == null || !"dynamictrees".equals(blockId.getNamespace())) {
            return false;
        }
        if (!state.isRandomlyTicking()) {
            return false;
        }
        BlockState current = state;
        for (int tick = 0; tick < extraTicks; tick++) {
            current.randomTick(level, pos, random);
            current = level.getBlockState(pos);
            if (!current.isRandomlyTicking()) {
                break;
            }
        }
        return true;
    }
}