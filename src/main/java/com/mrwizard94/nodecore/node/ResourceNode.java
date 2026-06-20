package com.mrwizard94.nodecore.node;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;
import java.util.UUID;

public final class ResourceNode {
    private final UUID id;
    private final NodeType type;
    private final BlockPos center;
    private final int radius;

    public ResourceNode(UUID id, NodeType type, BlockPos center, int radius) {
        this.id = Objects.requireNonNull(id, "id");
        this.type = Objects.requireNonNull(type, "type");
        this.center = Objects.requireNonNull(center, "center").immutable();
        this.radius = Math.max(8, radius);
    }

    public static ResourceNode create(NodeType type, BlockPos center, int radius) {
        return new ResourceNode(UUID.randomUUID(), type, center, radius);
    }

    public UUID getId() {
        return id;
    }

    public NodeType getType() {
        return type;
    }

    public BlockPos getCenter() {
        return center;
    }

    public int getRadius() {
        return radius;
    }

    public boolean contains(BlockPos pos) {
        return center.distSqr(pos) <= (long) radius * radius;
    }

    public double distanceTo(BlockPos pos) {
        return Math.sqrt(center.distSqr(pos));
    }

    public boolean isLush() {
        return type.isLush();
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("Id", id);
        tag.putString("Type", type.getId());
        tag.putLong("Center", center.asLong());
        tag.putInt("Radius", radius);
        return tag;
    }

    public static ResourceNode load(CompoundTag tag) {
        UUID id = tag.getUUID("Id");
        NodeType type = NodeType.byId(tag.getString("Type"));
        BlockPos center = BlockPos.of(tag.getLong("Center"));
        int radius = tag.getInt("Radius");
        return new ResourceNode(id, type, center, radius);
    }

    public void writeToBuffer(FriendlyByteBuf buf) {
        buf.writeUUID(id);
        buf.writeUtf(type.getId());
        buf.writeBlockPos(center);
        buf.writeVarInt(radius);
    }

    public static ResourceNode readFromBuffer(FriendlyByteBuf buf) {
        return new ResourceNode(
                buf.readUUID(),
                NodeType.byId(buf.readUtf()),
                buf.readBlockPos(),
                buf.readVarInt()
        );
    }
}