package com.mrwizard94.nodecore.event;

import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.data.NodeSavedData;
import com.mrwizard94.nodecore.node.NodeType;
import com.mrwizard94.nodecore.node.ResourceNode;
import com.mrwizard94.nodecore.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class NodeMarkerHandler {
    private NodeMarkerHandler() {}

    public static void onMarkerPlaced(Level level, BlockPos pos) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        NodeSavedData data = NodeSavedData.get(serverLevel);
        if (data.findByCenter(pos).isPresent()) {
            return;
        }

        NodeType type = resolveMarkerType();
        int radius = type.getDefaultRadius() > 0
                ? type.getDefaultRadius()
                : NodeCoreConfig.DEFAULT_NODE_RADIUS.get();

        ResourceNode node = data.addNode(type, pos, radius);
        NodeCore.LOGGER.debug("Marker at {} registered {} node [{}]",
                pos, type.getId(), node.getId());
    }

    public static void onMarkerRemoved(Level level, BlockPos pos) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (NodeSavedData.get(serverLevel).removeNodeAtCenter(pos)) {
            NodeCore.LOGGER.debug("Marker at {} removed linked node", pos);
        }
    }

    public static boolean isNodeMarker(BlockState state) {
        return state.is(ModBlocks.NODE_MARKER.get());
    }

    private static NodeType resolveMarkerType() {
        try {
            return NodeType.byId(NodeCoreConfig.MARKER_DEFAULT_NODE_TYPE.get());
        } catch (IllegalArgumentException ex) {
            NodeCore.LOGGER.warn("Invalid markerDefaultType '{}', falling back to ore_iron",
                    NodeCoreConfig.MARKER_DEFAULT_NODE_TYPE.get());
            return NodeType.ORE_IRON;
        }
    }
}