package com.mrwizard94.nodecore.data;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;
import java.util.UUID;

/**
 * Axis-aligned lush cavern region for 3D volume membership queries.
 */
public final class LushCavernVolume {
    private final UUID id;
    private final int minX;
    private final int minY;
    private final int minZ;
    private final int maxX;
    private final int maxY;
    private final int maxZ;

    public LushCavernVolume(UUID id, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.id = Objects.requireNonNull(id, "id");
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
        this.maxZ = Math.max(minZ, maxZ);
    }

    public static LushCavernVolume fromCenterRadii(UUID id, BlockPos center, int radiusX, int radiusY, int radiusZ) {
        return new LushCavernVolume(
                id,
                center.getX() - radiusX,
                center.getY() - radiusY,
                center.getZ() - radiusZ,
                center.getX() + radiusX,
                center.getY() + radiusY,
                center.getZ() + radiusZ
        );
    }

    public UUID getId() {
        return id;
    }

    public BlockPos center() {
        return new BlockPos((minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2);
    }

    public int horizontalRadius() {
        return Math.max(maxX - minX, maxZ - minZ) / 2;
    }

    public boolean contains(BlockPos pos) {
        return pos.getX() >= minX && pos.getX() <= maxX
                && pos.getY() >= minY && pos.getY() <= maxY
                && pos.getZ() >= minZ && pos.getZ() <= maxZ;
    }

    public boolean intersectsChunk(int chunkX, int chunkZ) {
        int chunkMinX = chunkX << 4;
        int chunkMaxX = chunkMinX + 15;
        int chunkMinZ = chunkZ << 4;
        int chunkMaxZ = chunkMinZ + 15;
        return maxX >= chunkMinX && minX <= chunkMaxX && maxZ >= chunkMinZ && minZ <= chunkMaxZ;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("Id", id);
        tag.putInt("MinX", minX);
        tag.putInt("MinY", minY);
        tag.putInt("MinZ", minZ);
        tag.putInt("MaxX", maxX);
        tag.putInt("MaxY", maxY);
        tag.putInt("MaxZ", maxZ);
        return tag;
    }

    public static LushCavernVolume load(CompoundTag tag) {
        return new LushCavernVolume(
                tag.getUUID("Id"),
                tag.getInt("MinX"),
                tag.getInt("MinY"),
                tag.getInt("MinZ"),
                tag.getInt("MaxX"),
                tag.getInt("MaxY"),
                tag.getInt("MaxZ")
        );
    }

    public void writeToBuffer(FriendlyByteBuf buf) {
        buf.writeUUID(id);
        buf.writeVarInt(minX);
        buf.writeVarInt(minY);
        buf.writeVarInt(minZ);
        buf.writeVarInt(maxX);
        buf.writeVarInt(maxY);
        buf.writeVarInt(maxZ);
    }

    public static LushCavernVolume readFromBuffer(FriendlyByteBuf buf) {
        return new LushCavernVolume(
                buf.readUUID(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt()
        );
    }
}