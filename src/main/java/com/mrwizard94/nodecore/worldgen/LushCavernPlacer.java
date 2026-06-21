package com.mrwizard94.nodecore.worldgen;

import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.data.LushCavernSavedData;
import com.mrwizard94.nodecore.data.LushCavernVolume;
import com.mrwizard94.nodecore.data.NodeSavedData;
import com.mrwizard94.nodecore.integration.dynamictrees.DynamicTreesBridge;
import com.mrwizard94.nodecore.network.NodeCoreNetwork;
import com.mrwizard94.nodecore.node.NodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.ChunkPos;

import java.util.Optional;

public final class LushCavernPlacer {
    private LushCavernPlacer() {
    }

    public static void generateForChunk(ServerLevel level, ChunkPos chunkPos) {
        if (!NodeCoreConfig.LUSH_CAVERN_WORLDGEN_ENABLED.get()) {
            return;
        }
        if (level.dimension() != Level.OVERWORLD) {
            return;
        }

        int spacing = LushCavernGrid.gridSpacing();
        if (spacing <= 0) {
            return;
        }

        int minX = chunkPos.getMinBlockX();
        int maxX = chunkPos.getMaxBlockX();
        int minZ = chunkPos.getMinBlockZ();
        int maxZ = chunkPos.getMaxBlockZ();
        int searchRadius = NodeCoreConfig.LUSH_CAVERN_MAX_HORIZONTAL_RADIUS.get() + 16;

        int gridStartX = Math.floorDiv(minX - searchRadius, spacing) * spacing;
        int gridEndX = Math.floorDiv(maxX + searchRadius, spacing) * spacing;
        int gridStartZ = Math.floorDiv(minZ - searchRadius, spacing) * spacing;
        int gridEndZ = Math.floorDiv(maxZ + searchRadius, spacing) * spacing;

        for (int gridX = gridStartX; gridX <= gridEndX; gridX += spacing) {
            for (int gridZ = gridStartZ; gridZ <= gridEndZ; gridZ += spacing) {
                processGridPoint(level, chunkPos, gridX, gridZ);
            }
        }
    }

    private static void processGridPoint(ServerLevel level, ChunkPos chunkPos, int gridX, int gridZ) {
        Optional<LushCavernGrid.CavernSpec> specOptional = LushCavernGrid.specForGrid(level, gridX, gridZ);
        if (specOptional.isEmpty()) {
            return;
        }
        LushCavernGrid.CavernSpec spec = specOptional.get();
        if (!spec.toBounds().intersectsChunk(chunkPos.x, chunkPos.z)) {
            return;
        }
        carveAndDecorateChunk(level, chunkPos, spec);
        registerIfAnchor(level, chunkPos, gridX, gridZ, spec);
    }

    private static void registerIfAnchor(
            ServerLevel level,
            ChunkPos chunkPos,
            int gridX,
            int gridZ,
            LushCavernGrid.CavernSpec spec
    ) {
        if ((gridX >> 4) != chunkPos.x || (gridZ >> 4) != chunkPos.z) {
            return;
        }

        LushCavernSavedData data = LushCavernSavedData.get(level);
        if (data.findById(spec.id()).isPresent() || data.hasVolumeNear(spec.center(), 32)) {
            return;
        }

        LushCavernVolume volume = LushCavernVolume.fromCenterRadii(
                spec.id(),
                spec.center(),
                spec.radiusX(),
                spec.radiusY(),
                spec.radiusZ()
        );
        data.register(volume);
        registerTerritory(level, volume);
        NodeCoreNetwork.syncCavernVolume(level, volume);
        NodeCore.LOGGER.debug("Registered lush cavern {} at {}", volume.getId(), volume.center());
    }

    private static void registerTerritory(ServerLevel level, LushCavernVolume volume) {
        if (!NodeCoreConfig.LUSH_CAVERN_REGISTER_TERRITORY.get()) {
            return;
        }
        BlockPos center = volume.center();
        int radius = volume.horizontalRadius() + NodeCoreConfig.LUSH_CAVERN_TERRITORY_PADDING.get();
        if (NodeSavedData.get(level).findByCenter(center).isPresent()) {
            return;
        }
        NodeSavedData.get(level).addNode(NodeType.LUSH_HYDRO, center, radius);
    }

