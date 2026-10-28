package com.mrwizard94.nodecore.worldgen;

import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.block.node.OreNodeBlock;
import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.data.NodeSavedData;
import com.mrwizard94.nodecore.event.OreNodeHandler;
import com.mrwizard94.nodecore.node.NodeType;
import com.mrwizard94.nodecore.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;

public final class OreNodePlacer {
    private OreNodePlacer() {}

    public static void generateForChunk(ServerLevel level, ChunkPos chunkPos) {
        if (!NodeCoreConfig.ORE_NODE_WORLDGEN_ENABLED.get()) {
            return;
        }
        if (level.dimension() != Level.OVERWORLD) {
            return;
        }

        int spacing = NodeSpacingHints.minSpacing();
        if (spacing <= 0) {
            return;
        }

        int minX = chunkPos.getMinBlockX();
        int maxX = chunkPos.getMaxBlockX();
        int minZ = chunkPos.getMinBlockZ();
        int maxZ = chunkPos.getMaxBlockZ();

        int gridStartX = Math.floorDiv(minX, spacing) * spacing;
        int gridStartZ = Math.floorDiv(minZ, spacing) * spacing;

        for (int gridX = gridStartX; gridX <= maxX; gridX += spacing) {
            for (int gridZ = gridStartZ; gridZ <= maxZ; gridZ += spacing) {
                if ((gridX >> 4) != chunkPos.x || (gridZ >> 4) != chunkPos.z) {
                    continue;
                }
                tryPlaceGridNode(level, chunkPos, gridX, gridZ);
            }
        }
    }

    private static void tryPlaceGridNode(ServerLevel level, ChunkPos chunkPos, int gridX, int gridZ) {
        if (!OreNodeClusterMap.shouldPlaceAtGrid(level, gridX, gridZ)) {
            return;
        }

        ChunkAccess chunk = level.getChunk(chunkPos.x, chunkPos.z);
        BlockPos surface = findPlacementPos(level, chunk, gridX, gridZ);
        if (surface == null) {
            return;
        }

        if (level.getBlockState(surface).is(ModBlocks.ORE_NODE.get())) {
            return;
        }
        if (NodeSavedData.get(level).findByCenter(surface).isPresent()) {
            return;
        }
        if (!NodeWorldgenStub.shouldPlaceNode(level, surface, null)) {
            return;
        }

        NodeType type = OreNodeClusterMap.resolveType(level, surface);
        int tier = OreNodeClusterMap.rollTier(level, surface);
        placeOreNode(level, surface, type, tier);
    }

    private static BlockPos findPlacementPos(ServerLevel level, ChunkAccess chunk, int x, int z) {
        int minY = NodeCoreConfig.ORE_NODE_MIN_Y.get();
        int maxY = NodeCoreConfig.ORE_NODE_MAX_Y.get();
        int surfaceY = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1;
        int y = Math.min(surfaceY, maxY);
        y = Math.max(y, minY);

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(x, y, z);
        for (int attempt = 0; attempt < 24; attempt++) {
            BlockState state = chunk.getBlockState(cursor);
            if (isReplaceableAnchor(state) && isSolidBelow(chunk, cursor)) {
                return cursor.immutable();
            }
            cursor.move(0, -1, 0);
            if (cursor.getY() < minY) {
                break;
            }
        }

        cursor.set(x, Math.max(minY, Math.min(surfaceY, maxY)), z);
        BlockState fallback = chunk.getBlockState(cursor);
        if (isReplaceableAnchor(fallback) || fallback.isAir()) {
            return cursor.immutable();
        }
        return null;
    }

    private static boolean isSolidBelow(ChunkAccess chunk, BlockPos.MutableBlockPos cursor) {
        BlockPos below = cursor.below();
        if (below.getY() < chunk.getMinBuildHeight()) {
            return false;
        }
        return chunk.getBlockState(below).isSolidRender(chunk, below);
    }

