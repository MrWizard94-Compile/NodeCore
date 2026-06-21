package com.mrwizard94.nodecore.event;

import com.mrwizard94.nodecore.block.node.OreNodeBlock;
import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.data.NodeSavedData;
import com.mrwizard94.nodecore.node.NodeType;
import com.mrwizard94.nodecore.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class OreNodeHandler {
    private OreNodeHandler() {}

    public static void onOreNodePlaced(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!state.is(ModBlocks.ORE_NODE.get())) {
            return;
        }

        NodeType type = state.getValue(OreNodeBlock.NODE_TYPE);
        int tier = state.getValue(OreNodeBlock.TIER);
        registerFromBlock(serverLevel, pos, type, tier);
    }

    public static void onOreNodeRemoved(Level level, BlockPos pos) {
        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) {
            return;
        }
        NodeSavedData.get(serverLevel).removeNodeAtCenter(pos);
    }

    public static void registerFromBlock(ServerLevel level, BlockPos pos, NodeType type, int tier) {
        NodeSavedData data = NodeSavedData.get(level);
        if (data.findByCenter(pos).isPresent()) {
            return;
        }

        int radius = type.getDefaultRadius() > 0
                ? type.getDefaultRadius()
                : NodeCoreConfig.DEFAULT_NODE_RADIUS.get();
        data.addNode(type, pos, radius, tier);
    }
}