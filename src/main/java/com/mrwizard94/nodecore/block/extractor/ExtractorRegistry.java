package com.mrwizard94.nodecore.block.extractor;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class ExtractorRegistry extends SavedData {
    private static final String DATA_NAME = "nodecore_extractors";
    private final Map<UUID, BlockPos> nodeToExtractor = new HashMap<>();
    private final Map<Long, UUID> positionToNode = new HashMap<>();

    public static ExtractorRegistry get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                ExtractorRegistry::load,
                ExtractorRegistry::new,
                DATA_NAME
        );
    }

    public Optional<BlockPos> findExtractorForNode(UUID nodeId) {
        return Optional.ofNullable(nodeToExtractor.get(nodeId));
    }

    public Optional<UUID> findNodeForExtractor(BlockPos pos) {
        return Optional.ofNullable(positionToNode.get(pos.asLong()));
    }

    public boolean register(UUID nodeId, BlockPos extractorPos) {
        if (nodeToExtractor.containsKey(nodeId)) {
            BlockPos existing = nodeToExtractor.get(nodeId);
            if (!existing.equals(extractorPos)) {
                return false;
            }
            return true;
        }

        if (positionToNode.containsKey(extractorPos.asLong())) {
            return false;
        }

        nodeToExtractor.put(nodeId, extractorPos.immutable());
        positionToNode.put(extractorPos.asLong(), nodeId);
        setDirty();
        return true;
    }

    public void unregister(BlockPos extractorPos) {
        UUID nodeId = positionToNode.remove(extractorPos.asLong());
        if (nodeId != null) {
            nodeToExtractor.remove(nodeId);
            setDirty();
        }
    }

    public void unregisterNode(UUID nodeId) {
        BlockPos pos = nodeToExtractor.remove(nodeId);
        if (pos != null) {
            positionToNode.remove(pos.asLong());
            setDirty();
        }
    }

    public static ExtractorRegistry load(CompoundTag tag) {
        ExtractorRegistry registry = new ExtractorRegistry();
        ListTag list = tag.getList("Links", Tag.TAG_COMPOUND);
        for (Tag entry : list) {
            CompoundTag link = (CompoundTag) entry;
            UUID nodeId = link.getUUID("NodeId");
            BlockPos pos = BlockPos.of(link.getLong("ExtractorPos"));
            registry.nodeToExtractor.put(nodeId, pos);
            registry.positionToNode.put(pos.asLong(), nodeId);
        }
        return registry;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Map.Entry<UUID, BlockPos> entry : nodeToExtractor.entrySet()) {
            CompoundTag link = new CompoundTag();
            link.putUUID("NodeId", entry.getKey());
            link.putLong("ExtractorPos", entry.getValue().asLong());
            list.add(link);
        }
        tag.put("Links", list);
        return tag;
    }
}