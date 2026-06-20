package com.mrwizard94.nodecore.data;

import com.mrwizard94.nodecore.node.NodeType;
import com.mrwizard94.nodecore.node.ResourceNode;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NodeSavedData extends SavedData {
    private static final String DATA_NAME = "nodecore_nodes";
    private final List<ResourceNode> nodes = new ArrayList<>();

    public static NodeSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                NodeSavedData::load,
                NodeSavedData::new,
                DATA_NAME
        );
    }

    public NodeSavedData() {
    }

    public List<ResourceNode> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    public ResourceNode addNode(NodeType type, BlockPos center, int radius) {
        ResourceNode node = ResourceNode.create(type, center, radius);
        nodes.add(node);
        setDirty();
        return node;
    }

    public boolean removeNode(UUID id) {
        boolean removed = nodes.removeIf(node -> node.getId().equals(id));
        if (removed) {
            setDirty();
        }
        return removed;
    }

    public Optional<ResourceNode> findByCenter(BlockPos center) {
        return nodes.stream()
                .filter(node -> node.getCenter().equals(center))
                .findFirst();
    }

    public boolean removeNodeAtCenter(BlockPos center) {
        boolean removed = nodes.removeIf(node -> node.getCenter().equals(center));
        if (removed) {
            setDirty();
        }
        return removed;
    }

    public Optional<ResourceNode> findAt(BlockPos pos) {
        return nodes.stream().filter(node -> node.contains(pos)).findFirst();
    }

    public Optional<ResourceNode> findNearest(BlockPos pos, NodeType typeFilter) {
        ResourceNode best = null;
        double bestDistance = Double.MAX_VALUE;
        for (ResourceNode node : nodes) {
            if (typeFilter != null && node.getType() != typeFilter) {
                continue;
            }
            double distance = node.distanceTo(pos);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = node;
            }
        }
        return Optional.ofNullable(best);
    }

    public List<ResourceNode> within(BlockPos pos, int range) {
        List<ResourceNode> matches = new ArrayList<>();
        long rangeSq = (long) range * range;
        for (ResourceNode node : nodes) {
            if (node.getCenter().distSqr(pos) <= rangeSq) {
                matches.add(node);
            }
        }
        return matches;
    }

    public static NodeSavedData load(CompoundTag tag) {
        NodeSavedData data = new NodeSavedData();
        ListTag list = tag.getList("Nodes", Tag.TAG_COMPOUND);
        for (Tag entry : list) {
            data.nodes.add(ResourceNode.load((CompoundTag) entry));
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (ResourceNode node : nodes) {
            list.add(node.save());
        }
        tag.put("Nodes", list);
        return tag;
    }
}