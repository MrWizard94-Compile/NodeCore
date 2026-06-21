package com.mrwizard94.nodecore.worldgen;

import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.block.node.OreNodeBlock;
import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.data.NodeSavedData;
import com.mrwizard94.nodecore.event.OreNodeHandler;
import com.mrwizard94.nodecore.node.NodeType;
import com.mrwizard94.nodecore.registry.ModBlocks;
import net.minecraft.core.BlockPos;
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

    public static void placeOreNode(ServerLevel level, BlockPos pos, NodeType type, int tier) {
        BlockState state = ModBlocks.ORE_NODE.get().defaultBlockState()
                .setValue(OreNodeBlock.NODE_TYPE, type)
                .setValue(OreNodeBlock.TIER, tier);
        level.setBlock(pos, state, Block.UPDATE_ALL);
        OreNodeHandler.registerFromBlock(level, pos, type, tier);
        NodeCore.LOGGER.debug("Placed ore node {} tier {} at {}", type.getId(), tier, pos);
    }
}