package com.mrwizard94.nodecore.block.extractor;

import com.mrwizard94.nodecore.node.NodeQueries;
import com.mrwizard94.nodecore.node.ResourceNode;
import com.mrwizard94.nodecore.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public final class ExtractorMultiblockValidator {
    private ExtractorMultiblockValidator() {}

    public static Optional<ResourceNode> findAdjacentOreNode(ServerLevel level, BlockPos corePos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighbor = corePos.relative(direction);
            Optional<ResourceNode> node = NodeQueries.nodeAt(level, neighbor)
                    .filter(candidate -> candidate.getType().isOre());
            if (node.isPresent()) {
                return node;
            }
        }
        return Optional.empty();
    }

    public static boolean hasFrameRing(ServerLevel level, BlockPos corePos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState state = level.getBlockState(corePos.relative(direction));
            if (!state.is(ModBlocks.ORE_EXTRACTOR_FRAME.get())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isComplete(ServerLevel level, BlockPos corePos) {
        return findAdjacentOreNode(level, corePos).isPresent() && hasFrameRing(level, corePos);
    }
}