    private static void carveAndDecorateChunk(ServerLevel level, ChunkPos chunkPos, LushCavernGrid.CavernSpec spec) {
        ChunkAccess chunk = level.getChunk(chunkPos.x, chunkPos.z);
        RandomSource random = RandomSource.create(
                level.getSeed() ^ spec.id().getMostSignificantBits() ^ chunkPos.toLong()
        );

        int minX = Math.max(chunkPos.getMinBlockX(), spec.center().getX() - spec.radiusX());
        int maxX = Math.min(chunkPos.getMaxBlockX(), spec.center().getX() + spec.radiusX());
        int minY = Math.max(level.getMinBuildHeight(), spec.center().getY() - spec.radiusY());
        int maxY = Math.min(level.getMaxBuildHeight() - 1, spec.center().getY() + spec.radiusY());
        int minZ = Math.max(chunkPos.getMinBlockZ(), spec.center().getZ() - spec.radiusZ());
        int maxZ = Math.min(chunkPos.getMaxBlockZ(), spec.center().getZ() + spec.radiusZ());

        double invRadiusXSq = 1.0D / (double) (spec.radiusX() * spec.radiusX());
        double invRadiusYSq = 1.0D / (double) (spec.radiusY() * spec.radiusY());
        double invRadiusZSq = 1.0D / (double) (spec.radiusZ() * spec.radiusZ());

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    double dx = x - spec.center().getX();
                    double dy = y - spec.center().getY();
                    double dz = z - spec.center().getZ();
                    double dist = dx * dx * invRadiusXSq + dy * dy * invRadiusYSq + dz * dz * invRadiusZSq;
                    if (dist > 1.0D) {
                        continue;
                    }
                    cursor.set(x, y, z);
                    BlockState state = chunk.getBlockState(cursor);
                    if (!state.isAir() && isCarvable(state)) {
                        level.setBlock(cursor, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
                    }
                }
            }
        }

        decorateFlora(level, chunk, spec, random, minX, maxX, minY, maxY, minZ, maxZ);
    }

    private static void decorateFlora(
            ServerLevel level,
            ChunkAccess chunk,
            LushCavernGrid.CavernSpec spec,
            RandomSource random,
            int minX,
            int maxX,
            int minY,
            int maxY,
            int minZ,
            int maxZ
    ) {
        double floraDensity = NodeCoreConfig.LUSH_CAVERN_FLORA_DENSITY.get();
        double treeDensity = NodeCoreConfig.LUSH_CAVERN_TREE_DENSITY.get();
        double mossDensity = NodeCoreConfig.LUSH_CAVERN_MOSS_DENSITY.get();

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos above = new BlockPos.MutableBlockPos();

        for (int x = minX; x <= maxX; x += 2) {
            for (int z = minZ; z <= maxZ; z += 2) {
                for (int y = maxY; y >= minY; y--) {
                    cursor.set(x, y, z);
                    if (!chunk.getBlockState(cursor).isAir()) {
                        continue;
                    }
                    above.set(x, y + 1, z);
                    if (!chunk.getBlockState(above).isAir()) {
                        continue;
                    }
                    BlockPos floor = cursor.below();
                    if (!isSolidFloor(chunk, floor)) {
                        continue;
                    }

                    if (random.nextDouble() < floraDensity) {
                        level.setBlock(floor, Blocks.GRASS_BLOCK.defaultBlockState(), Block.UPDATE_CLIENTS);
                    }

                    if (random.nextDouble() < mossDensity) {
                        level.setBlock(cursor, Blocks.MOSS_CARPET.defaultBlockState(), Block.UPDATE_CLIENTS);
                        continue;
                    }

                    if (random.nextDouble() < floraDensity * 0.65D) {
                        Block flora = random.nextBoolean() ? Blocks.FERN : Blocks.GRASS;
                        level.setBlock(cursor, flora.defaultBlockState(), Block.UPDATE_CLIENTS);
                        continue;
                    }

                    if (random.nextDouble() < treeDensity && chunk.getBlockState(floor).is(Blocks.GRASS_BLOCK)) {
                        if (!DynamicTreesBridge.tryPlantTree(level, floor.above(), random)) {
                            level.setBlock(floor.above(), Blocks.OAK_SAPLING.defaultBlockState(), Block.UPDATE_CLIENTS);
                        }
                    }
                    break;
                }
            }
        }
    }

    private static boolean isSolidFloor(ChunkAccess chunk, BlockPos pos) {
        if (pos.getY() < chunk.getMinBuildHeight()) {
            return false;
        }
        BlockState state = chunk.getBlockState(pos);
        return state.is(Blocks.STONE)
                || state.is(Blocks.DEEPSLATE)
                || state.is(Blocks.DIRT)
                || state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.TUFF)
                || state.is(Blocks.ANDESITE)
                || state.is(Blocks.GRANITE)
                || state.is(Blocks.DIORITE);
    }

    private static boolean isCarvable(BlockState state) {
        return state.is(Blocks.STONE)
                || state.is(Blocks.DEEPSLATE)
                || state.is(Blocks.DIRT)
                || state.is(Blocks.GRAVEL)
                || state.is(Blocks.ANDESITE)
                || state.is(Blocks.GRANITE)
                || state.is(Blocks.DIORITE)
                || state.is(Blocks.TUFF)
                || state.is(Blocks.CALCITE)
                || state.is(Blocks.DRIPSTONE_BLOCK)
                || state.is(Blocks.POINTED_DRIPSTONE);
    }
}