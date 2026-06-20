package com.mrwizard94.nodecore.worldgen;

import com.mrwizard94.nodecore.data.NodeSavedData;
import com.mrwizard94.nodecore.node.NodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Spacing gate for procedural node placement. {@link NodeLodBridge} calls this before registering LOD deposits.
 */
public final class NodeWorldgenStub {
    private NodeWorldgenStub() {}

    public static boolean shouldPlaceNode(ServerLevel level, BlockPos pos, NodeType type) {
        return !NodeSpacingHints.violatesMinSpacing(pos, NodeSavedData.get(level).getNodes());
    }
}