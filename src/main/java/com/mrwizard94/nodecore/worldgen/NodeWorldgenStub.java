package com.mrwizard94.nodecore.worldgen;

import com.mrwizard94.nodecore.data.NodeSavedData;
import com.mrwizard94.nodecore.node.NodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Placeholder for procedural node placement. Large Ore Deposits wiring lands here in a later phase.
 */
public final class NodeWorldgenStub {
    private NodeWorldgenStub() {}

    public static boolean shouldPlaceNode(ServerLevel level, BlockPos pos, NodeType type) {
        return !NodeSpacingHints.violatesMinSpacing(pos, NodeSavedData.get(level).getNodes());
    }
}