    private static boolean isReplaceableAnchor(BlockState state) {
        return state.isAir()
                || state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.DIRT)
                || state.is(Blocks.STONE)
                || state.is(Blocks.DEEPSLATE)
                || state.is(Blocks.SAND)
                || state.is(Blocks.GRAVEL);
    }

    private static final int CLUSTER_RADIUS_H = 7;
    private static final int CLUSTER_RADIUS_V = 5;
    private static final double RAW_ORE_FRACTION = 0.12D;

    public static void placeOreNode(ServerLevel level, BlockPos center, NodeType type, int tier) {
        BlockState nodeState = ModBlocks.ORE_NODE.get().defaultBlockState()
                .setValue(OreNodeBlock.NODE_TYPE, type)
                .setValue(OreNodeBlock.TIER, tier);

        // Dense ore body around the extraction core. Clamped to the center's own
        // chunk so we never write into neighbouring (possibly unloaded) chunks
        // during ChunkEvent.Load. The dead-world ore strip removes biome ore
        // FEATURES, not blocks we place directly, so this stays.
        Block oreBlock = clusterOreFor(type);
        Block rawBlock = rawOreFor(type);
        RandomSource random = level.getRandom();
        int cMinX = (center.getX() >> 4) << 4;
        int cMinZ = (center.getZ() >> 4) << 4;
        int cMaxX = cMinX + 15;
        int cMaxZ = cMinZ + 15;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -CLUSTER_RADIUS_H; dx <= CLUSTER_RADIUS_H; dx++) {
            for (int dz = -CLUSTER_RADIUS_H; dz <= CLUSTER_RADIUS_H; dz++) {
                for (int dy = -CLUSTER_RADIUS_V; dy <= CLUSTER_RADIUS_V; dy++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }
                    double norm = (double) (dx * dx + dz * dz) / (double) (CLUSTER_RADIUS_H * CLUSTER_RADIUS_H)
                            + (double) (dy * dy) / (double) (CLUSTER_RADIUS_V * CLUSTER_RADIUS_V);
                    if (norm > 1.0D) {
                        continue;
                    }
                    int x = center.getX() + dx;
                    int z = center.getZ() + dz;
                    if (x < cMinX || x > cMaxX || z < cMinZ || z > cMaxZ) {
                        continue;
                    }
                    cursor.set(x, center.getY() + dy, z);
                    if (cursor.getY() <= level.getMinBuildHeight() || cursor.getY() >= level.getMaxBuildHeight()) {
                        continue;
                    }
                    if (!isClusterReplaceable(level.getBlockState(cursor))) {
                        continue;
                    }
                    Block fill = (rawBlock != null && random.nextDouble() < RAW_ORE_FRACTION) ? rawBlock : oreBlock;
                    level.setBlock(cursor, fill.defaultBlockState(), Block.UPDATE_CLIENTS);
                }
            }
        }

        // Extraction core — the single registered, indestructible node fixture.
        level.setBlock(center, nodeState, Block.UPDATE_ALL);
        OreNodeHandler.registerFromBlock(level, center, type, tier);
        NodeCore.LOGGER.debug("Placed ore-node cluster {} tier {} at {}", type.getId(), tier, center);
    }

    private static boolean isClusterReplaceable(BlockState state) {
        return state.isAir()
                || state.is(Blocks.STONE)
                || state.is(Blocks.DEEPSLATE)
                || state.is(Blocks.DIRT)
                || state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.GRAVEL)
                || state.is(Blocks.SAND)
                || state.is(Blocks.ANDESITE)
                || state.is(Blocks.DIORITE)
                || state.is(Blocks.GRANITE)
                || state.is(Blocks.TUFF)
                || state.is(Blocks.WATER);
    }

    private static Block clusterOreFor(NodeType type) {
        return switch (type) {
            case ORE_IRON -> Blocks.DEEPSLATE_IRON_ORE;
            case ORE_COPPER, ORE_BRASS -> Blocks.DEEPSLATE_COPPER_ORE;
            case ORE_QUARTZ, QUARTZ_RIFT -> Blocks.NETHER_QUARTZ_ORE;
            default -> Blocks.DEEPSLATE_IRON_ORE;
        };
    }

    private static Block rawOreFor(NodeType type) {
        return switch (type) {
            case ORE_IRON -> Blocks.RAW_IRON_BLOCK;
            case ORE_COPPER, ORE_BRASS -> Blocks.RAW_COPPER_BLOCK;
            default -> null;
        };
    }
}