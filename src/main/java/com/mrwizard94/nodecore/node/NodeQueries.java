package com.mrwizard94.nodecore.node;

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

    public static boolean isInOreNode(ServerLevel level, BlockPos pos) {
        return nodeAt(level, pos).map(n -> n.getType().isOre()).orElse(false);
    }
}