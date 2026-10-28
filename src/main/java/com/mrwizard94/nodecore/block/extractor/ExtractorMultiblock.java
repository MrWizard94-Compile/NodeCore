package com.mrwizard94.nodecore.block.extractor;

import com.mrwizard94.nodecore.integration.create.CreateKineticBridge;
import com.mrwizard94.nodecore.node.NodeQueries;
import com.mrwizard94.nodecore.node.ResourceNode;
import com.mrwizard94.nodecore.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class ExtractorMultiblock {
    private ExtractorMultiblock() {}

    public record Layout(BlockPos corePos, BlockPos nodePos, ResourceNode node, Set<BlockPos> framePositions) {
        public Set<BlockPos> assemblyPositions() {
            Set<BlockPos> positions = new HashSet<>(framePositions);
            positions.add(corePos);
            positions.add(nodePos);
            return positions;
        }

        public Set<BlockPos> interfacePositions() {
            Set<BlockPos> positions = new HashSet<>(framePositions);
            positions.add(corePos);
            return positions;
        }
    }

    public static Optional<Layout> findLayout(ServerLevel level, BlockPos corePos) {
        if (!level.getBlockState(corePos).is(ModBlocks.ORE_EXTRACTOR_CORE.get())) {
            return Optional.empty();
        }

        BlockPos nodePos = null;
        ResourceNode node = null;
        for (Direction direction : Direction.values()) {
            BlockPos candidatePos = corePos.relative(direction);
            BlockState candidateState = level.getBlockState(candidatePos);
            if (!candidateState.is(ModBlocks.ORE_NODE.get())) {
                continue;
            }

            Optional<ResourceNode> candidateNode = NodeQueries.nodeAt(level, candidatePos)
                    .filter(resourceNode -> resourceNode.getType().isOre());
            if (candidateNode.isPresent()) {
                nodePos = candidatePos;
                node = candidateNode.get();
                break;
            }
        }

        if (nodePos == null || node == null) {
            return Optional.empty();
        }

        Set<BlockPos> framePositions = requiredFramePositions(corePos, nodePos);
        for (BlockPos framePos : framePositions) {
            if (!level.getBlockState(framePos).is(ModBlocks.ORE_EXTRACTOR_FRAME.get())) {
                return Optional.empty();
            }
        }

        return Optional.of(new Layout(corePos.immutable(), nodePos.immutable(), node, framePositions));
    }

    public static Optional<BlockPos> findCore(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(ModBlocks.ORE_EXTRACTOR_CORE.get())) {
            return Optional.of(pos.immutable());
        }
        if (!state.is(ModBlocks.ORE_EXTRACTOR_FRAME.get())) {
            return Optional.empty();
        }

        for (Direction direction : Direction.values()) {
            BlockPos neighbor = pos.relative(direction);
            if (level.getBlockState(neighbor).is(ModBlocks.ORE_EXTRACTOR_CORE.get())) {
                return Optional.of(neighbor.immutable());
            }
        }

        for (Direction direction : Direction.values()) {
            BlockPos nodePos = pos.relative(direction);
            if (!level.getBlockState(nodePos).is(ModBlocks.ORE_NODE.get())) {
                continue;
            }
            for (Direction nodeDirection : Direction.values()) {
                BlockPos corePos = nodePos.relative(nodeDirection);
                if (level.getBlockState(corePos).is(ModBlocks.ORE_EXTRACTOR_CORE.get())) {
                    return Optional.of(corePos.immutable());
                }
            }
        }

        return Optional.empty();
    }

    public static float maxAssemblyKineticSpeed(Level level, Layout layout) {
        if (!CreateKineticBridge.isAvailable()) {
            return 0.0F;
        }

        float max = 0.0F;
        Set<BlockPos> internal = layout.assemblyPositions();
        for (BlockPos partPos : layout.interfacePositions()) {
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = partPos.relative(direction);
                if (internal.contains(neighborPos)) {
                    continue;
                }
                BlockEntity neighbor = level.getBlockEntity(neighborPos);
                if (neighbor == null) {
                    continue;
                }
                max = Math.max(max, CreateKineticBridge.readSpeed(neighbor));
            }
        }
        return max;
    }

    static Set<BlockPos> requiredFramePositions(BlockPos corePos, BlockPos nodePos) {
        Set<BlockPos> frames = new HashSet<>();
        Direction nodeToCore = connectionDirection(nodePos, corePos);

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (nodeToCore != null && direction == nodeToCore) {
                continue;
            }
            frames.add(nodePos.relative(direction).immutable());
        }

        Direction coreToNode = nodeToCore != null ? nodeToCore.getOpposite() : null;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (coreToNode != null && direction == coreToNode) {
                continue;
            }
            frames.add(corePos.relative(direction).immutable());
        }

        return frames;
    }

    private static Direction connectionDirection(BlockPos from, BlockPos to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        int dz = to.getZ() - from.getZ();
        if (dx == 0 && dy == 0 && dz == 0) {
            return null;
        }
        if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) != 1) {
            return null;
        }
        if (dx == 1) {
            return Direction.EAST;
        }
        if (dx == -1) {
            return Direction.WEST;
        }
        if (dy == 1) {
            return Direction.UP;
        }
        if (dy == -1) {
            return Direction.DOWN;
        }
        return dz == 1 ? Direction.SOUTH : Direction.NORTH;
    }
}