package com.mrwizard94.nodecore.node;

import com.mrwizard94.nodecore.data.LushCavernSavedData;
import com.mrwizard94.nodecore.data.NodeSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.Optional;

public final class NodeQueries {
    private NodeQueries() {
    }

    public static Optional<ResourceNode> nodeAt(ServerLevel level, BlockPos pos) {
        return NodeSavedData.get(level).findAt(pos);
    }

    public static Optional<ResourceNode> nearestOreNode(ServerLevel level, BlockPos pos) {
        return NodeSavedData.get(level).findNearest(pos, null)
                .filter(node -> node.getType().isOre());
    }

    public static Optional<ResourceNode> nearestLushNode(ServerLevel level, BlockPos pos) {
        return NodeSavedData.get(level).findNearest(pos, NodeType.LUSH_HYDRO);
    }

    public static boolean isInLushNode(ServerLevel level, BlockPos pos) {
        return nodeAt(level, pos).map(ResourceNode::isLush).orElse(false);
    }

    /**
     * Canonical 3D lush-cavern membership (axis-aligned volume from worldgen SavedData).
     * Child 6 tint queries and child 7 growth booster should use this for cavern volumes.
     */
    public static boolean isInLushCavern(ServerLevel level, BlockPos pos) {
        return LushCavernSavedData.get(level).contains(pos);
    }

    public static boolean isInOreNode(ServerLevel level, BlockPos pos) {
        return nodeAt(level, pos).map(n -> n.getType().isOre()).orElse(false);
    }

    public static int tierAt(ServerLevel level, BlockPos pos) {
        return nodeAt(level, pos).map(ResourceNode::getTier).orElse(0);
    }
}