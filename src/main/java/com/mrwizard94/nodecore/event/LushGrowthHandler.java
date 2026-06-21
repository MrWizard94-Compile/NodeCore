package com.mrwizard94.nodecore.event;

import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.integration.dynamictrees.DynamicTreesGrowthBoost;
import com.mrwizard94.nodecore.node.NodeQueries;
import com.mrwizard94.nodecore.util.OrganicBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Accelerates random-tick plant growth inside lush cavern volumes (3D SavedData membership).
 */
public final class LushGrowthHandler {
    private static final BlockPos.MutableBlockPos MUTABLE = new BlockPos.MutableBlockPos();

    @SubscribeEvent
    public void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (!NodeCoreConfig.LUSH_GROWTH_ENABLED.get()) {
            return;
        }
        if (!(event.level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (serverLevel.players().isEmpty()) {
            return;
        }

        int interval = NodeCoreConfig.LUSH_GROWTH_INTERVAL_TICKS.get();
        if (interval <= 0 || serverLevel.getGameTime() % interval != 0) {
            return;
        }

        int samples = NodeCoreConfig.LUSH_GROWTH_SAMPLES_PER_PASS.get();
        int baseExtraTicks = NodeCoreConfig.LUSH_GROWTH_EXTRA_RANDOM_TICKS.get();
        int scanRadius = NodeCoreConfig.LUSH_GROWTH_SCAN_RADIUS.get();
        Map<ResourceLocation, Integer> multipliers = parseBlockMultipliers();

        RandomSource random = serverLevel.getRandom();
        for (int sampleIndex = 0; sampleIndex < samples; sampleIndex++) {
            BlockPos anchor = pickSampleAnchor(serverLevel, random);
            if (anchor == null || !NodeQueries.isInLushCavern(serverLevel, anchor)) {
                continue;
            }
            boostAround(serverLevel, anchor, scanRadius, baseExtraTicks, multipliers, random);
        }
    }

    private static BlockPos pickSampleAnchor(ServerLevel level, RandomSource random) {
        List<ServerPlayer> players = level.players();
        if (players.isEmpty()) {
            return null;
        }

        ServerPlayer player = players.get(random.nextInt(players.size()));
        int radius = NodeCoreConfig.LUSH_GROWTH_SAMPLE_RADIUS.get();
        int x = player.blockPosition().getX() + random.nextInt(radius * 2 + 1) - radius;
        int z = player.blockPosition().getZ() + random.nextInt(radius * 2 + 1) - radius;
        int minY = NodeCoreConfig.LUSH_GROWTH_MIN_Y.get();
        int maxY = NodeCoreConfig.LUSH_GROWTH_MAX_Y.get();
        if (minY > maxY) {
            int swap = minY;
            minY = maxY;
            maxY = swap;
        }
        int y = minY + random.nextInt(maxY - minY + 1);

        if (!level.hasChunk(x >> 4, z >> 4)) {
            return null;
        }

        return new BlockPos(x, y, z);
    }

    private static void boostAround(
            ServerLevel level,
            BlockPos anchor,
            int scanRadius,
            int baseExtraTicks,
            Map<ResourceLocation, Integer> multipliers,
            RandomSource random
    ) {
        int minX = anchor.getX() - scanRadius;
        int maxX = anchor.getX() + scanRadius;
        int minY = anchor.getY() - scanRadius;
        int maxY = anchor.getY() + scanRadius;
        int minZ = anchor.getZ() - scanRadius;
        int maxZ = anchor.getZ() + scanRadius;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                if (!level.hasChunk(x >> 4, z >> 4)) {
                    continue;
                }
                LevelChunk chunk = level.getChunk(x >> 4, z >> 4);
                for (int y = minY; y <= maxY; y++) {
                    MUTABLE.set(x, y, z);
                    if (!NodeQueries.isInLushCavern(level, MUTABLE)) {
                        continue;
                    }
                    BlockState state = chunk.getBlockState(MUTABLE);
                    if (!isGrowable(state)) {
                        continue;
                    }
                    int extraTicks = resolveExtraTicks(state, baseExtraTicks, multipliers);
                    if (extraTicks <= 0) {
                        continue;
                    }
                    applyGrowthBoost(level, MUTABLE, state, extraTicks, random);
                }
            }
        }
    }

    private static boolean isGrowable(BlockState state) {
        return OrganicBlocks.isOrganicBlockState(state) || state.isRandomlyTicking();
    }

    private static int resolveExtraTicks(
            BlockState state,
            int baseExtraTicks,
            Map<ResourceLocation, Integer> multipliers
    ) {
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (blockId == null) {
            return baseExtraTicks;
        }
        Integer tuned = multipliers.get(blockId);
        if (tuned == null) {
            return baseExtraTicks;
        }
        return Math.max(0, tuned);
    }

    private static void applyGrowthBoost(
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            int extraTicks,
            RandomSource random
    ) {
        if (DynamicTreesGrowthBoost.tryAccelerateGrowth(level, pos, state, random, extraTicks)) {
            return;
        }

        if (state.isRandomlyTicking()) {
            BlockState current = state;
            for (int tick = 0; tick < extraTicks; tick++) {
                current.randomTick(level, pos, random);
                current = level.getBlockState(pos);
                if (!current.isRandomlyTicking()) {
                    break;
                }
            }
            return;
        }

        Block block = state.getBlock();
        if (block instanceof CropBlock crop && !crop.isMaxAge(state)) {
            level.setBlock(pos, crop.getStateForAge(crop.getAge(state) + 1), Block.UPDATE_CLIENTS);
        } else if (block instanceof SaplingBlock sapling) {
            sapling.advanceTree(level, pos, state, random);
        }
    }

    private static Map<ResourceLocation, Integer> parseBlockMultipliers() {
        Map<ResourceLocation, Integer> multipliers = new HashMap<>();
        for (String entry : NodeCoreConfig.LUSH_GROWTH_BLOCK_MULTIPLIERS.get()) {
            if (entry == null || entry.isBlank()) {
                continue;
            }
            int separator = entry.indexOf('=');
            if (separator <= 0 || separator >= entry.length() - 1) {
                continue;
            }
            ResourceLocation blockId = ResourceLocation.tryParse(entry.substring(0, separator).trim());
            if (blockId == null) {
                continue;
            }
            try {
                multipliers.put(blockId, Integer.parseInt(entry.substring(separator + 1).trim()));
            } catch (NumberFormatException ignored) {
                // Skip malformed tuning entries.
            }
        }
        return multipliers;
    }
}