package com.mrwizard94.nodecore.worldgen;

import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.node.ResourceNode;
import net.minecraft.core.BlockPos;

import java.util.List;

/**
 * Spacing helpers for future worldgen / Large Ore Deposits integration.
 * Datagen emits matching defaults in {@code data/nodecore/worldgen/node_spacing_hints.json}.
 */
public final class NodeSpacingHints {
    private NodeSpacingHints() {}

    public static int minSpacing() {
        return NodeCoreConfig.MIN_NODE_SPACING.get();
    }

    public static int maxSpacing() {
        return NodeCoreConfig.MAX_NODE_SPACING.get();
    }

    public static boolean violatesMinSpacing(BlockPos candidate, List<ResourceNode> existing) {
        long minDistSq = (long) minSpacing() * minSpacing();
        for (ResourceNode node : existing) {
            if (node.getCenter().distSqr(candidate) < minDistSq) {
                return true;
            }
        }
        return false;
    }
}