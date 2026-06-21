package com.mrwizard94.nodecore.worldgen;

import com.mrwizard94.nodecore.config.NodeCoreConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.Optional;
import java.util.UUID;

public final class LushCavernGrid {
    private static final long FREQUENCY_SALT = 0x4C7573684361764CL;
    private static final long SIZE_SALT = 0x43617653697A654CL;

    private LushCavernGrid() {
    }

    public record CavernSpec(UUID id, BlockPos center, int radiusX, int radiusY, int radiusZ) {
        public LushCavernVolumeBounds toBounds() {
            return new LushCavernVolumeBounds(
                    id,
                    center.getX() - radiusX,
                    center.getY() - radiusY,
                    center.getZ() - radiusZ,
                    center.getX() + radiusX,
                    center.getY() + radiusY,
                    center.getZ() + radiusZ
            );
        }
    }

    public record LushCavernVolumeBounds(UUID id, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        public boolean intersectsChunk(int chunkX, int chunkZ) {
            int chunkMinX = chunkX << 4;
            int chunkMaxX = chunkMinX + 15;
            int chunkMinZ = chunkZ << 4;
            int chunkMaxZ = chunkMinZ + 15;
            return maxX >= chunkMinX && minX <= chunkMaxX && maxZ >= chunkMinZ && minZ <= chunkMaxZ;
        }

        public BlockPos center() {
            return new BlockPos((minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2);
        }
    }

    public static Optional<CavernSpec> specForGrid(ServerLevel level, int gridX, int gridZ) {
        if (!NodeCoreConfig.LUSH_CAVERN_WORLDGEN_ENABLED.get()) {
            return Optional.empty();
        }
        if (!shouldPlaceAtGrid(level, gridX, gridZ)) {
            return Optional.empty();
        }

        long sizeSeed = mixSeed(level.getSeed() ^ SIZE_SALT, gridX, gridZ);
        int minRadiusX = NodeCoreConfig.LUSH_CAVERN_MIN_HORIZONTAL_RADIUS.get();
        int maxRadiusX = NodeCoreConfig.LUSH_CAVERN_MAX_HORIZONTAL_RADIUS.get();
        int minRadiusY = NodeCoreConfig.LUSH_CAVERN_MIN_VERTICAL_RADIUS.get();
        int maxRadiusY = NodeCoreConfig.LUSH_CAVERN_MAX_VERTICAL_RADIUS.get();

        int radiusX = rollBetween(sizeSeed, 0, minRadiusX, maxRadiusX);
        int radiusZ = rollBetween(sizeSeed >> 8, 1, minRadiusX, maxRadiusX);
        int radiusY = rollBetween(sizeSeed >> 16, 2, minRadiusY, maxRadiusY);

        int minY = NodeCoreConfig.LUSH_CAVERN_MIN_Y.get();
        int maxY = NodeCoreConfig.LUSH_CAVERN_MAX_Y.get();
        int centerY = rollBetween(sizeSeed >> 24, 3, minY + radiusY, maxY - radiusY);
        if (centerY >= 0) {
            centerY = Math.min(-4, maxY - radiusY);
        }

        UUID id = uuidForGrid(level, gridX, gridZ);
        BlockPos center = new BlockPos(gridX, centerY, gridZ);
        return Optional.of(new CavernSpec(id, center, radiusX, radiusY, radiusZ));
    }

    public static boolean shouldPlaceAtGrid(ServerLevel level, int gridX, int gridZ) {
        double frequency = NodeCoreConfig.LUSH_CAVERN_FREQUENCY.get();
        if (frequency <= 0.0D) {
            return false;
        }
        if (frequency >= 1.0D) {
            return true;
        }
        long hash = mixSeed(level.getSeed() ^ FREQUENCY_SALT, gridX, gridZ);
        double roll = (hash & 0xFFFFL) / 65535.0D;
        return roll < frequency;
    }

    public static int gridSpacing() {
        return NodeCoreConfig.LUSH_CAVERN_GRID_SPACING.get();
    }

    private static UUID uuidForGrid(ServerLevel level, int gridX, int gridZ) {
        long hash = mixSeed(level.getSeed() ^ 0x4361766E49644CL, gridX, gridZ);
        return new UUID(hash, mixSeed(hash, gridZ, gridX));
    }

    private static int rollBetween(long seed, int channel, int min, int max) {
        if (max <= min) {
            return min;
        }
        long mixed = seed ^ ((long) channel * 0x9E3779B97F4A7C15L);
        int span = max - min + 1;
        return min + (int) Math.floorMod(mixed, span);
    }

    private static long mixSeed(long seed, int x, int z) {
        long hash = seed;
        hash ^= (long) x * 0x9E3779B97F4A7C15L;
        hash ^= (long) z * 0xC2B2AE3D27D4EB4FL;
        hash ^= hash >>> 33;
        hash *= 0xff51afd7ed558ccdL;
        hash ^= hash >>> 33;
        hash *= 0xc4ceb9fe1a85ec53L;
        hash ^= hash >>> 33;
        return hash;
    }
}